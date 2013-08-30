package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.policy.PolicyAnd;
import xdi2.core.features.linkcontracts.policy.PolicyUtil;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;

/**
 * This interceptor can initialize an empty XDI graph with basic bootstrapping data,
 * such as the owner XDI address of the graph, and initial link contracts.
 * 
 * @author markus
 */
public class BootstrapInterceptor extends AbstractInterceptor implements MessagingTargetInterceptor, Prototype<BootstrapInterceptor> {

	private static Logger log = LoggerFactory.getLogger(BootstrapInterceptor.class.getName());

	private XDI3Segment bootstrapOwner;
	private XDI3Segment[] bootstrapOwnerSynonyms;
	private boolean bootstrapRootLinkContract;
	private boolean bootstrapPublicLinkContract;
	private XDI3Statement[] bootstrapStatements;

	public BootstrapInterceptor() {

		this.bootstrapOwner = null;
		this.bootstrapOwnerSynonyms = null;
		this.bootstrapRootLinkContract = false;
		this.bootstrapPublicLinkContract = false;
		this.bootstrapStatements = null;
	}

	/*
	 * Prototype
	 */

	@Override
	public BootstrapInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		BootstrapInterceptor interceptor = new BootstrapInterceptor();
		interceptor.setBootstrapOwner(prototypingContext.getOwner());
		interceptor.setBootstrapRootLinkContract(this.getBootstrapRootLinkContract());
		interceptor.setBootstrapPublicLinkContract(this.getBootstrapPublicLinkContract());

		// read the owner synonyms

		XDI3Segment[] ownerSynonyms = null;

		if (prototypingContext.getOwnerPeerRoot() != null) {

			Iterator<ContextNode> ownerSynonymPeerRootContextNodes = Equivalence.getIncomingReferenceContextNodes(prototypingContext.getOwnerPeerRoot().getContextNode());
			XdiPeerRoot[] ownerSynonymPeerRoots = (new IteratorArrayMaker<XdiPeerRoot> (new MappingContextNodePeerRootIterator(ownerSynonymPeerRootContextNodes))).array(XdiPeerRoot.class);

			ownerSynonyms = new XDI3Segment[ownerSynonymPeerRoots.length];
			for (int i=0; i<ownerSynonyms.length; i++) ownerSynonyms[i] = ownerSynonymPeerRoots[i].getXriOfPeerRoot();
		}

		interceptor.setBootstrapOwnerSynonyms(ownerSynonyms);

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

		if (log.isDebugEnabled()) log.debug("bootstrapOwner=" + this.getBootstrapOwner() + ", bootstrapOwnerSynonyms=" + this.getBootstrapOwnerSynonyms() + ", bootstrapLinkContract=" + this.getBootstrapRootLinkContract() + ", bootstrapPublicLinkContract=" + this.getBootstrapPublicLinkContract());

		// check if the owner statement exists

		if (XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot() != null) return;

		// create bootstrap owner

		ContextNode bootstrapOwnerContextNode = null;
		ContextNode bootstrapOwnerSelfPeerRootContextNode = null;

		if (this.getBootstrapOwner() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap owner: " + this.getBootstrapOwner());

			bootstrapOwnerContextNode = graph.setDeepContextNode(this.getBootstrapOwner());
			bootstrapOwnerSelfPeerRootContextNode = XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(this.getBootstrapOwner()).getContextNode();
		}

		// create bootstrap owner synonyms

		if (this.getBootstrapOwner() != null && this.getBootstrapOwnerSynonyms() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap owner synonyms: " + this.getBootstrapOwnerSynonyms());

			for (XDI3Segment bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

				graph.setDeepRelation(bootstrapOwnerSynonym, XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerContextNode);

				ContextNode bootstrapOwnerSynonymPeerRootContextNode = XdiLocalRoot.findLocalRoot(graph).findPeerRoot(bootstrapOwnerSynonym, true).getContextNode();
				bootstrapOwnerSynonymPeerRootContextNode.delRelations(XDIDictionaryConstants.XRI_S_REF);
				bootstrapOwnerSynonymPeerRootContextNode.setRelation(XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerSelfPeerRootContextNode);
			}
		}

		// create bootstrap root link contract

		if (this.getBootstrapRootLinkContract()) {

			if (this.getBootstrapOwner() == null) {

				throw new Xdi2MessagingException("Can only create the bootstrap root link contract if a bootstrap owner is given.", null, null);
			}

			if (log.isDebugEnabled()) log.debug("Creating bootstrap link contract: " + this.getBootstrapRootLinkContract());

			bootstrapOwnerContextNode = graph.setDeepContextNode(this.getBootstrapOwner());

			LinkContract bootstrapLinkContract = LinkContracts.getLinkContract(rootContextNode, true);
			bootstrapLinkContract.setPermission(XDILinkContractConstants.XRI_S_ALL, XDIConstants.XRI_S_ROOT);

			PolicyAnd policyAnd = bootstrapLinkContract.getPolicyRoot(true).createAndPolicy(true);
			PolicyUtil.createSenderIsOperator(policyAnd, this.getBootstrapOwner());
			PolicyUtil.createSecretTokenValidOperator(policyAnd);
		}

		// create bootstrap public link contract

		if (this.getBootstrapPublicLinkContract()) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap public link contract.");

			ContextNode publicContextNode = graph.setDeepContextNode(XDILinkContractConstants.XRI_S_PUBLIC);

			LinkContract bootstrapPublicLinkContract = LinkContracts.getLinkContract(publicContextNode, true);
			bootstrapPublicLinkContract.setPermission(XDILinkContractConstants.XRI_S_GET, XDILinkContractConstants.XRI_S_PUBLIC);
		}

		// create bootstrap statements

		if (this.getBootstrapStatements() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap statements.");

			for (XDI3Statement bootstrapStatement : this.getBootstrapStatements()) {

				graph.setStatement(bootstrapStatement);
			}
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

	public boolean getBootstrapRootLinkContract() {

		return this.bootstrapRootLinkContract;
	}

	public void setBootstrapRootLinkContract(boolean bootstrapLinkContract) {

		this.bootstrapRootLinkContract = bootstrapLinkContract;
	}

	public boolean getBootstrapPublicLinkContract() {

		return this.bootstrapPublicLinkContract;
	}

	public void setBootstrapPublicLinkContract(boolean bootstrapPublicLinkContract) {

		this.bootstrapPublicLinkContract = bootstrapPublicLinkContract;
	}

	public XDI3Statement[] getBootstrapStatements() {

		return this.bootstrapStatements;
	}

	public void setBootstrapStatements(XDI3Statement[] bootstrapStatements) {

		this.bootstrapStatements = bootstrapStatements;
	}
}
