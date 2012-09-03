package xdi2.messaging.target.interceptor.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
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
import xdi2.messaging.util.JSPolicyExpressionHelper;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractsInterceptor extends AbstractInterceptor implements
MessageInterceptor, TargetInterceptor {

	private Graph linkContractsGraph;

	@Override
	public boolean before(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		// find out which link contract is referenced by this message, and store
		// it in the execution context

		XRI3Segment linkContractXri = message.getLinkContractXri();
		ContextNode linkContractContextNode = (linkContractXri == null) ? null
				: this.linkContractsGraph.findContextNode(linkContractXri,
						false);
		LinkContract linkContract = (linkContractContextNode == null) ? null
				: LinkContract.fromContextNode(linkContractContextNode, false);

		if (linkContract != null) {
			putLinkContract(executionContext, linkContract);
		}
		if ((linkContract != null)
				&& !this.evaluatePolicyExpressions(linkContract, message)) {
			throw new Xdi2NotAuthorizedException(
					"Link contract policy expression violation for message",
					null, null);
		}
		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return false;
	}


	private static boolean checkLinkContractAuthorization(Operation operation,
			XRI3Segment targetAddress, ExecutionContext executionContext)
					throws Xdi2NotAuthorizedException {

		boolean operationAllowed = false, senderIsAssigned = false;
		XRI3Segment sender = operation.getSender();
		LinkContract linkContract = getLinkContract(executionContext);
		// check if sender has been assigned the link contract

		for (Iterator<ContextNode> iter = linkContract.getAssignees(); iter
				.hasNext();) {
			ContextNode assignee = iter.next();
			if (assignee.getXri().equals(sender)) {
				senderIsAssigned = true;
				break;
			}
		}
		if (!senderIsAssigned) {
			return operationAllowed;
		}
		XDILinkContractPermission lcPermission = null;
		if (GetOperation.isValid(operation.getRelation())) {
			lcPermission = XDILinkContractPermission.LC_OP_GET;
		} else if (AddOperation.isValid(operation.getRelation())) {
			lcPermission = XDILinkContractPermission.LC_OP_ADD;
		} else if (DelOperation.isValid(operation.getRelation())) {
			lcPermission = XDILinkContractPermission.LC_OP_DEL;
		} else if (ModOperation.isValid(operation.getRelation())) {
			lcPermission = XDILinkContractPermission.LC_OP_MOD;
		}
		//check if an address covered by a link contract is a parent of the target address of the operation
		int subSegArrayLen = targetAddress.getNumSubSegments()+1;
		String [] subSegArray = new String[subSegArrayLen];
		subSegArray[0] = "()";
		String subSegStr = "";
		for(int i = 1 ; i < subSegArrayLen ; i++){
			subSegStr += targetAddress.getSubSegment(i-1).toString();
			subSegArray[i] = subSegStr;
		}
		for (int i = 0 ; i < subSegArray.length ; i++) {
			String subseg = subSegArray[i];

			Iterator<ContextNode> nodesWithRequestedOp = linkContract
					.getNodesWithPermission(lcPermission);

			Iterator<ContextNode> nodesWithRequestedAllOp = linkContract
					.getNodesWithPermission(XDILinkContractPermission.LC_OP_ALL);

			for  (Iterator<ContextNode> iter = nodesWithRequestedOp; iter.hasNext();){ 
				ContextNode c = iter.next(); 
				if(c.getXri().equals(subseg)){
					operationAllowed = true;
					return operationAllowed;
				}
			}
			for  (Iterator<ContextNode> iter = nodesWithRequestedAllOp; iter.hasNext();){ 
				ContextNode c = iter.next(); 
				if(c.getXri().equals(subseg)){
					operationAllowed = true;
					return operationAllowed;
				}
			}

		}
		return operationAllowed;
	}

	@Override
	public Statement targetStatement(Statement targetStatement, Operation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null)
			throw new Xdi2MessagingException("No link contract.", null,
					executionContext);

		XRI3Segment targetAddress = targetStatement.getSubject();

		if (!checkLinkContractAuthorization(operation, targetAddress,
				executionContext)) {
			throw new Xdi2NotAuthorizedException(
					"Link contract violation for operation: "
							+ operation.toString() + " on target statement:"
							+ targetStatement.toString(), null, executionContext);
		}
		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(XRI3Segment targetAddress, Operation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null)
			throw new Xdi2MessagingException("No link contract.", null,
					executionContext);

		if (!checkLinkContractAuthorization(operation, targetAddress,
				executionContext)) {
			throw new Xdi2NotAuthorizedException(
					"Link contract violation for operation: "
							+ operation.toString() + " on target address:"
							+ targetAddress.toString(), null, executionContext);
		}

		return targetAddress;
	}

	public Graph getLinkContractsGraph() {

		return this.linkContractsGraph;
	}

	public void setLinkContractsGraph(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
	}

	public boolean evaluatePolicyExpressions(LinkContract lc, Message message) {
		// If no policy is set then return true

		Policy lcPolicy = null;

		boolean evalResult = false;
		if ((lcPolicy = lc.getPolicy(false)) == null) {
			return true;
		} else {
			if (lcPolicy.getPolicyExpressionComponent() != null) {
				Context cx = Context.enter();
				Scriptable scope = cx.initStandardObjects();
				try {
					ScriptableObject.defineClass(scope,
							JSPolicyExpressionHelper.class);
					Object[] arg = {};
					Scriptable policyExpressionHelper = cx.newObject(scope,
							"JSPolicyExpressionHelper", arg);

					scope.put("xdi", scope, policyExpressionHelper);

				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scope.put("linkContract", scope, lc);
				scope.put("message", scope, message);
				evalResult = lcPolicy.getPolicyExpressionComponent().evaluate(
						cx, scope);
				Context.exit();
			} else {
				evalResult = true;
			}
		}
		// JSPolicyExpressionUtil.cleanup();

		return evalResult;

	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractsInterceptor.class
			.getCanonicalName() + "#linkcontractpermessage";

	private static LinkContract getLinkContract(
			ExecutionContext executionContext) {

		return (LinkContract) executionContext
				.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	private static void putLinkContract(ExecutionContext executionContext,
			LinkContract linkContract) {

		executionContext.putMessageAttribute(
				EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}
}
