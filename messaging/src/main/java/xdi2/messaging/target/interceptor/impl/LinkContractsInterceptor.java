package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.features.linkcontracts.LinkContract;
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
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class LinkContractsInterceptor implements MessageInterceptor, TargetInterceptor {

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

	private void checkLinkContractAuthorization(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2NotAuthorizedException{
		
		boolean operationAllowed = false , senderIsAssigned = false;
		XRI3Segment sender = operation.getSender();
		LinkContract linkContract = getLinkContract(executionContext);
		//check if sender has been assigned the link contract
		List<ContextNode> assignees = linkContract.getAssignees();
		for(Iterator<ContextNode> iter = assignees.iterator();iter.hasNext();){
			ContextNode assignee = iter.next();
			if(assignee.toString().equalsIgnoreCase(sender.toString())){
				senderIsAssigned = true;
				break;
			}
		}
		if(!senderIsAssigned){
			throw new Xdi2NotAuthorizedException("Not authorized:  " + operation.getOperationXri() + " on statement " + targetAddress, null, operation);
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
			List<ContextNode> nodesWithRequestedOp = linkContract.getNodesWithPermission( lcPermission);
			if(nodesWithRequestedOp == null || nodesWithRequestedOp.isEmpty()){
				nodesWithRequestedOp = linkContract.getNodesWithPermission( XDILinkContractPermission.LC_OP_ALL);
			}
			for(Iterator<ContextNode> iter = nodesWithRequestedOp.iterator();iter.hasNext();){
				ContextNode c = iter.next();
				if(c.toString().equalsIgnoreCase(targetNode.toString()) || c.containsContextNode(targetNode.getArcXri()) ){
					operationAllowed = true;
					break;
				}
				
			}
		}
		if(!operationAllowed){
			throw new Xdi2NotAuthorizedException("Not authorized:  " + operation.getOperationXri() + " on statement " + targetAddress, null, operation);
		}
	}
	@Override
	public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, operation);

		
		XRI3Segment targetAddress = targetStatement.getSubject();

		//if (Math.random() > 0.5f) throw new Xdi2NotAuthorizedException("Not authorized:  " + operation.getOperationXri() + " on statement " + targetStatement, null, operation);
		
		// done
		checkLinkContractAuthorization(operation,targetAddress, executionContext);
		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, operation);

		// check if the current operation and target statement are allowed under this link contract
		checkLinkContractAuthorization(operation,targetAddress, executionContext);

		// ...

		//if (Math.random() > 0.5f) throw new Xdi2NotAuthorizedException("Not authorized:  " + operation.getOperationXri() + " on address " + targetAddress, null, operation);
		
		

		// done

		return targetAddress;
	}

	public Graph getLinkContractsGraph() {

		return this.linkContractsGraph;
	}

	public void setLinkContractsGraph(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
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
