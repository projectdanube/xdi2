package xdi2.messaging.target.contributor.impl.connection;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.agent.XDIAgent;
import xdi2.client.agent.impl.XDIBasicAgent;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiAbstractVariable;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.features.nodetypes.XdiVariableSingleton.MappingContextNodeXdiVariableSingletonIterator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can process connection requests.
 * Warning: This is experimental, do not use for serious applications.
 */
@ContributorMount(
		contributorAddresses={"{{}}{$do}"},
		operationAddresses={"$do{}"}
		)
public class ConnectionRequestContributor extends AbstractContributor implements Prototype<ConnectionRequestContributor> {

	private static final Logger log = LoggerFactory.getLogger(ConnectionRequestContributor.class);

	private Graph targetGraph;

	public ConnectionRequestContributor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public ConnectionRequestContributor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public ConnectionRequestContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		ConnectionRequestContributor contributor = new ConnectionRequestContributor();

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

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
	public ContributorResult executeDoOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress linkContractTemplateXDIaddress = operation.getTargetXDIAddress();

		// determine requesting authority

		XDIAddress requestingAuthority = operation.getSenderXDIAddress();

		// determine authorizing authority

		XDIAddress authorizingAuthority = GraphUtil.getOwnerXDIAddress(this.getTargetGraph());

		// use agent to obtain link contract template

		ContextNode linkContractTemplateContextNode;

		try {

			XDIAgent xdiAgent = new XDIBasicAgent();
			linkContractTemplateContextNode = xdiAgent.get(linkContractTemplateXDIaddress, null);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Unable to obtain link contract template at address " + operation.getTargetXDIAddress() + ": " + ex.getMessage(), ex, executionContext);
		}

		XdiVariable linkContractTemplateXdiVariable = XdiAbstractVariable.fromContextNode(linkContractTemplateContextNode);
		if (linkContractTemplateXdiVariable == null) throw new Xdi2MessagingException("Invalid link contract template variable at address " + operation.getTargetXDIAddress(), null, executionContext);

		LinkContractTemplate linkContractTemplate = LinkContractTemplate.fromXdiVariable(linkContractTemplateXdiVariable);
		if (linkContractTemplate == null) throw new Xdi2MessagingException("Invalid link contract template at address " + operation.getTargetXDIAddress(), null, executionContext);

		// read custom replacements from message

		Map<XDIArc, XDIAddress> customReplacements = new HashMap<XDIArc, XDIAddress> ();
		MappingContextNodeXdiVariableSingletonIterator xdiVariablesIterator = new MappingContextNodeXdiVariableSingletonIterator(operation.getMessage().getContextNode().getContextNodes());

		for (XdiVariable xdiVariable : xdiVariablesIterator) {

			XDIArc customReplacementXDIArc = xdiVariable.getXDIArc();
			ContextNode customReplacementContextNode = Equivalence.getIdentityContextNode(xdiVariable.getContextNode());
			XDIAddress customReplacementXDIAddress = customReplacementContextNode == null ? null : customReplacementContextNode.getXDIAddress();

			if (log.isDebugEnabled()) log.debug("Custom variable replacement: " + customReplacementXDIArc + " --> " + customReplacementXDIAddress);

			if (customReplacementXDIArc == null || customReplacementXDIAddress == null) continue;

			customReplacements.put(customReplacementXDIArc, customReplacementXDIAddress);
		}

		// instantiate link contract

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation();
		linkContractInstantiation.setRequestingAuthority(requestingAuthority);
		linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
		linkContractInstantiation.setLinkContractTemplate(linkContractTemplate);

		GenericLinkContract genericLinkContract = linkContractInstantiation.execute(
				this.getTargetGraph(), 
				customReplacements, 
				true);

		// return link contract instance in result

		messageResult.getGraph().setStatement(XDIStatement.fromComponents(
				linkContractTemplate.getContextNode().getXDIAddress(),
				XDIDictionaryConstants.XDI_ADD_TYPE, 
				genericLinkContract.getContextNode().getXDIAddress())); 

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
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
}
