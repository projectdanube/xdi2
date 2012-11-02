package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.linkcontracts.AndExpression;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.Policy;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;

/**
 * This interceptor can initialize an empty XDI graph with basic bootstrapping data,
 * such as the owner XRI of the graph, a shared secret, and an initial "root link contract".
 * 
 * @author markus
 */
public class BootstrapInterceptor implements MessagingTargetInterceptor, Prototype<BootstrapInterceptor> {

	private static Logger log = LoggerFactory.getLogger(BootstrapInterceptor.class.getName());

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

	/*
	 * Prototype
	 */

	@Override
	public BootstrapInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		BootstrapInterceptor interceptor = new BootstrapInterceptor();
		interceptor.setBootstrapOwner(prototypingContext.getOwner());
		interceptor.setBootstrapLinkContract(this.getBootstrapLinkContract());

		// read the owner synonyms

		XRI3Segment[] ownerSynonyms = null;

		if (prototypingContext.getOwnerRemoteRootContextNode() != null) {

			Iterator<ContextNode> ownerSynonymRemoteRootContextNodes = Dictionary.getSynonymContextNodes(prototypingContext.getOwnerRemoteRootContextNode());

			ownerSynonyms = (new IteratorArrayMaker<XRI3Segment> (new MappingContextNodeXriIterator(ownerSynonymRemoteRootContextNodes))).array(XRI3Segment.class);
			for (int i=0; i<ownerSynonyms.length; i++) ownerSynonyms[i] = RemoteRoots.xriOfRemoteRootXri(ownerSynonyms[i]);
		}

		interceptor.setBootstrapOwnerSynonyms(ownerSynonyms);

		// read the shared secret

		String sharedSecret = null;

		if (prototypingContext.getOwnerContextNode() != null) {

			Literal sharedSecretLiteral = prototypingContext.getOwnerContextNode().findLiteral(XDIMessagingConstants.XRI_S_SECRET_TOKEN);
			sharedSecret = sharedSecretLiteral == null ? null : sharedSecretLiteral.getLiteralData();
		}

		interceptor.setBootstrapSharedSecret(sharedSecret);

		// done

		return interceptor;
	}

	/*
	 * MessagingTargetInterceptor
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		if (! (messagingTarget instanceof GraphMessagingTarget)) return;

		GraphMessagingTarget graphMessagingTarget = (GraphMessagingTarget) messagingTarget;
		Graph graph = graphMessagingTarget.getGraph();
		ContextNode rootContextNode = graph.getRootContextNode();

		log.debug("bootstrapOwner=" + this.bootstrapOwner + ", bootstrapSharedSecret=" + (this.bootstrapSharedSecret == null ? null : "XXXXX") + ", bootstrapLinkContract=" + this.bootstrapLinkContract);

		// check if the owner statement exists

		if (rootContextNode.containsRelations(XDIDictionaryConstants.XRI_S_IS_IS)) return;

		// create bootstrap owner

		ContextNode bootstrapOwnerContextNode = null;
		ContextNode bootstrapOwnerSelfRemoteRootContextNode = null;

		if (this.bootstrapOwner != null) {

			bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);
			bootstrapOwnerSelfRemoteRootContextNode = RemoteRoots.setSelfRemoteRootContextNode(graph, this.bootstrapOwner);
		}

		// create bootstrap owner synonyms

		if (this.bootstrapOwner != null && this.bootstrapOwnerSynonyms != null) {

			for (XRI3Segment bootstrapOwnerSynonym : this.bootstrapOwnerSynonyms) {

				ContextNode bootstrapOwnerSynonymContextNode = graph.findContextNode(bootstrapOwnerSynonym, true);
				bootstrapOwnerSynonymContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, bootstrapOwnerContextNode);

				ContextNode bootstrapOwnerSynonymRemoteRootContextNode = RemoteRoots.findRemoteRootContextNode(graph, bootstrapOwnerSynonym, true);
				bootstrapOwnerSynonymRemoteRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, bootstrapOwnerSelfRemoteRootContextNode);
			}
		}

		// create bootstrap shared secret

		if (this.bootstrapSharedSecret != null) {

			ContextNode bootstrapOwnerSharedSecretContextNode = graph.findContextNode(XDIMessagingConstants.XRI_S_SECRET_TOKEN, true);
			bootstrapOwnerSharedSecretContextNode.createLiteral(this.bootstrapSharedSecret);
		}

		// create bootstrap link contract and policy

		if (this.bootstrapLinkContract) {

			bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);

			LinkContract bootstrapLinkContract = LinkContracts.getLinkContract(rootContextNode, true);
			bootstrapLinkContract.addAssignee(bootstrapOwnerContextNode);
			bootstrapLinkContract.addPermission(XDILinkContractPermission.LC_OP_ALL, rootContextNode);

			Policy policy = bootstrapLinkContract.getPolicy(true);
			AndExpression andExpression = policy.getAndNode(true);
			andExpression.addLiteralExpression("xdi.getGraphValue('" + XDIMessagingConstants.XRI_S_SECRET_TOKEN  +"') != null && (xdi.getGraphValue('" + XDIMessagingConstants.XRI_S_SECRET_TOKEN + "') == xdi.getMessageProperty('" + XDIMessagingConstants.XRI_S_SECRET_TOKEN + "'))");
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}

	/*
	 * Getters and setters
	 */

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
