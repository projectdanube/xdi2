package xdi2.messaging.target.interceptor.impl.linkcontract;

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
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.policy.PolicyRoot;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphAware;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.messaging.Message;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.target.interceptor.impl.push.PushInInterceptor;
import xdi2.messaging.target.interceptor.impl.push.PushResultInterceptor;
import xdi2.messaging.target.interceptor.impl.push.PushResultInterceptor.PushResult;
import xdi2.messaging.target.interceptor.impl.util.MessagePolicyEvaluationContext;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractInterceptor extends AbstractInterceptor<MessagingTarget> implements GraphAware, MessageInterceptor, OperationInterceptor, TargetInterceptor, Prototype<LinkContractInterceptor> {

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

		// create new interceptor

		LinkContractInterceptor interceptor = new LinkContractInterceptor();

		// set the graph

		interceptor.setLinkContractsGraph(this.getLinkContractsGraph());

		// done

		return interceptor;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getLinkContractsGraph() == null) this.setLinkContractsGraph(graph);
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

		ContextNode linkContractContextNode = this.getLinkContractsGraph().getDeepContextNode(linkContractXDIAddress, true);
		XdiEntity xdiEntity = linkContractContextNode == null ? null : XdiAbstractEntity.fromContextNode(linkContractContextNode);
		LinkContract linkContract = xdiEntity == null ? null : LinkContract.fromXdiEntity(xdiEntity);

		if (linkContract == null) throw new Xdi2MessagingException("Invalid link contract: " + linkContractXDIAddress, null, executionContext);

		if (log.isDebugEnabled()) log.debug("Found link contract " + linkContract);

		putLinkContract(executionContext, linkContract);

		// evaluate the XDI policy against this message

		PolicyRoot policyRoot = linkContract.getPolicyRoot(false);
		boolean policyRootResult = policyRoot == null ? true : this.evaluatePolicyRoot(message, policyRoot);
		if (policyRoot != null) if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " policy evaluated to " + policyRootResult);

		if (policyRootResult) {

			putPushFlag(executionContext, Boolean.FALSE);
			return InterceptorResult.DEFAULT;
		}

		// evaluate the XDI push policy against this message

		PolicyRoot pushPolicyRoot = linkContract.getPushPolicyRoot(false);
		boolean pushPolicyRootResult = pushPolicyRoot == null ? false : this.evaluatePolicyRoot(message, pushPolicyRoot);
		if (pushPolicyRoot != null) if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " push policy evaluated to " + pushPolicyRootResult);

		if (pushPolicyRootResult) {

			putPushFlag(executionContext, Boolean.TRUE);
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

		// read the referenced link contract and push required flag from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		Boolean pushFlag = getPushFlag(executionContext);
		if (pushFlag == null) throw new Xdi2MessagingException("No push flag.", null, executionContext);

		// check permission on $connect operation

		if (isConnect(operation)) {

			// look at target address for ConnectInterceptor

			XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
			if (targetXDIAddress == null) targetXDIAddress = XDIConstants.XDI_ADD_ROOT;

			// check permission on target address

			Boolean authorized = null;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			// handle result

			handleEvaluationResult(authorized, pushFlag, targetXDIAddress, operation, executionContext);
		}

		// check permission on $send operation

		if (isSend(operation)) {


			// look at target address for ConnectInterceptor

			XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
			if (targetXDIAddress == null) targetXDIAddress = XDIConstants.XDI_ADD_ROOT;

			// check permission on target address

			Boolean authorized = null;

			if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
			}

			// handle result

			handleEvaluationResult(authorized, pushFlag, targetXDIAddress, operation, executionContext);
		}

		// check permission on $push operation

		if (isPush(operation)) {

			// TODO: how exactly are incoming $push operations authorized?

			// look at messages for PushInInterceptor

			AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

			PushInInterceptor pushInInterceptor = messagingTarget.getInterceptors().getInterceptor(PushInInterceptor.class);
			if (pushInInterceptor == null) return InterceptorResult.DEFAULT;

			List<Message> pushedMessages = pushInInterceptor.getPushedMessages(operation, executionContext);

			for (Message pushedMessage : pushedMessages) {

				for (Operation pushedOperation : pushedMessage.getOperations()) {

					XDIAddress targetXDIAddress = pushedOperation.getTargetXDIAddress();
					IterableIterator<XDIStatement> targetXDIStatements = pushedOperation.getTargetXDIStatements();

					// check permission on target address

					if (targetXDIAddress != null) {

						Boolean authorized = null;

						if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

							authorized = Boolean.TRUE;
							if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
						} else {

							authorized = Boolean.FALSE;
							if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
						}

						// handle result

						handleEvaluationResult(authorized, pushFlag, targetXDIAddress, operation, executionContext);
					}

					// check permissions on target statements

					if (targetXDIStatements != null) {

						for (XDIStatement targetXDIStatement : targetXDIStatements) {

							targetXDIAddress = targetXDIAddressForTargetXDIStatement(targetXDIStatement);

							Boolean authorized = null;

							if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIAddress, linkContract)) {

								authorized = Boolean.TRUE;
								if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXDIAddress() + " permission on target address " + targetXDIAddress);
							} else {

								authorized = Boolean.FALSE;
								if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXDIAddress() + " permissions on target address " + targetXDIAddress);
							}

							// handle result

							handleEvaluationResult(authorized, pushFlag, targetXDIAddress, operation, executionContext);
						}
					}
				}
			}
		}

		// done

		return Boolean.TRUE.equals(pushFlag) ? InterceptorResult.SKIP_SIBLING_INTERCEPTORS_AND_MESSAGING_TARGET : InterceptorResult.DEFAULT;
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

		// read the referenced link contract and push required flag from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		Boolean pushFlag = getPushFlag(executionContext);
		if (pushFlag == null) throw new Xdi2MessagingException("No push flag.", null, executionContext);

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

				XDIAddress doTargetAddress = XDIAddressUtil.subXDIAddress(targetXDIAddress, 0, XDIAddressUtil.indexOfXDIArc(targetXDIAddress, XDILinkContractConstants.XDI_ARC_DO));

				if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, doTargetAddress, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_DO + " succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
				} else {

					authorized = Boolean.FALSE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_DO + " failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
				}
			}
		}

		// handle result

		handleEvaluationResult(authorized, pushFlag, targetXDIAddress, operation, executionContext);

		// done

		return targetXDIAddress;
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		Boolean pushFlag = getPushFlag(executionContext);
		if (pushFlag == null) throw new Xdi2MessagingException("No push flag.", null, executionContext);

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

		if (Boolean.TRUE.equals(authorized)) {

			if (isSetOnDoAddress(targetXDIAddress, operation)) {

				if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, targetXDIAddress, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_DO + " succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + targetXDIAddress);
				} else if (decideLinkContractPermission(operation.getOperationXDIAddress(), targetXDIStatement, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_DO + " succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target statement " + targetXDIStatement);
				} else {

					authorized = Boolean.FALSE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_DO + " failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + targetXDIAddress);
				}
			}
		}

		if (Boolean.TRUE.equals(authorized)) {

			if (isSetOnRefRepStatement(targetXDIStatement, operation)) {

				if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement.getContextNodeXDIAddress(), linkContract) && 
						decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement.getTargetXDIAddress(), linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_REF + " succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target addresses " + targetXDIStatement.getContextNodeXDIAddress() + " and " + targetXDIStatement.getTargetXDIAddress());
				} else if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIStatement, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_REF + " succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target statement " + targetXDIStatement);
				} else {

					authorized = Boolean.FALSE;
					if (log.isDebugEnabled()) log.debug("Authorization for " + XDILinkContractConstants.XDI_ADD_SET_REF + " failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_REF + " permissions on either target addresses " + targetXDIStatement.getContextNodeXDIAddress() + " and " + targetXDIStatement.getTargetXDIAddress() + ", or target statement " + targetXDIStatement);
				}
			}
		}

		// handle result

		handleEvaluationResult(authorized, pushFlag, targetXDIStatement, operation, executionContext);

		// done

		return targetXDIStatement;
	}

	/*
	 * Getters and setters
	 */

	public Graph getLinkContractsGraph() {

		return this.linkContractsGraph;
	}

	public void setLinkContractsGraph(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
	}

	/*
	 * Helper methods
	 */

	private boolean evaluatePolicyRoot(Message message, PolicyRoot policyRoot) {

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(message, this.getLinkContractsGraph());

		return policyRoot.evaluate(policyEvaluationContext);
	}

	private static XDIAddress targetXDIAddressForTargetXDIStatement(XDIStatement targetXDIStatement) {

		if (targetXDIStatement.isContextNodeStatement()) {

			return targetXDIStatement.getTargetXDIAddress();
		} else {

			return targetXDIStatement.getContextNodeXDIAddress();
		}
	}

	private static void handleEvaluationResult(Boolean authorized, Boolean pushRequired, XDIAddress targetXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2NotAuthorizedException {

		// authorized?

		if (! Boolean.TRUE.equals(authorized)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXDIAddress() + " on target address: " + targetXDIAddress, null, executionContext);
		}

		// push required?

		if (Boolean.TRUE.equals(pushRequired)) {

			PushResult pushResult = new PushResult(targetXDIAddress);
			PushResultInterceptor.addOperationPushResult(executionContext, operation, pushResult);
		}
	}

	private static void handleEvaluationResult(Boolean authorized, Boolean pushFlag, XDIStatement targetXDIStatement, Operation operation, ExecutionContext executionContext) throws Xdi2NotAuthorizedException {

		// authorized?

		if (! Boolean.TRUE.equals(authorized)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXDIAddress() + " on target statement: " + targetXDIStatement, null, executionContext);
		}

		// push flag?

		if (Boolean.TRUE.equals(pushFlag)) {

			PushResult pushResult = new PushResult(targetXDIStatement);
			PushResultInterceptor.addOperationPushResult(executionContext, operation, pushResult);
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

	private static boolean isSetOnDoAddress(XDIAddress targetAddress, Operation operation) {

		if (! (operation instanceof SetOperation)) return false;
		if (XDIAddressUtil.indexOfXDIArc(targetAddress, XDILinkContractConstants.XDI_ARC_DO) != -1) return true;

		return false;
	}

	private static boolean isSetOnRefRepStatement(XDIStatement targetStatement, Operation operation) {

		if (! (operation instanceof SetOperation)) return false;
		if (! targetStatement.isRelationStatement()) return false;
		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getRelationXDIAddress())) return true;
		if (XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getRelationXDIAddress())) return true;

		return false;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionAddress, XDIAddress contextNodeXDIAddress, LinkContract linkContract) {

		// check positive permissions for the target address

		List<Iterator<? extends XDIAddress>> positiveIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		positiveIterators.add(linkContract.getPermissionTargetXDIAddresses(permissionAddress));
		positiveIterators.add(linkContract.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> positiveIterator = new CompositeIterator<XDIAddress> (positiveIterators.iterator());

		int longestPositivePermission = -1;

		for (XDIAddress targetAddress : positiveIterator) {

			if (XDIAddressUtil.startsWithXDIAddress(contextNodeXDIAddress, targetAddress, false, true) != null) {

				int positiveMatch = targetAddress.getNumXDIArcs();
				if (positiveMatch > longestPositivePermission) longestPositivePermission = positiveMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionAddress + " on " + contextNodeXDIAddress);
			}
		}

		// check negative permissions for the target address

		List<Iterator<? extends XDIAddress>> negativeIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		negativeIterators.add(linkContract.getNegativePermissionTargetXDIAddresses(permissionAddress));
		negativeIterators.add(linkContract.getNegativePermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> negativeIterator = new CompositeIterator<XDIAddress> (negativeIterators.iterator());

		int longestNegativePermission = -1;

		for (XDIAddress targetAddress : negativeIterator) {

			if (XDIAddressUtil.startsWithXDIAddress(contextNodeXDIAddress, targetAddress, false, true) != null) {

				int negativeMatch = targetAddress.getNumXDIArcs();
				if (negativeMatch > longestNegativePermission) longestNegativePermission = negativeMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionAddress + " on " + contextNodeXDIAddress);
			}
		}

		// decide

		boolean decision = longestPositivePermission > longestNegativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionAddress + " on address " + contextNodeXDIAddress + ": " + decision);

		return decision;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionAddress, XDIStatement XDIstatement, LinkContract linkContract) {

		// check positive permissions for the target statement

		boolean positivePermission = 
				linkContract.hasPermissionTargetXDIStatement(permissionAddress, XDIstatement) ||
				linkContract.hasPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_ALL, XDIstatement);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionAddress + " on " + XDIstatement);

		// check negative permissions for the target statement

		boolean negativePermission = 
				linkContract.hasNegativePermissionTargetXDIStatement(permissionAddress, XDIstatement) ||
				linkContract.hasNegativePermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_ALL, XDIstatement);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionAddress + " on " + XDIstatement);

		// decide

		boolean decision = positivePermission && ! negativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionAddress + " on statement " + XDIstatement + ": " + decision);

		return decision;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#linkcontractpermessage";
	private static final String EXECUTIONCONTEXT_KEY_PUSHFLAG_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#pushflagpermessage";

	public static LinkContract getLinkContract(ExecutionContext executionContext) {

		return (LinkContract) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	public static void putLinkContract(ExecutionContext executionContext, LinkContract linkContract) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}

	public static Boolean getPushFlag(ExecutionContext executionContext) {

		return (Boolean) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_PUSHFLAG_PER_MESSAGE);
	}

	public static void putPushFlag(ExecutionContext executionContext, Boolean pushFlag) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_PUSHFLAG_PER_MESSAGE, pushFlag);
	}
}
