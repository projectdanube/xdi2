package xdi2.messaging.target.interceptor.impl;



import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Iterator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.Policy;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class LinkContractsInterceptor extends AbstractInterceptor implements MessageInterceptor, TargetInterceptor {

	private Graph linkContractsGraph;

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find out which link contract is referenced by this message, and store it in the execution context

		XRI3Segment linkContractXri = message.getLinkContractXri();
		ContextNode linkContractContextNode = (linkContractXri == null) ? null : this.linkContractsGraph.findContextNode(linkContractXri, false);
		LinkContract linkContract = (linkContractContextNode == null) ? null : LinkContract.fromContextNode(linkContractContextNode);

		if(linkContract != null){
		putLinkContract(executionContext, linkContract);
		}
		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return false;
	}

	private boolean checkLinkContractAuthorization(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2NotAuthorizedException{
		
		boolean operationAllowed = false , senderIsAssigned = false;
		XRI3Segment sender = operation.getSender();
		LinkContract linkContract = getLinkContract(executionContext);
		//check if sender has been assigned the link contract
		
		for(Iterator<ContextNode> iter = linkContract.getAssignees();iter.hasNext();){
			ContextNode assignee = iter.next();
			if(assignee.getXri().equals(sender)){
				senderIsAssigned = true;
				break;
			}
		}
		if(!senderIsAssigned){
			return operationAllowed;
		}
		XDILinkContractPermission lcPermission = null;
		if(GetOperation.isValid(operation.getRelation())){
			lcPermission = XDILinkContractPermission.LC_OP_GET;
		}
		else if(AddOperation.isValid(operation.getRelation())){
			lcPermission = XDILinkContractPermission.LC_OP_ADD;
		}
		else if(DelOperation.isValid(operation.getRelation())){
			lcPermission = XDILinkContractPermission.LC_OP_DEL;
		}
		else if(ModOperation.isValid(operation.getRelation())){
			lcPermission = XDILinkContractPermission.LC_OP_MOD;
		}

		//check if a $operation arc goes from the LinkContract node to the $targetStatement or one of it's parents
		// ...
		

		ContextNode targetNode = this.linkContractsGraph.findContextNode(targetAddress, false);
		
		if(targetNode != null){
			Iterator<ContextNode> nodesWithRequestedOp = linkContract.getNodesWithPermission( lcPermission);
			if(!nodesWithRequestedOp.hasNext()){
				nodesWithRequestedOp = linkContract.getNodesWithPermission( XDILinkContractPermission.LC_OP_ALL);
			}
			for(Iterator<ContextNode> iter = nodesWithRequestedOp;iter.hasNext();){
				ContextNode c = iter.next();
				//if the requested permission is given directly to the target node 
				if(c.getXri().equals(targetNode.getXri()) ){
					operationAllowed = true;
					break;
				}
				//if the requested permission is given to a node which is a parent of the target node
				ContextNode parentNodeOfTargetNode = targetNode.getContextNode();
				ContextNode tempTargetNode = targetNode;
				while(parentNodeOfTargetNode != null){
					if(c.getXri().equals(parentNodeOfTargetNode.getXri()) ){
						operationAllowed = true;
						break;
					}				
					tempTargetNode = parentNodeOfTargetNode;
					parentNodeOfTargetNode = tempTargetNode.getContextNode();
				}
				if(operationAllowed){
					break;
				}
			}
		}
		if(operationAllowed){
			operationAllowed = this.evaluatePolicyExpressions(linkContract,operation.getMessage());
		}
			
		
		return operationAllowed;
	}
	@Override
	public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, operation);

		
		XRI3Segment targetAddress = targetStatement.getSubject();

		
		if(!checkLinkContractAuthorization(operation,targetAddress, executionContext)){
			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.toString() + " on target statement:"+ targetStatement.toString(), null, operation);
		}
				
		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, operation);

		if(!checkLinkContractAuthorization(operation,targetAddress, executionContext)){
			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.toString() + " on target address:" + targetAddress.toString(), null, operation);
		}

		return targetAddress;
	}

	public Graph getLinkContractsGraph() {

		return this.linkContractsGraph;
	}

	public void setLinkContractsGraph(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
	}
	
	public boolean evaluatePolicyExpressions(LinkContract lc , Message message) {
		// If no policy is set then return true
		
		Policy lcPolicy = null;
		String policyExpression = "";
		boolean evalResult = false;
		if ((lcPolicy = lc.getPolicy(false)) == null) {
			return true;
		} else {
			Context cx = Context.enter();
			
			try {
				// evaluate the policy expression
				try {
					policyExpression = URLDecoder.decode(
							lcPolicy.getLiteralExpression(), "UTF-8");
				} catch (UnsupportedEncodingException unSupEx) {

				}
				if (policyExpression != null && !policyExpression.isEmpty()) {
					// Initialize the standard objects (Object, Function, etc.)
					// This must be done before scripts can be executed. Returns
					// a scope object that we use in later calls.
					Scriptable scope = cx.initStandardObjects();
					
					ScriptableObject.putProperty(scope,"linkContract",lc);
					ScriptableObject.putProperty(scope, "message", message);
					ScriptableObject.defineClass(scope, JSPolicyExpressionHelper.class);
					Object[] arg = { lc, message};
					Scriptable policyExpressionHelper = cx.newObject(scope, "JSPolicyExpressionHelper", arg);
				
					scope.put("myPolicyExpressionHelper", scope, policyExpressionHelper);
					cx.evaluateString(scope, policyExpression,
							"policyExpression", 1, null);

					// Now evaluate the string we've collected.
					Object fObj = scope.get("f", scope);
					Object functionArgs[] = {  };
					Function f = (Function) fObj;
					Object result = f.call(cx, scope, scope, functionArgs);

					if (result != null
							&& Context.toString(result).equals("true")) {
						evalResult = true;
					}

				} else {
					evalResult = true;
				}

			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			} finally {
			
				// Exit from the context.
				Context.exit();
			}
		}
		return evalResult;

	}
	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractsInterceptor.class.getCanonicalName() + "#linkcontractpermessage";

	private static LinkContract getLinkContract(ExecutionContext executionContext) {

		return (LinkContract) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	private static void putLinkContract(ExecutionContext executionContext, LinkContract linkContract) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}
}
