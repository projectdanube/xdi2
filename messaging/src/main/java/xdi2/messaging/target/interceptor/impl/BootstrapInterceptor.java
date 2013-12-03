package xdi2.messaging.target.interceptor.impl;

import java.util.Arrays;
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
import xdi2.core.features.linkcontracts.policy.PolicyOr;
import xdi2.core.features.linkcontracts.policy.PolicyUtil;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;

/**
 * This interceptor can initialize an empty XDI graph with basic bootstrapping data,
 * such as the owner XDI address of the graph, and initial link contracts.
 * 
 * @author markus
 */
public class BootstrapInterceptor extends AbstractInterceptor implements Prototype<BootstrapInterceptor> {

	private static Logger log = LoggerFactory.getLogger(BootstrapInterceptor.class.getName());

	public final static int INIT_PRIORITY = 20;
	public final static int SHUTDOWN_PRIORITY = 10;

	private XDI3Segment bootstrapOwner;
	private XDI3Segment[] bootstrapOwnerSynonyms;
	private boolean bootstrapRootLinkContract;
	private boolean bootstrapPublicLinkContract;
	private Graph bootstrapGraph;
	private MessageEnvelope bootstrapMessageEnvelope;

	public BootstrapInterceptor() {

		super(INIT_PRIORITY, SHUTDOWN_PRIORITY);

		this.bootstrapOwner = null;
		this.bootstrapOwnerSynonyms = null;
		this.bootstrapRootLinkContract = false;
		this.bootstrapPublicLinkContract = false;
		this.bootstrapGraph = null;
		this.bootstrapMessageEnvelope = null;
	}

	/*
	 * Prototype
	 */

	@Override
	public BootstrapInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		BootstrapInterceptor interceptor = new BootstrapInterceptor();

		// set the owner, root link contract, and public link contract

		interceptor.setBootstrapOwner(prototypingContext.getOwner());
		interceptor.setBootstrapRootLinkContract(this.getBootstrapRootLinkContract());
		interceptor.setBootstrapPublicLinkContract(this.getBootstrapPublicLinkContract());

		// set the owner synonyms

		XDI3Segment[] ownerSynonyms = null;

		if (prototypingContext.getOwnerPeerRoot() != null) {

			Iterator<ContextNode> ownerSynonymPeerRootContextNodes = Equivalence.getIncomingReferenceContextNodes(prototypingContext.getOwnerPeerRoot().getContextNode());
			XdiPeerRoot[] ownerSynonymPeerRoots = (new IteratorArrayMaker<XdiPeerRoot> (new MappingContextNodePeerRootIterator(ownerSynonymPeerRootContextNodes))).array(XdiPeerRoot.class);

			ownerSynonyms = new XDI3Segment[ownerSynonymPeerRoots.length];
			for (int i=0; i<ownerSynonyms.length; i++) ownerSynonyms[i] = ownerSynonymPeerRoots[i].getXriOfPeerRoot();
		}

		interceptor.setBootstrapOwnerSynonyms(ownerSynonyms);

		// set boostrap statements and operations

		interceptor.setBootstrapGraph(this.getBootstrapGraph());
		interceptor.setBootstrapMessageEnvelope(this.getBootstrapMessageEnvelope());

		// done

		return interceptor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (! (messagingTarget instanceof GraphMessagingTarget)) return;

		GraphMessagingTarget graphMessagingTarget = (GraphMessagingTarget) messagingTarget;
		Graph graph = graphMessagingTarget.getGraph();

		ContextNode rootContextNode = graph.getRootContextNode();

		if (log.isDebugEnabled()) log.debug("bootstrapOwner=" + this.getBootstrapOwner() + ", bootstrapOwnerSynonyms=" + Arrays.asList(this.getBootstrapOwnerSynonyms()) + ", bootstrapLinkContract=" + this.getBootstrapRootLinkContract() + ", bootstrapPublicLinkContract=" + this.getBootstrapPublicLinkContract() + ", bootstrapGraph=" + (this.getBootstrapGraph() != null) + ", bootstrapMessageEnvelope=" + (this.getBootstrapMessageEnvelope() != null));

		// check if the owner statement exists

		if (XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot() != null) return;

		// create bootstrap owner

		ContextNode bootstrapOwnerContextNode = null;
		ContextNode bootstrapOwnerSelfPeerRootContextNode = null;

		if (this.getBootstrapOwner() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap owner: " + this.getBootstrapOwner());

			bootstrapOwnerContextNode = graph.setDeepContextNode(this.getBootstrapOwner());
			bootstrapOwnerSelfPeerRootContextNode = XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(this.getBootstrapOwner()).getContextNode();

			// create bootstrap owner synonyms

			if (this.getBootstrapOwnerSynonyms() != null) {

				if (log.isDebugEnabled()) log.debug("Creating bootstrap owner synonyms: " + Arrays.asList(this.getBootstrapOwnerSynonyms()));

				for (XDI3Segment bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

					ContextNode bootstrapOwnerSynonymContextNode = graph.setDeepContextNode(bootstrapOwnerSynonym);
					bootstrapOwnerSynonymContextNode.delRelations(XDIDictionaryConstants.XRI_S_REF);
					bootstrapOwnerSynonymContextNode.setRelation(XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerContextNode);
					bootstrapOwnerContextNode.delRelations(XDIDictionaryConstants.XRI_S_IS_REF);
					bootstrapOwnerContextNode.setRelation(XDIDictionaryConstants.XRI_S_IS_REF, bootstrapOwnerSynonymContextNode);

					ContextNode bootstrapOwnerSynonymPeerRootContextNode = XdiLocalRoot.findLocalRoot(graph).findPeerRoot(bootstrapOwnerSynonym, true).getContextNode();
					bootstrapOwnerSynonymPeerRootContextNode.delRelations(XDIDictionaryConstants.XRI_S_REF);
					bootstrapOwnerSynonymPeerRootContextNode.setRelation(XDIDictionaryConstants.XRI_S_REF, bootstrapOwnerSelfPeerRootContextNode);
				}
			}
		}

