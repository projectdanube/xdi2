package xdi2.messaging.target.contributor.impl.connection;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can inverse operations.
 * Warning: This is experimental, do not use for serious applications.
 */
@ContributorMount(
		contributorXDIAddresses={""},
		operationXDIAddresses={"$get$is", "$set$is", "$del$is", "$do$is", "$do$is{}"}
		)
public class InverseOperationContributor extends AbstractContributor implements Prototype<InverseOperationContributor> {

	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT;

	private Graph targetGraph;
	private XDIDiscoveryClient xdiDiscoveryClient;

	public InverseOperationContributor(Graph targetGraph, XDIDiscoveryClient xdiDiscoveryClient) {

		this.targetGraph = targetGraph;
		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public InverseOperationContributor() {

		this(null, DEFAULT_DISCOVERY_CLIENT);
	}

	/*
	 * Prototype
	 */

	@Override
	public InverseOperationContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		InverseOperationContributor contributor = new InverseOperationContributor();

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

		// set the discovery client

		contributor.setXdiDiscoveryClient(this.getXdiDiscoveryClient());

		// done

		return contributor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getTargetGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setTargetGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getTargetGraph() == null) throw new Xdi2MessagingException("No target graph.", null, null);
	}

	/*
	 * Contributor methods
	 */

	@Override
	public ContributorResult executeGetOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetXDIAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, relativeTargetXDIAddress, null, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeSetOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetXDIAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, relativeTargetXDIAddress, null, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeDelOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetXDIAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, relativeTargetXDIAddress, null, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeDoOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetXDIAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, relativeTargetXDIAddress, null, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeGetOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, null, relativeTargetStatement, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeSetOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, null, relativeTargetStatement, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeDelOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, null, relativeTargetStatement, messageResult, executionContext);
	}

	@Override
	public ContributorResult executeDoOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return this.sendInverseMessage(operation, null, relativeTargetStatement, messageResult, executionContext);
	}

	/*
	 * Helper methods
	 */

	private ContributorResult sendInverseMessage(Operation operation, XDIAddress targetXDIAddress, XDIStatement targetXDIStatement, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// determine sender for the outgoing message

		XDIAddress senderXDIAddress = GraphUtil.getOwnerXDIAddress(this.getTargetGraph());

		// determine recipient for the outgoing message

		XDIAddress recipientXDIAddress = operation.getSenderXDIAddress();

		// discover recipient

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(recipientXDIAddress, null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("XDI Discovery failed on " + recipientXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		}

		if (xdiDiscoveryResult.getCloudNumber() == null) throw new Xdi2MessagingException("Could not discover Cloud Number for recipient at " + recipientXDIAddress, null, executionContext);
		if (xdiDiscoveryResult.getXdiEndpointUrl() == null) throw new Xdi2MessagingException("Could not discover XDI endpoint URI for recipient at " + recipientXDIAddress, null, executionContext);

		// create connection request

		//TODO: this is not quite right
		XDIAddress inverseOperationXDIAddress = XDIAddress.create(operation.getOperationXDIAddress().toString().replace("$is", ""));
		XDIAddress inverseLinkContractXDIAddress = getInverseLinkContractXDIAddress(operation.getMessage());

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(senderXDIAddress);
		message.getLinkContractXDIAddress();
		CopyUtil.copyContextNodeContents(operation.getMessage().getContextNode(), message.getContextNode(), null);
		message.deleteOperations();
		message.setToPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(recipientXDIAddress));
		message.setLinkContractXDIAddress(inverseLinkContractXDIAddress);
		if (targetXDIAddress != null) message.createOperation(inverseOperationXDIAddress, targetXDIAddress);
		if (targetXDIStatement != null) message.createOperation(inverseOperationXDIAddress, targetXDIStatement);

		try {

			new XDIHttpClient(xdiDiscoveryResult.getXdiEndpointUrl()).send(messageEnvelope, messageResult);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while sending message with inverse operation: " + ex.getMessage(), ex, executionContext);
		}

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Helper methods
	 */

	public static XDIAddress getInverseLinkContractXDIAddress(Message message) {

		Relation inverseLinkContractRelation = message.getContextNode().getRelation(XDIAddressUtil.concatXDIAddresses(XDIDictionaryConstants.XDI_ADD_IS, XDILinkContractConstants.XDI_ADD_DO));
		if (inverseLinkContractRelation == null) return null;

		return inverseLinkContractRelation.getTargetXDIAddress();
	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return this.xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}
}
