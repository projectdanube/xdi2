package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.policy.PolicyAnd;
import xdi2.core.features.linkcontracts.policy.PolicyUtil;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.xri3.XDI3Segment;
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

	private XDI3Segment bootstrapOwner;
	private XDI3Segment[] bootstrapOwnerSynonyms;
	private String bootstrapSharedSecret;
	private boolean bootstrapLinkContract;
	private boolean bootstrapPublicLinkContract;

	public BootstrapInterceptor() {

		this.bootstrapOwner = null;
		this.bootstrapOwnerSynonyms = null;
		this.bootstrapSharedSecret = null;
		this.bootstrapLinkContract = false;
		this.bootstrapPublicLinkContract = false;
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
		interceptor.setBootstrapPublicLinkContract(this.getBootstrapPublicLinkContract());

		// read the owner synonyms

		XDI3Segment[] ownerSynonyms = null;

		if (prototypingContext.getOwnerRemoteRootContextNode() != null) {

			Iterator<ContextNode> ownerSynonymRemoteRootContextNodes = Equivalence.getIncomingReferenceAndPrivateReferenceContextNodes(prototypingContext.getOwnerRemoteRootContextNode());

			ownerSynonyms = (new IteratorArrayMaker<XDI3Segment> (new MappingContextNodeXriIterator(ownerSynonymRemoteRootContextNodes))).array(XDI3Segment.class);
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

		log.debug("bootstrapOwner=" + this.bootstrapOwner + ", bootstrapOwnerSynonyms=" + this.bootstrapOwnerSynonyms + ", bootstrapSharedSecret=" + (this.bootstrapSharedSecret == null ? null : "XXXXX") + ", bootstrapLinkContract=" + this.bootstrapLinkContract + ", bootstrapPublicLinkContract=" + this.bootstrapPublicLinkContract);

		// check if the owner statement exists

		if (RemoteRoots.getSelfRemoteRootContextNode(graph) != null) return;

		// create bootstrap owner

		ContextNode bootstrapOwnerContextNode = null;
		ContextNode bootstrapOwnerSelfRemoteRootContextNode = null;

		if (this.bootstrapOwner != null) {

			bootstrapOwnerContextNode = graph.findContextNode(this.bootstrapOwner, true);
			bootstrapOwnerSelfRemoteRootContextNode = RemoteRoots.setSelfRemoteRootContextNode(graph, this.bootstrapOwner);
		}

		// create bootstrap owner synonyms

		if (this.bootstrapOwner != null && this.bootstrapOwnerSynonyms != null) {

			for (XDI3Segment bootstrapOwnerSynonym : this.bootstrapOwnerSynonyms) {

				ContextNode bootstrapOwnerSynonymContextNode = graph.findContextNode(bootstrapOwnerSynonym, true);
				bootstrapOwnerSynonymContextNode.createRelation(XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerContextNode);

				ContextNode bootstrapOwnerSynonymRemoteRootContextNode = RemoteRoots.findRemoteRootContextNode(graph, bootstrapOwnerSynonym, true);
				bootstrapOwnerSynonymRemoteRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerSelfRemoteRootContextNode);
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
			bootstrapLinkContract.addPermission(XDILinkContractConstants.XRI_S_ALL, XDIConstants.XRI_S_ROOT);

			PolicyAnd policyAnd = bootstrapLinkContract.getPolicyRoot(true).createAndPolicy();
			policyAnd.addPolicyStatement(PolicyUtil.senderMatchesPolicyStatement(this.bootstrapOwner));
			policyAnd.addPolicyStatement(PolicyUtil.secretTokenMatchesPolicyStatement());
		}

		// create public bootstrap link contract

		if (this.bootstrapPublicLinkContract) {

			ContextNode publicContextNode = graph.findContextNode(XDIConstants.XRI_S_PUBLIC, true);

			LinkContract bootstrapPublicLinkContract = LinkContracts.getLinkContract(publicContextNode, true);
			bootstrapPublicLinkContract.addPermission(XDILinkContractConstants.XRI_S_GET, XDIConstants.XRI_S_PUBLIC);
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}

	/*
	 * Getters and setters
	 */

	public XDI3Segment getBootstrapOwner() {

		return this.bootstrapOwner;
	}

	public void setBootstrapOwner(XDI3Segment bootstrapOwner) {

		this.bootstrapOwner = bootstrapOwner;
	}

	public XDI3Segment[] getBootstrapOwnerSynonyms() {

		return this.bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(XDI3Segment[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(String[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = new XDI3Segment[bootstrapOwnerSynonyms.length];
		for (int i=0; i<this.bootstrapOwnerSynonyms.length; i++) this.bootstrapOwnerSynonyms[i] = XDI3Segment.create(bootstrapOwnerSynonyms[i]);
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

	public boolean getBootstrapPublicLinkContract() {

		return this.bootstrapPublicLinkContract;
	}

	public void setBootstrapPublicLinkContract(boolean bootstrapPublicLinkContract) {

		this.bootstrapPublicLinkContract = bootstrapPublicLinkContract;
	}
}
