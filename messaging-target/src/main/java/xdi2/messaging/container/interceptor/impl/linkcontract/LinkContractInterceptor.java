package xdi2.messaging.container.interceptor.impl.linkcontract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.policy.PolicyRoot;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.messaging.Message;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.MessageInterceptor;
import xdi2.messaging.container.interceptor.OperationInterceptor;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.container.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.container.interceptor.impl.defer.DeferResultInterceptor;
import xdi2.messaging.container.interceptor.impl.push.PushInInterceptor;
import xdi2.messaging.container.interceptor.impl.util.MessagePolicyEvaluationContext;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.operations.SetOperation;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractInterceptor extends AbstractInterceptor<MessagingContainer> implements MessageInterceptor, OperationInterceptor, TargetInterceptor, Prototype<LinkContractInterceptor> {

	private static Logger log = LoggerFactory.getLogger(LinkContractInterceptor.class.getName());

	private Graph linkContractsGraph;

	public LinkContractInterceptor(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
	}

	public LinkContractInterceptor() {

		this.linkContractsGraph = null;
	}

	/*
	 * Prototype
	 */

	@Override
	public LinkContractInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// find the XDI link contract referenced by the message

		XDIAddress linkContractXDIAddress = message.getLinkContractXDIAddress();

		if (linkContractXDIAddress == null) {

			if (log.isDebugEnabled()) log.debug("No link contract specified by message.");
			return InterceptorResult.DEFAULT;
		}

		ContextNode linkContractContextNode = this.getLinkContractsGraph(executionContext).getDeepContextNode(linkContractXDIAddress, true);
		XdiEntity xdiEntity = linkContractContextNode == null ? null : XdiAbstractEntity.fromContextNode(linkContractContextNode);
		LinkContract linkContract = xdiEntity == null ? null : LinkContract.fromXdiEntity(xdiEntity);

		if (linkContract == null) throw new Xdi2MessagingException("Invalid link contract: " + linkContractXDIAddress, null, executionContext);

		if (log.isDebugEnabled()) log.debug("Found link contract " + linkContract);

		putLinkContract(executionContext, linkContract);

		// evaluate the XDI policy against this message

		PolicyRoot policyRoot = linkContract.getPolicyRoot(false);
		boolean policyRootResult = policyRoot == null ? true : this.evaluatePolicyRoot(message, policyRoot, executionContext);
		if (policyRoot != null) if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " policy evaluated to " + policyRootResult);

		if (policyRootResult) {

			putEvaluationResult(executionContext, XDIConstants.XDI_ADD_ROOT);
			return InterceptorResult.DEFAULT;
		}

		// evaluate the XDI defer policy against this message

		PolicyRoot deferPolicyRoot = linkContract.getDeferPolicyRoot(false);
		boolean deferPolicyRootResult = deferPolicyRoot == null ? false : this.evaluatePolicyRoot(message, deferPolicyRoot, executionContext);
		if (deferPolicyRoot != null) if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " defer policy evaluated to " + deferPolicyRootResult);

		if (deferPolicyRootResult) {

			DeferResultInterceptor.putDeferResult(executionContext, message, Boolean.FALSE);

			putEvaluationResult(executionContext, XDIPolicyConstants.XDI_ADD_DEFER);
			return InterceptorResult.DEFAULT;
		}

		// evaluate the XDI defer push policy against this message

		PolicyRoot deferPushPolicyRoot = linkContract.getDeferPushPolicyRoot(false);
		boolean deferPushPolicyRootResult = deferPushPolicyRoot == null ? false : this.evaluatePolicyRoot(message, deferPushPolicyRoot, executionContext);
		if (deferPushPolicyRoot != null) if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " defer push policy evaluated to " + deferPushPolicyRootResult);

		if (deferPushPolicyRootResult) {

			DeferResultInterceptor.putDeferResult(executionContext, message, Boolean.TRUE);

			putEvaluationResult(executionContext, XDIPolicyConstants.XDI_ADD_DEFER_PUSH);
			return InterceptorResult.DEFAULT;
		}

		// done

		throw new Xdi2NotAuthorizedException("Link contract policy violation for message " + message.toString() + " in link contract " + linkContract.toString() + ".", null, executionContext);
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// decide authorization on operation

		Boolean authorized = null;

		if (authorized == null && isConnect(operation)) authorized = decideConnectOperation(operation, linkContract, executionContext); 
		if (authorized == null && isSend(operation)) authorized = decideSendOperation(operation, linkContract, executionContext);
		if (authorized == null && isPush(operation)) authorized = decidePushOperation(operation, linkContract, executionContext);

		// authorized?

		if (authorized != null) handleAuthorizationResult(authorized, operation, executionContext);

		// done

		if (DeferResultInterceptor.hasDeferResult(executionContext, operation.getMessage())) return InterceptorResult.SKIP_MESSAGING_TARGET;

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIAddress targetAddress(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// check permission on target address

		Boolean authorized = null;

		if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

			authorized = Boolean.TRUE;
			if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
		} else {

			authorized = Boolean.FALSE;
			if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
		}

		if (Boolean.TRUE.equals(authorized)) {

			if (isSetOnDoAddress(targetXDIAddress, operation)) {

				XDIAddress doTargetAddress = XDIAddressUtil.subXDIAddress(targetXDIAddress, 0, XDIAddressUtil.indexOfXDIArc(targetXDIAddress, XDILinkContractConstants.XDI_ARC_CONTRACT));

				if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, doTargetAddress, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
				} else {

					authorized = Boolean.FALSE;
					if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
				}
			}
		}

		// authorized?

		handleAuthorizationResult(authorized, operation, executionContext);

		// done

		return targetXDIAddress;
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// determine target address

		XDIAddress targetXDIAddress = targetXDIAddressForTargetXDIStatement(targetXDIStatement);

		// check permission on target address

		Boolean authorized = null;

		if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

			authorized = Boolean.TRUE;
			if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
		} else if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIStatement, linkContract)) {

			authorized = Boolean.TRUE;
			if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target statement " + targetXDIStatement);
		} else {

			authorized = Boolean.FALSE;
			if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on either target address " + targetXDIAddress + ", or target statement " + targetXDIStatement);
		}

		if (Boolean.TRUE.equals(authorized) && isSetOnDoAddress(targetXDIAddress, operation)) {

			if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, targetXDIAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + targetXDIAddress);
			} else if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIStatement, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target statement " + targetXDIStatement);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + targetXDIAddress);
			}
		}

		if (Boolean.TRUE.equals(authorized) && isSetOnRefRepStatement(targetXDIStatement, operation)) {

			if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement.getContextNodeXDIAddress(), linkContract) && 
					decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement.getTargetXDIAddress(), linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target addresses " + targetXDIStatement.getContextNodeXDIAddress() + " and " + targetXDIStatement.getTargetXDIAddress());
			} else if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target statement " + targetXDIStatement);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_REF + " permissions on either target addresses " + targetXDIStatement.getContextNodeXDIAddress() + " and " + targetXDIStatement.getTargetXDIAddress() + ", or target statement " + targetXDIStatement);
			}
		}

		// authorized?

		handleAuthorizationResult(authorized, operation, executionContext);

		// done

		return targetXDIStatement;
	}

	/*
	 * Getters and setters
	 */

	public Graph getLinkContractsGraph(ExecutionContext executionContext) {

		Graph linkContractsGraph = this.getLinkContractsGraph();
		if (linkContractsGraph == null) linkContractsGraph = executionContext.getCurrentGraph();
		if (linkContractsGraph == null) throw new NullPointerException("No link contracts graph.");

		return linkContractsGraph;
	}

	public Graph getLinkContractsGraph() {

		return this.linkContractsGraph;
	}

	public void setLinkContractsGraph(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
	}

	/*
	 * Helper methods
	 */

	private boolean evaluatePolicyRoot(Message message, PolicyRoot policyRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(message, this.getLinkContractsGraph(executionContext));

		return policyRoot.evaluate(policyEvaluationContext);
	}

	private static XDIAddress targetXDIAddressForTargetXDIStatement(XDIStatement targetXDIStatement) {

		if (targetXDIStatement.isContextNodeStatement()) {

			return targetXDIStatement.getTargetXDIAddress();
		} else {

			return targetXDIStatement.getContextNodeXDIAddress();
		}
	}

	private static boolean isConnect(Operation operation) {

		return operation instanceof ConnectOperation;
	}

	private static boolean isSend(Operation operation) {

		return operation instanceof SendOperation;
	}

	private static boolean isPush(Operation operation) {

		return operation instanceof PushOperation;
	}

	private static boolean isSetOnDoAddress(XDIAddress targetXDIAddress, Operation operation) {

		if (! (operation instanceof SetOperation)) return false;
		if (XDIAddressUtil.indexOfXDIArc(targetXDIAddress, XDILinkContractConstants.XDI_ARC_CONTRACT) != -1) return true;

		return false;
	}

	private static boolean isSetOnRefRepStatement(XDIStatement targetXDIStatement, Operation operation) {

		if (! (operation instanceof SetOperation)) return false;
		if (! targetXDIStatement.isRelationStatement()) return false;
		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetXDIStatement.getRelationXDIAddress())) return true;
		if (XDIDictionaryConstants.XDI_ADD_REP.equals(targetXDIStatement.getRelationXDIAddress())) return true;

		return false;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionXDIAddress, XDIAddress targetXDIaddress, LinkContract linkContract) {

		// check positive permissions for the target address

		List<Iterator<? extends XDIAddress>> positiveIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		positiveIterators.add(linkContract.getPermissionTargetXDIAddresses(permissionXDIAddress));
		positiveIterators.add(linkContract.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> positiveIterator = new CompositeIterator<XDIAddress> (positiveIterators.iterator());

		int longestPositivePermission = -1;

		for (XDIAddress permissionTargetXDIAddress : positiveIterator) {

			if (XDIAddressUtil.startsWithXDIAddress(targetXDIaddress, permissionTargetXDIAddress, false, true) != null) {

				int positiveMatch = permissionTargetXDIAddress.getNumXDIArcs();
				if (positiveMatch > longestPositivePermission) longestPositivePermission = positiveMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionXDIAddress + " on " + targetXDIaddress);
			}
		}

		// check negative permissions for the target address

		List<Iterator<? extends XDIAddress>> negativeIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		negativeIterators.add(linkContract.getNegativePermissionTargetXDIAddresses(permissionXDIAddress));
		negativeIterators.add(linkContract.getNegativePermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> negativeIterator = new CompositeIterator<XDIAddress> (negativeIterators.iterator());

		int longestNegativePermission = -1;

		for (XDIAddress permissionTargetXDIAddress : negativeIterator) {

			if (XDIAddressUtil.startsWithXDIAddress(targetXDIaddress, permissionTargetXDIAddress, false, true) != null) {

				int negativeMatch = permissionTargetXDIAddress.getNumXDIArcs();
				if (negativeMatch > longestNegativePermission) longestNegativePermission = negativeMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionXDIAddress + " on " + targetXDIaddress);
			}
		}

		// decide

		boolean decision = longestPositivePermission > longestNegativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionXDIAddress + " on address " + targetXDIaddress + ": " + decision);

		return decision;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionXDIAddress, XDIStatement targetXDIstatement, LinkContract linkContract) {

		// check positive permissions for the target statement

		boolean positivePermission = 
				linkContract.hasPermissionTargetXDIStatement(permissionXDIAddress, targetXDIstatement) ||
				linkContract.hasPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_ALL, targetXDIstatement);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionXDIAddress + " on " + targetXDIstatement);

		// check negative permissions for the target statement

		boolean negativePermission = 
				linkContract.hasNegativePermissionTargetXDIStatement(permissionXDIAddress, targetXDIstatement) ||
				linkContract.hasNegativePermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_ALL, targetXDIstatement);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionXDIAddress + " on " + targetXDIstatement);

		// decide

		boolean decision = positivePermission && ! negativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionXDIAddress + " on statement " + targetXDIstatement + ": " + decision);

		return decision;
	}

	private static Boolean decideConnectOperation(Operation operation, LinkContract linkContract, ExecutionContext executionContext) {

		XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
		IterableIterator<XDIStatement> targetXDIStatements = operation.getTargetXDIStatements();

		// for target address: check permission on target address

		if (targetXDIAddress != null) {

			Boolean authorized;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization of $connect succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization of $connect failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			return authorized;
		}

		// for target statements: check permission on common root

		if (targetXDIStatements != null) {

			Boolean authorized = null;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), XDIConstants.XDI_ADD_ROOT, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization of $connect succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization of $connect failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			return authorized;
		}

		throw new IllegalArgumentException();
	}

	private static Boolean decideSendOperation(Operation operation, LinkContract linkContract, ExecutionContext executionContext) {

		XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
		IterableIterator<XDIStatement> targetXDIStatements = operation.getTargetXDIStatements();

		// for target address: check permission on target address

		if (targetXDIAddress != null) {

			Boolean authorized = null;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization of $send succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization of $send failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			return authorized;
		}

		// for target statements: check permission on common root

		if (targetXDIStatements != null) {

			Boolean authorized = null;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), XDIConstants.XDI_ADD_ROOT, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization of $send succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization of $send failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			return authorized;
		}

		throw new IllegalArgumentException();
	}

	// TODO: how exactly are incoming $push operations authorized?
	private static Boolean decidePushOperation(Operation operation, LinkContract linkContract, ExecutionContext executionContext) throws Xdi2MessagingException {

		// for target address and target statements: look at messages for PushInInterceptor

		AbstractMessagingContainer messagingContainer = (AbstractMessagingContainer) executionContext.getCurrentMessagingContainer();
		PushInInterceptor pushInInterceptor = messagingContainer.getInterceptors().getInterceptor(PushInInterceptor.class);
		if (pushInInterceptor == null) return Boolean.FALSE;

		List<Message> pushedMessages = pushInInterceptor.getPushedMessages(operation, executionContext);

		// targets of pushed operations must be authorized

		for (Message pushedMessage : pushedMessages) {

			for (Operation pushedOperation : pushedMessage.getOperations()) {

				XDIAddress targetXDIAddress = pushedOperation.getTargetXDIAddress();
				IterableIterator<XDIStatement> targetXDIStatements = pushedOperation.getTargetXDIStatements();

				// for target address: check permission on target address

				if (targetXDIAddress != null) {

					Boolean authorized = null;

					if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

						authorized = Boolean.TRUE;
						if (log.isDebugEnabled()) log.debug("Authorization of $push succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
					} else {

						authorized = Boolean.FALSE;
						if (log.isDebugEnabled()) log.debug("Authorization of $push failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
					}

					return authorized;
				}

				// for target statements: check permission on target statements

				if (targetXDIStatements != null) {

					for (XDIStatement targetXDIStatement : targetXDIStatements) {

						targetXDIAddress = targetXDIAddressForTargetXDIStatement(targetXDIStatement);

						Boolean authorized = null;

						if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

							authorized = Boolean.TRUE;
							if (log.isDebugEnabled()) log.debug("Authorization of $push succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
						} else {

							authorized = Boolean.FALSE;
							if (log.isDebugEnabled()) log.debug("Authorization of $push failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
						}

						return authorized;
					}
				}
			}
		}

		return Boolean.FALSE;
	}

	private static void handleAuthorizationResult(Boolean authorized, Operation operation, ExecutionContext executionContext) throws Xdi2NotAuthorizedException {

		if (authorized == null) throw new NullPointerException();

		if (! Boolean.TRUE.equals(authorized)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXDIAddress(), null, executionContext);
		}
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#linkcontractpermessage";
	private static final String EXECUTIONCONTEXT_KEY_EVALUATIONRESULT_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#evaluationresultpermessage";

	public static LinkContract getLinkContract(ExecutionContext executionContext) {

		return (LinkContract) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	public static void putLinkContract(ExecutionContext executionContext, LinkContract linkContract) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}

	public static XDIAddress getEvaluationResult(ExecutionContext executionContext) {

		return (XDIAddress) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_EVALUATIONRESULT_PER_MESSAGE);
	}

	public static void putEvaluationResult(ExecutionContext executionContext, XDIAddress evaluationResult) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_EVALUATIONRESULT_PER_MESSAGE, evaluationResult);
	}
}
