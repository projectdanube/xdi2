package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor enforces link contracts while a message is executed.
 * 
 * @author animesh
 */
public class LinkContractInterceptor extends AbstractInterceptor implements MessageInterceptor, TargetInterceptor, Prototype<LinkContractInterceptor> {

	private static Logger log = LoggerFactory.getLogger(LinkContractInterceptor.class.getName());

	private Graph linkContractsGraph;

	/*
	 * Prototype
	 */

	@Override
	public LinkContractInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		LinkContractInterceptor interceptor = new LinkContractInterceptor();

		// set the link contracts graph

		if (this.linkContractsGraph == null && prototypingContext.getMessagingTarget() instanceof GraphMessagingTarget) {

			interceptor.setLinkContractsGraph(((GraphMessagingTarget) prototypingContext.getMessagingTarget()).getGraph());
		}

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find the XDI link contract referenced by the message

		XDI3Segment linkContractXri = message.getLinkContractXri();
		if (linkContractXri == null) return false;

		ContextNode linkContractContextNode = this.getLinkContractsGraph().findContextNode(linkContractXri, false);
		if (linkContractContextNode == null) return false;

		LinkContract linkContract = LinkContract.fromContextNode(linkContractContextNode);
		if (linkContract == null) return false;

		putLinkContract(executionContext, linkContract);

		// evaluate the XDI policy against this message

		PolicyRoot policyRoot = linkContract.getPolicyRoot(false);
		if (policyRoot == null) return false;

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(this.getLinkContractsGraph(), message);

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

	private static boolean checkLinkContractAuthorization(Operation operation, XDI3Segment contextNodeXri, ExecutionContext executionContext) {

		LinkContract linkContract = getLinkContract(executionContext);

		// check if the link contract covers the context node XRI

		for (Iterator<ContextNode> contextNodes = linkContract.getNodesWithPermission(operation.getOperationXri()); contextNodes.hasNext(); ) {

			ContextNode contextNode = contextNodes.next();

			if (contextNode.isRootContextNode() || XRIUtil.startsWith(contextNodeXri, contextNode.getXri())) {

				log.debug("Link contract " + linkContract + " allows " + operation.getOperationXri() + " on " + contextNodeXri);
				return true;
			}
		}

		for (Iterator<ContextNode> contextNodes = linkContract.getNodesWithPermission(XDILinkContractConstants.XRI_S_ALL); contextNodes.hasNext(); ) {

			ContextNode contextNode = contextNodes.next();

			if (contextNode.isRootContextNode() || XRIUtil.startsWith(contextNodeXri, contextNode.getXri())) {

				log.debug("Link contract " + linkContract + " allows " + operation.getOperationXri() + " on " + contextNodeXri);
				return true;
			}
		}

		// done

		log.debug("Link contract " + linkContract + " does not allow " + operation.getOperationXri() + " on " + contextNodeXri);
		return false;
	}

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		XDI3Segment contextNodeXri = targetStatement.getContextNodeXri();

		if (! checkLinkContractAuthorization(operation, contextNodeXri, executionContext)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXri() + " on target statement: " + targetStatement, null, executionContext);
		}

		// done

		return targetStatement;
	}

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// read the referenced link contract from the execution context

		LinkContract linkContract = getLinkContract(executionContext);
		if (linkContract == null) throw new Xdi2MessagingException("No link contract.", null, executionContext);

		XDI3Segment contextNodeXri = targetAddress;

		if (! checkLinkContractAuthorization(operation, contextNodeXri, executionContext)) {

			throw new Xdi2NotAuthorizedException("Link contract violation for operation: " + operation.getOperationXri() + " on target address: " + targetAddress, null, executionContext);
		}

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

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE = LinkContractInterceptor.class.getCanonicalName() + "#linkcontractpermessage";

	public static LinkContract getLinkContract(ExecutionContext executionContext) {

		return (LinkContract) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE);
	}

	public static void putLinkContract(ExecutionContext executionContext, LinkContract linkContract) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACT_PER_MESSAGE, linkContract);
	}
}
