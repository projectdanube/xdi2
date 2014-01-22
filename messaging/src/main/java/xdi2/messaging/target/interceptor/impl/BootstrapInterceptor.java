package xdi2.messaging.target.interceptor.impl;

import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.PublicLinkContract;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.features.linkcontracts.policy.PolicyAnd;
import xdi2.core.features.linkcontracts.policy.PolicyOr;
import xdi2.core.features.linkcontracts.policy.PolicyUtil;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
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
public class BootstrapInterceptor extends AbstractInterceptor<MessagingTarget> implements Prototype<BootstrapInterceptor> {

	private static Logger log = LoggerFactory.getLogger(BootstrapInterceptor.class.getName());

	public final static int INIT_PRIORITY = 20;
	public final static int SHUTDOWN_PRIORITY = 10;

	public final static XDI3SubSegment XRI_SS_SELF = XDI3SubSegment.create("{$self}");

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

		interceptor.setBootstrapOwner(prototypingContext.getOwnerXri());
		interceptor.setBootstrapRootLinkContract(this.getBootstrapRootLinkContract());
		interceptor.setBootstrapPublicLinkContract(this.getBootstrapPublicLinkContract());

		// set the owner synonyms

		XDI3Segment[] bootstrapOwnerSynonyms = null;

		if (prototypingContext.getOwnerPeerRoot() != null) {

			Iterator<ContextNode> ownerSynonymPeerRootContextNodes = Equivalence.getIncomingReferenceContextNodes(prototypingContext.getOwnerPeerRoot().getContextNode());
			XdiPeerRoot[] ownerSynonymPeerRoots = (new IteratorArrayMaker<XdiPeerRoot> (new MappingContextNodePeerRootIterator(ownerSynonymPeerRootContextNodes))).array(XdiPeerRoot.class);

			bootstrapOwnerSynonyms = new XDI3Segment[ownerSynonymPeerRoots.length];
			for (int i=0; i<bootstrapOwnerSynonyms.length; i++) bootstrapOwnerSynonyms[i] = ownerSynonymPeerRoots[i].getXriOfPeerRoot();
		}

		interceptor.setBootstrapOwnerSynonyms(bootstrapOwnerSynonyms);

		// set bootstrap statements and operations

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

		if (log.isDebugEnabled()) log.debug("bootstrapOwner=" + this.getBootstrapOwner() + ", bootstrapOwnerSynonyms=" + (this.getBootstrapOwnerSynonyms() == null ? null : Arrays.asList(this.getBootstrapOwnerSynonyms())) + ", bootstrapLinkContract=" + this.getBootstrapRootLinkContract() + ", bootstrapPublicLinkContract=" + this.getBootstrapPublicLinkContract() + ", bootstrapGraph=" + (this.getBootstrapGraph() != null) + ", bootstrapMessageEnvelope=" + (this.getBootstrapMessageEnvelope() != null));

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