		// create bootstrap root link contract

		if (this.getBootstrapRootLinkContract()) {

			if (this.getBootstrapOwner() == null) {

				throw new Xdi2MessagingException("Can only create the bootstrap root link contract if a bootstrap owner is given.", null, null);
			}

			if (log.isDebugEnabled()) log.debug("Creating bootstrap root link contract.");

			bootstrapOwnerContextNode = graph.setDeepContextNode(this.getBootstrapOwner());

			LinkContract bootstrapLinkContract = LinkContracts.getLinkContract(rootContextNode, true);
			bootstrapLinkContract.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_ALL, XDIConstants.XRI_S_ROOT);

			PolicyAnd policyAnd = bootstrapLinkContract.getPolicyRoot(true).createAndPolicy(true);
			PolicyUtil.createSenderIsOperator(policyAnd, this.getBootstrapOwner());

			PolicyOr policyOr = policyAnd.createOrPolicy(true);
			PolicyUtil.createSecretTokenValidOperator(policyOr);
			PolicyUtil.createSignatureValidOperator(policyOr);
		}

		// create bootstrap public link contract

		if (this.getBootstrapPublicLinkContract()) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap public link contract.");

			ContextNode publicContextNode = graph.setDeepContextNode(XDILinkContractConstants.XRI_S_PUBLIC);

			LinkContract bootstrapPublicLinkContract = LinkContracts.getLinkContract(publicContextNode, true);
			bootstrapPublicLinkContract.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_GET, XDILinkContractConstants.XRI_S_PUBLIC);

			XDI3Statement selfPeerRootRefStatement = XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE);
			bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, selfPeerRootRefStatement);

			for (XDI3Segment bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

				XDI3Statement bootstrapOwnerSynonymRefStatement = XDI3Statement.fromRelationComponents(bootstrapOwnerSynonym, XDIDictionaryConstants.XRI_S_REF, this.getBootstrapOwner());
				bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, bootstrapOwnerSynonymRefStatement);

				XDI3Statement bootstrapOwnerSynonymIsRefStatement = XDI3Statement.fromRelationComponents(this.getBootstrapOwner(), XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE);
				bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, bootstrapOwnerSynonymIsRefStatement);
			}
		}

		// create bootstrap graph

		if (this.getBootstrapGraph() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap graph.");

			CopyUtil.copyGraph(this.getBootstrapGraph(), graph, null);
		}

		// execute bootstrap message envelope

		if (this.getBootstrapMessageEnvelope() != null) {

			if (log.isDebugEnabled()) log.debug("Executing bootstrap message envelope.");

			ToInterceptor toInterceptor = null;
			Boolean toInterceptorEnabled = null;
			RefInterceptor refInterceptor = null;
			Boolean refInterceptorEnabled = null;
			LinkContractInterceptor linkContractInterceptor = null;
			Boolean linkContractInterceptorEnabled = null;

			try {

				toInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(ToInterceptor.class);
				toInterceptorEnabled = Boolean.valueOf(toInterceptor != null && toInterceptor.isEnabled());
				if (toInterceptor != null) toInterceptor.setEnabled(false);

				refInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(RefInterceptor.class);
				refInterceptorEnabled = Boolean.valueOf(refInterceptor != null && refInterceptor.isEnabled());
				if (refInterceptor != null) refInterceptor.setEnabled(false);

				linkContractInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
				linkContractInterceptorEnabled = Boolean.valueOf(linkContractInterceptor != null && linkContractInterceptor.isEnabled());
				if (linkContractInterceptor != null) linkContractInterceptor.setEnabled(false);

				graphMessagingTarget.execute(this.getBootstrapMessageEnvelope(), null, null);
			} finally {

				if (toInterceptor != null && toInterceptorEnabled != null) toInterceptor.setEnabled(toInterceptorEnabled.booleanValue());
				if (refInterceptor != null && refInterceptorEnabled != null) refInterceptor.setEnabled(refInterceptorEnabled.booleanValue());
				if (linkContractInterceptor != null && linkContractInterceptorEnabled != null) linkContractInterceptor.setEnabled(linkContractInterceptorEnabled.booleanValue());
			}
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);
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

	public Graph getBootstrapGraph() {

		return this.bootstrapGraph;
	}

	public void setBootstrapGraph(Graph bootstrapGraph) {

		this.bootstrapGraph = bootstrapGraph;
	}

	public MessageEnvelope getBootstrapMessageEnvelope() {

		return this.bootstrapMessageEnvelope;
	}

	public void setBootstrapMessageEnvelope(MessageEnvelope bootstrapMessageEnvelope) {

		this.bootstrapMessageEnvelope = bootstrapMessageEnvelope;
	}
}
