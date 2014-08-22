package xdi2.messaging.target.interceptor.impl.linkcontract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.AddressUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.util.MessagePolicyEvaluationContext;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, TargetInterceptor, Prototype<LinkContractInterceptor> {

	private static Logger log = LoggerFactory.getLogger(LinkContractInterceptor.class.getName());

	private Graph linkContractsGraph;
	private XDIAddress defaultLinkContractXri;

	public LinkContractInterceptor(Graph linkContractsGraph) {

		this.linkContractsGraph = linkContractsGraph;
		this.defaultLinkContractXri = null;
	}

	public LinkContractInterceptor() {

		this.linkContractsGraph = null;
		this.defaultLinkContractXri = null;
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

		// set the default link contract XRI

		interceptor.setDefaultLinkContractXri(this.getDefaultLinkContractXri());

		// done

		return interceptor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getLinkContractsGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setLinkContractsGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getLinkContractsGraph() == null) throw new Xdi2MessagingException("No link contracts graph.", null, null);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find the XDI link contract referenced by the message

		XDIAddress linkContractXri = message.getLinkContractXri();

		if (linkContractXri == null) linkContractXri = this.getDefaultLinkContractXri();

		if (linkContractXri == null) {

			if (log.isDebugEnabled()) log.debug("No link contract specified by message.");
			return InterceptorResult.DEFAULT;
		}

		ContextNode linkContractContextNode = this.getLinkContractsGraph().getDeepContextNode(linkContractXri, true);
		if (linkContractContextNode == null) {

			if (log.isDebugEnabled()) log.debug("No link contract context node found in graph.");
			return InterceptorResult.DEFAULT;
		}

		XdiEntity xdiEntity = XdiAbstractEntity.fromContextNode(linkContractContextNode);
		if (xdiEntity == null) {

			if (log.isDebugEnabled()) log.debug("No link contract entity found in graph.");
			return InterceptorResult.DEFAULT;
		}

		LinkContract linkContract = LinkContract.fromXdiEntity(xdiEntity);
		if (linkContract == null) {

			if (log.isDebugEnabled()) log.debug("No link contract found in graph.");
			return InterceptorResult.DEFAULT;
		}

		if (log.isDebugEnabled()) log.debug("Found link contract " + linkContract);

		putLinkContract(executionContext, linkContract);

		// evaluate the XDI policy against this message

		PolicyRoot policyRoot = linkContract.getPolicyRoot(false);
		if (policyRoot == null) return InterceptorResult.DEFAULT;

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(message, this.getLinkContractsGraph());

		if (! Boolean.TRUE.equals(policyRoot.evaluate(policyEvaluationContext))) {

			throw new Xdi2NotAuthorizedException("Link contract policy violation for message " + message.toString() + " in link contract " + linkContract.toString() + ".", null, executionContext);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// check permission on target address

		Boolean authorized = null;

		if (isSetOnDoAddress(targetAddress, operation)) {

			XDIAddress doTargetAddress = AddressUtil.subXri(targetAddress, 0, AddressUtil.indexOfXri(targetAddress, XDILinkContractConstants.XDI_ARC_DO));

			if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, doTargetAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
			}
		}

		if (authorized == null) {

			if (decideLinkContractPermission(operation.getOperationXri(), targetAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXri() + " permission on target address " + targetAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXri() + " permissions on target address " + targetAddress);
			}
		}

		// authorized?

		if (! Boolean.TRUE.equals(authorized)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXri() + " on target address: " + targetAddress, null, executionContext);
		}

		// done

		return targetAddress;
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// check permission on target statement

		XDIAddress targetAddress;

		if (targetStatement.isContextNodeStatement()) {

			targetAddress = targetStatement.getTargetContextNodeAddress();
		} else {

			targetAddress = targetStatement.getContextNodeAddress();
		}

		Boolean authorized = null;

		if (isSetOnDoAddress(targetAddress, operation)) {

			XDIAddress doTargetAddress = AddressUtil.subXri(targetAddress, 0, AddressUtil.indexOfXri(targetAddress, XDILinkContractConstants.XDI_ARC_DO));

			if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_DO, doTargetAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_DO + " permission on target address " + doTargetAddress);
			}
		}

		if (! Boolean.FALSE.equals(authorized)) {

			if (isSetOnRefRepStatement(targetStatement, operation)) {

				if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetStatement.getContextNodeAddress(), linkContract) && 
						decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetStatement.getTargetContextNodeAddress(), linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target addresses " + targetStatement.getContextNodeAddress() + " and " + targetStatement.getTargetContextNodeAddress());
				} else if (decideLinkContractPermission(XDILinkContractConstants.XDI_ADD_SET_REF, targetStatement, linkContract)) {

					authorized = Boolean.TRUE;
					if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + XDILinkContractConstants.XDI_ADD_SET_REF + " permission on target statement " + targetStatement);
				} else {

					authorized = Boolean.FALSE;
					if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + XDILinkContractConstants.XDI_ADD_SET_REF + " permissions on either target addresses " + targetStatement.getContextNodeAddress() + " and " + targetStatement.getTargetContextNodeAddress() + ", or target statement " + targetStatement);
				}
			}
		}

		if (authorized == null) {

			if (decideLinkContractPermission(operation.getOperationXri(), targetAddress, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXri() + " permission on target address " + targetAddress);
			} else if (decideLinkContractPermission(operation.getOperationXri(), targetStatement, linkContract)) {

				authorized = Boolean.TRUE;
				if (log.isDebugEnabled()) log.debug("Authorization succeeded, because of " + operation.getOperationXri() + " permission on target statement " + targetStatement);
			} else {

				authorized = Boolean.FALSE;
				if (log.isDebugEnabled()) log.debug("Authorization failed, because of missing " + operation.getOperationXri() + " permissions on either target address " + targetAddress + ", or target statement " + targetStatement);
			}
		}

		// authorized?

		if (! Boolean.TRUE.equals(authorized)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXri() + " on target statement: " + targetStatement, null, executionContext);
		}

		// done

		return targetStatement;
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

	public XDIAddress getDefaultLinkContractXri() {

		return this.defaultLinkContractXri;
	}

	public void setDefaultLinkContractXri(XDIAddress defaultLinkContractXri) {

		this.defaultLinkContractXri = defaultLinkContractXri;
	}

	/*
	 * Helper methods
	 */

	private static boolean isSetOnDoAddress(XDIAddress targetAddress, Operation operation) {

		if (! XDIMessagingConstants.XDI_ADD_SET.equals(operation.getOperationXri())) return false;
		if (AddressUtil.indexOfXri(targetAddress, XDILinkContractConstants.XDI_ARC_DO) != -1) return true;

		return false;
	}

	private static boolean isSetOnRefRepStatement(XDIStatement targetStatement, Operation operation) {

		if (! XDIMessagingConstants.XDI_ADD_SET.equals(operation.getOperationXri())) return false;
		if (! targetStatement.isRelationStatement()) return false;
		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getRelationAddress())) return true;
		if (XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getRelationAddress())) return true;

		return false;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionXri, XDIAddress contextNodeAddress, LinkContract linkContract) {

		// check positive permissions for the target address

		List<Iterator<? extends XDIAddress>> positiveIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		positiveIterators.add(linkContract.getPermissionTargetAddresses(permissionXri));
		positiveIterators.add(linkContract.getPermissionTargetAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> positiveIterator = new CompositeIterator<XDIAddress> (positiveIterators.iterator());

		int longestPositivePermission = -1;

		for (XDIAddress targetAddress : positiveIterator) {

			if (AddressUtil.startsWith(contextNodeAddress, targetAddress, false, true) != null) {

				int positiveMatch = targetAddress.getNumArcs();
				if (positiveMatch > longestPositivePermission) longestPositivePermission = positiveMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionXri + " on " + contextNodeAddress);
			}
		}

		// check negative permissions for the target address

		List<Iterator<? extends XDIAddress>> negativeIterators = new ArrayList<Iterator<? extends XDIAddress>> ();
		negativeIterators.add(linkContract.getNegativePermissionTargetAddresses(permissionXri));
		negativeIterators.add(linkContract.getNegativePermissionTargetAddresses(XDILinkContractConstants.XDI_ADD_ALL));
		CompositeIterator<XDIAddress> negativeIterator = new CompositeIterator<XDIAddress> (negativeIterators.iterator());

		int longestNegativePermission = -1;

		for (XDIAddress targetAddress : negativeIterator) {

			if (AddressUtil.startsWith(contextNodeAddress, targetAddress, false, true) != null) {

				int negativeMatch = targetAddress.getNumArcs();
				if (negativeMatch > longestNegativePermission) longestNegativePermission = negativeMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionXri + " on " + contextNodeAddress);
			}
		}

		// decide

		boolean decision = longestPositivePermission > longestNegativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionXri + " on address " + contextNodeAddress + ": " + decision);

		return decision;
	}

	private static boolean decideLinkContractPermission(XDIAddress permissionXri, XDIStatement statementXri, LinkContract linkContract) {

		// check positive permissions for the target statement

		boolean positivePermission = 
				linkContract.hasPermissionTargetStatement(permissionXri, statementXri) ||
				linkContract.hasPermissionTargetStatement(XDILinkContractConstants.XDI_ADD_ALL, statementXri);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + permissionXri + " on " + statementXri);
		
		// check negative permissions for the target statement

		boolean negativePermission = 
				linkContract.hasNegativePermissionTargetStatement(permissionXri, statementXri) ||
				linkContract.hasNegativePermissionTargetStatement(XDILinkContractConstants.XDI_ADD_ALL, statementXri);

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + permissionXri + " on " + statementXri);

		// decide

		boolean decision = positivePermission && ! negativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + permissionXri + " on statement " + statementXri + ": " + decision);

		return decision;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#linkcontractpermessage";

	public static LinkContract getLinkContract(ExecutionContext executionContext) {

		return (LinkContract) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	public static void putLinkContract(ExecutionContext executionContext, LinkContract linkContract) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}
}