			RootLinkContract bootstrapRootLinkContract = RootLinkContract.findRootLinkContract(graph, true);
			bootstrapRootLinkContract.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_ALL, XDIConstants.XRI_S_ROOT);

			PolicyAnd policyAnd = bootstrapRootLinkContract.getPolicyRoot(true).createAndPolicy(true);
			PolicyUtil.createSenderIsOperator(policyAnd, this.getBootstrapOwner());

			PolicyOr policyOr = policyAnd.createOrPolicy(true);
			PolicyUtil.createSecretTokenValidOperator(policyOr);
			PolicyUtil.createSignatureValidOperator(policyOr);
		}

		// create bootstrap public link contract

		if (this.getBootstrapPublicLinkContract()) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap public link contract.");

			PublicLinkContract bootstrapPublicLinkContract = PublicLinkContract.findPublicLinkContract(graph, true);
			XDI3Segment publicAddress = XDI3Util.concatXris(this.getBootstrapOwner(), XDILinkContractConstants.XRI_S_PUBLIC);
			bootstrapPublicLinkContract.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_GET, publicAddress);

			XDI3Statement selfPeerRootRefStatement = XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE);
			bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, selfPeerRootRefStatement);

			XDI3Statement bootstrapOwnerSynonymsIsRefStatement = XDI3Statement.fromRelationComponents(this.getBootstrapOwner(), XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE);
			bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, bootstrapOwnerSynonymsIsRefStatement);

			if (this.getBootstrapOwnerSynonyms() != null) {

				for (XDI3Segment bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

					XDI3Statement bootstrapOwnerSynonymRefStatement = XDI3Statement.fromRelationComponents(bootstrapOwnerSynonym, XDIDictionaryConstants.XRI_S_REF, this.getBootstrapOwner());
					bootstrapPublicLinkContract.setPermissionTargetStatement(XDILinkContractConstants.XRI_S_GET, bootstrapOwnerSynonymRefStatement);
				}
			}
		}

		// create bootstrap graph

		if (this.getBootstrapGraph() != null) {

			Graph bootstrapGraph = MemoryGraphFactory.getInstance().openGraph();
			CopyUtil.copyGraph(this.getBootstrapGraph(), bootstrapGraph, this.replaceSelfVariableCopyStrategy);

			if (log.isDebugEnabled()) log.debug("Creating bootstrap graph: " + bootstrapGraph.toString());

			CopyUtil.copyGraph(bootstrapGraph, graph, null);
		}

		// execute bootstrap message envelope

		if (this.getBootstrapMessageEnvelope() != null) {

			MessageEnvelope bootstrapMessageEnvelope = new MessageEnvelope();
			CopyUtil.copyGraph(this.getBootstrapMessageEnvelope().getGraph(), bootstrapMessageEnvelope.getGraph(), this.replaceSelfVariableCopyStrategy);

			if (log.isDebugEnabled()) log.debug("Executing bootstrap message envelope: " + bootstrapMessageEnvelope.getGraph().toString());

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

				graphMessagingTarget.execute(bootstrapMessageEnvelope, null, null);
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
	 * Helper classes
	 */

	private final CopyStrategy replaceSelfVariableCopyStrategy = new CopyStrategy() {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			XDI3Segment contextNodeXri = contextNode.getXri();
			XDI3SubSegment contextNodeArcXri = contextNode.getArcXri();

			XDI3Segment replacedContextNodeXri = XDI3Util.concatXris(
					XDI3Util.parentXri(contextNodeXri, -1),
					XDI3Util.replaceXri(
							XDI3Segment.fromComponent(contextNodeArcXri), 
							XRI_SS_SELF, 
							BootstrapInterceptor.this.getBootstrapOwner(), 
							true, 
							true, 
							true));

			if (log.isDebugEnabled()) log.debug("Replaced " + contextNodeXri + " with " + replacedContextNodeXri);

			if (contextNodeXri.equals(replacedContextNodeXri)) return super.replaceContextNode(contextNode);

			ContextNode replacedContextNode = GraphUtil.contextNodeFromComponents(replacedContextNodeXri);
			CopyUtil.copyContextNodeContents(contextNode, replacedContextNode, null);

			int additionalArcs = replacedContextNodeXri.getNumSubSegments() - contextNodeXri.getNumSubSegments();

			replacedContextNode = replacedContextNode.getContextNode(additionalArcs);

			return replacedContextNode;
		}

		@Override
		public Relation replaceRelation(Relation relation) {

			XDI3Segment contextNodeXri = relation.getContextNode().getXri();
			XDI3Segment arcXri = relation.getArcXri();
			XDI3Segment targetContextNodeXri = relation.getTargetContextNodeXri();

			XDI3Segment replacedTargetContextNodeXri = XDI3Util.replaceXri(
					targetContextNodeXri, 
					XRI_SS_SELF, 
					BootstrapInterceptor.this.getBootstrapOwner(), 
					true, 
					true, 
					true);

			if (log.isDebugEnabled()) log.debug("Replaced " + targetContextNodeXri + " with " + replacedTargetContextNodeXri);

			if (targetContextNodeXri.equals(replacedTargetContextNodeXri)) return super.replaceRelation(relation);

			Relation replacedRelation = GraphUtil.relationFromComponents(contextNodeXri, arcXri, replacedTargetContextNodeXri);

			return replacedRelation;
		}
	};

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

	public static void main(String[] args) {

		Graph g = MemoryGraphFactory.getInstance().openGraph();
		g.setStatement(XDI3Statement.create("{$self}$to$anon$from$public$do/$get/({$self}$msg$encrypt$keypair/$is+/{})"));
		Graph g2 = MemoryGraphFactory.getInstance().openGraph();

		BootstrapInterceptor b = new BootstrapInterceptor();
		b.setBootstrapOwner(XDI3Segment.create("[=]!1111"));

		CopyUtil.copyGraph(g, g2, b.replaceSelfVariableCopyStrategy);

		System.out.println(g2.toString("XDI DISPLAY", null));
	}
}
