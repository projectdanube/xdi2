package xdi2.messaging.target.interceptor.impl.linkcontract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.util.MessagePolicyEvaluationContext;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractInterceptor extends AbstractInterceptor implements MessageInterceptor, TargetInterceptor, Prototype<LinkContractInterceptor> {

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
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find the XDI link contract referenced by the message

		XDI3Segment linkContractXri = message.getLinkContractXri();
		if (linkContractXri == null) return false;

		ContextNode linkContractContextNode = this.getLinkContractsGraph().getDeepContextNode(linkContractXri);
		if (linkContractContextNode == null) return false;

		XdiEntity xdiEntity = XdiAbstractEntity.fromContextNode(linkContractContextNode);
		if (xdiEntity == null) return false;

		LinkContract linkContract = LinkContract.fromXdiEntity(xdiEntity);
		if (linkContract == null) return false;

		if (log.isDebugEnabled()) log.debug("Found link contract " + linkContract);

		putLinkContract(executionContext, linkContract);

		// evaluate the XDI policy against this message

		PolicyRoot policyRoot = linkContract.getPolicyRoot(false);
		if (policyRoot == null) return false;

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(message, this.getLinkContractsGraph());

		if (! Boolean.TRUE.equals(policyRoot.evaluate(policyEvaluationContext))) {

			throw new Xdi2NotAuthorizedException("Link contract policy violation for message " + message.toString() + " in link contract " + linkContract.toString() + ".", null, executionContext);
		}

		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return false;
	}

	/*
	 * TargetInterceptor
	 */

	private static boolean checkLinkContractAuthorization(Operation operation, XDI3Segment contextNodeXri, LinkContract linkContract) {

		// check positive permissions for the target address

		List<Iterator<? extends XDI3Segment>> positiveterators = new ArrayList<Iterator<? extends XDI3Segment>> ();
		positiveterators.add(linkContract.getPermissionTargetAddresses(operation.getOperationXri()));
		positiveterators.add(linkContract.getPermissionTargetAddresses(XDILinkContractConstants.XRI_S_ALL));
		CompositeIterator<XDI3Segment> positiveIterator = new CompositeIterator<XDI3Segment> (positiveterators.iterator());

		int longestPositivePermission = -1;

		for (XDI3Segment targetAddress : positiveIterator) {

			if (XDI3Util.startsWith(contextNodeXri, targetAddress) != null) {

				int positiveMatch = targetAddress.equals(XDIConstants.XRI_S_ROOT) ? 0 : targetAddress.getNumSubSegments();
				if (positiveMatch > longestPositivePermission) longestPositivePermission = positiveMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " allows " + operation.getOperationXri() + " on " + contextNodeXri);
			}
		}

		// check negative permissions for the target address

		List<Iterator<? extends XDI3Segment>> negativeIterators = new ArrayList<Iterator<? extends XDI3Segment>> ();
		negativeIterators.add(linkContract.getNegativePermissionTargetAddresses(operation.getOperationXri()));
		negativeIterators.add(linkContract.getNegativePermissionTargetAddresses(XDILinkContractConstants.XRI_S_ALL));
		CompositeIterator<XDI3Segment> negativeIterator = new CompositeIterator<XDI3Segment> (negativeIterators.iterator());

		int longestNegativePermission = -1;

		for (XDI3Segment targetAddress : negativeIterator) {

			if (XDI3Util.startsWith(contextNodeXri, targetAddress) != null) {

				int negativeMatch = targetAddress.equals(XDIConstants.XRI_S_ROOT) ? 0 : targetAddress.getNumSubSegments();
				if (negativeMatch > longestNegativePermission) longestNegativePermission = negativeMatch;

				if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " does not allow " + operation.getOperationXri() + " on " + contextNodeXri);
			}
		}

		// decide

		boolean decision = longestPositivePermission > longestNegativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + operation.getOperationXri() + " on " + contextNodeXri + ": " + decision);

		return decision;
	}

	private static boolean checkLinkContractAuthorization(Operation operation, XDI3Statement statementXri, LinkContract linkContract) {

		// check positive permissions for the target statement

		boolean positivePermission = linkContract.hasPermissionTargetStatement(operation.getOperationXri(), statementXri);

		// check negative permissions for the target statement

		boolean negativePermission = linkContract.hasNegativePermissionTargetStatement(operation.getOperationXri(), statementXri);

		// decide

		boolean decision = positivePermission && ! negativePermission;

		// done

		if (log.isDebugEnabled()) log.debug("Link contract " + linkContract + " decision for " + operation.getOperationXri() + " on " + statementXri + ": " + decision);

		return decision;
	}

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// check permission on target address

		boolean authorized = checkLinkContractAuthorization(operation, targetAddress, linkContract);

		if (! authorized) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXri() + " on target address: " + targetAddress, null, executionContext);
		}

		// done

		return targetAddress;
	}

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		// check permission on target address and target statement

		XDI3Segment targetAddress;

		if (targetStatement.isContextNodeStatement()) 
			targetAddress = targetStatement.getTargetContextNodeXri();
		else
			targetAddress = targetStatement.getContextNodeXri();

		boolean authorized = checkLinkContractAuthorization(operation, targetAddress, linkContract);
		authorized = authorized || checkLinkContractAuthorization(operation, targetStatement, linkContract);

		if (! authorized) {

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
