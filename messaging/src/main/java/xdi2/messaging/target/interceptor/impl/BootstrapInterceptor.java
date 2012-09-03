package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.AndExpression;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.Policy;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;

/**
 * This interceptor can initialize an empty XDI graph with basic bootstrapping data,
 * such as the owner XRI of the graph, a shared secret, and an initial "root link contract".
 * 
 * @author markus
 */
public class BootstrapInterceptor implements MessagingTargetInterceptor {

	private XRI3Segment bootstrapOwner;
	private XRI3Segment[] bootstrapOwnerSynonyms;
	private String bootstrapSharedSecret;
	private boolean bootstrapLinkContract;

	public BootstrapInterceptor() {

		this.bootstrapOwner = null;
		this.bootstrapOwnerSynonyms = null;
		this.bootstrapSharedSecret = null;
		this.bootstrapLinkContract = false;
	}

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		if (! (messagingTarget instanceof GraphMessagingTarget)) return;

		GraphMessagingTarget graphMessagingTarget = (GraphMessagingTarget) messagingTarget;
		Graph graph = graphMessagingTarget.getGraph();
		ContextNode rootContextNode = graph.getRootContextNode();

		// check if the owner statement exists

		if (! rootContextNode.containsRelations(XDIDictionaryConstants.XRI_S_IS_IS)) {

			// bootstrap owner

			if (this.bootstrapOwner != null) {

				graph.getRootContextNode().createContextNodes(this.bootstrapOwner);
				
				RemoteRoots.setSelfRemoteRootContextNode(graph, this.bootstrapOwner);
			}

			// bootstrap owner synonyms

			if (this.bootstrapOwner != null && this.bootstrapOwnerSynonyms != null) {

				ContextNode bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);

				for (XRI3Segment bootstrapOwnerSynonym : this.bootstrapOwnerSynonyms) {

					ContextNode bootstrapOwnerSynonymContextNode = graph.findContextNode(bootstrapOwnerSynonym, true);
					bootstrapOwnerSynonymContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, bootstrapOwnerContextNode);
				}
			}

			// bootstrap shared secret

			if (this.bootstrapSharedSecret != null) {

				XdiEntitySingleton entitySingleton = XdiSubGraph.fromContextNode(rootContextNode).getEntitySingleton(new XRI3SubSegment("$secret"), true);
				XdiAttributeSingleton attributeSingleton = entitySingleton.getAttributeSingleton(new XRI3SubSegment("$token"), true);

				attributeSingleton.getContextNode().createLiteral(this.bootstrapSharedSecret);
			}

			// bootstrap link contract and policy

			if (this.bootstrapLinkContract) {

				ContextNode bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);

				LinkContract bootstrapLinkContract = LinkContracts.getLinkContract(rootContextNode, true);
				bootstrapLinkContract.addAssignee(bootstrapOwnerContextNode);
				bootstrapLinkContract.addPermission(XDILinkContractPermission.LC_OP_ALL, rootContextNode);

				Policy policy = bootstrapLinkContract.getPolicy(true);
				AndExpression andExpression = policy.getAndNode(true);
				andExpression.addLiteralExpression("xdi.getGraphValue('$secret$!($token)') == xdi.getMessageProperty('$secret$!($token)')");
			}
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}

	public XRI3Segment getBootstrapOwner() {

		return this.bootstrapOwner;
	}

	public void setBootstrapOwner(XRI3Segment bootstrapOwner) {

		this.bootstrapOwner = bootstrapOwner;
	}

	public XRI3Segment[] getBootstrapOwnerSynonyms() {

		return this.bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(XRI3Segment[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(String[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = new XRI3Segment[bootstrapOwnerSynonyms.length];
		for (int i=0; i<this.bootstrapOwnerSynonyms.length; i++) this.bootstrapOwnerSynonyms[i] = new XRI3Segment(bootstrapOwnerSynonyms[i]);
	}

	public String getBootstrapSharedSecret() {

		return this.bootstrapSharedSecret;
	}

	public void setBootstrapSharedSecret(String bootstrapSharedSecret) {

		this.bootstrapSharedSecret = bootstrapSharedSecret;
	}

	public boolean getBootstrapLinkContract() {

		return this.bootstrapLinkContract;
	}

	public void setBootstrapLinkContract(boolean bootstrapLinkContract) {

		this.bootstrapLinkContract = bootstrapLinkContract;
	}
}
