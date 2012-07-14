package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.features.multiplicity.AttributeSingleton;
import xdi2.core.features.multiplicity.EntitySingleton;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;

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

				graph.findContextNode(this.bootstrapOwner, true);

				ContextNode bootstrapOwnerRemoteRootContextNode = RemoteRoots.findRemoteRootContextNode(graph, this.bootstrapOwner, true);

				rootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_IS, bootstrapOwnerRemoteRootContextNode);
				bootstrapOwnerRemoteRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, rootContextNode);
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

				EntitySingleton entitySingleton = Multiplicity.getEntitySingleton(rootContextNode, "$secret", true);
				AttributeSingleton attributeSingleton = Multiplicity.getAttributeSingleton(entitySingleton.getContextNode(), "$token", true);

				attributeSingleton.getContextNode().createLiteral(this.bootstrapSharedSecret);
			}

			// bootstrap link contract

			if (this.bootstrapLinkContract) {

				ContextNode bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);

				LinkContract bootstrapLinkContract = LinkContracts.getLinkContract(rootContextNode, true);
				bootstrapLinkContract.addAssignee(bootstrapOwnerContextNode);
				bootstrapLinkContract.addPermission(XDILinkContractPermission.LC_OP_ALL, rootContextNode);
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

	public boolean isBootstrapLinkContract() {

		return this.bootstrapLinkContract;
	}

	public void setBootstrapLinkContract(boolean bootstrapLinkContract) {

		this.bootstrapLinkContract = bootstrapLinkContract;
	}
}
