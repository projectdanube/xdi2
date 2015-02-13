package xdi2.messaging.target.interceptor.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.features.policy.PolicyAnd;
import xdi2.core.features.policy.PolicyOr;
import xdi2.core.features.policy.PolicyUtil;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IteratorArrayMaker;
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

	public final static XDIArc XDI_ARC_SELF = XDIArc.create("{$self}");

	private XDIAddress bootstrapOwner;
	private XDIAddress[] bootstrapOwnerSynonyms;
	private boolean bootstrapRootLinkContract;
	private boolean bootstrapPublicLinkContract;
	private boolean bootstrapTimestamp;
	private Graph bootstrapGraph;
	private MessageEnvelope bootstrapMessageEnvelope;

	public BootstrapInterceptor() {

		super(INIT_PRIORITY, SHUTDOWN_PRIORITY);

		this.bootstrapOwner = null;
		this.bootstrapOwnerSynonyms = null;
		this.bootstrapRootLinkContract = false;
		this.bootstrapPublicLinkContract = false;
		this.bootstrapTimestamp = false;
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

		interceptor.setBootstrapOwner(prototypingContext.getOwnerXDIAddress());
		interceptor.setBootstrapRootLinkContract(this.getBootstrapRootLinkContract());
		interceptor.setBootstrapPublicLinkContract(this.getBootstrapPublicLinkContract());

		// set the owner synonyms

		XDIAddress[] bootstrapOwnerSynonyms = null;

		if (prototypingContext.getOwnerPeerRoot() != null) {

			Iterator<ContextNode> ownerSynonymPeerRootContextNodes = Equivalence.getIncomingReferenceContextNodes(prototypingContext.getOwnerPeerRoot().getContextNode());
			XdiPeerRoot[] ownerSynonymPeerRoots = (new IteratorArrayMaker<XdiPeerRoot> (new MappingContextNodePeerRootIterator(ownerSynonymPeerRootContextNodes))).array(XdiPeerRoot.class);

			bootstrapOwnerSynonyms = new XDIAddress[ownerSynonymPeerRoots.length];
			for (int i=0; i<bootstrapOwnerSynonyms.length; i++) bootstrapOwnerSynonyms[i] = ownerSynonymPeerRoots[i].getXDIAddressOfPeerRoot();
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

		if (XdiCommonRoot.findCommonRoot(graph).getSelfPeerRoot() != null) return;

		// create bootstrap owner

		ContextNode bootstrapOwnerContextNode = null;
		ContextNode bootstrapOwnerSelfPeerRootContextNode = null;

		if (this.getBootstrapOwner() != null) {

			if (log.isDebugEnabled()) log.debug("Creating bootstrap owner: " + this.getBootstrapOwner());

			bootstrapOwnerContextNode = graph.setDeepContextNode(this.getBootstrapOwner());
			bootstrapOwnerSelfPeerRootContextNode = XdiCommonRoot.findCommonRoot(graph).setSelfPeerRoot(this.getBootstrapOwner()).getContextNode();

			// create bootstrap owner synonyms

			if (this.getBootstrapOwnerSynonyms() != null) {

				if (log.isDebugEnabled()) log.debug("Creating bootstrap owner synonyms: " + Arrays.asList(this.getBootstrapOwnerSynonyms()));

				for (XDIAddress bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

					ContextNode bootstrapOwnerSynonymContextNode = graph.setDeepContextNode(bootstrapOwnerSynonym);

					bootstrapOwnerSynonymContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_REF);
					bootstrapOwnerSynonymContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, bootstrapOwnerContextNode);

					bootstrapOwnerContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_IS_REF);
					bootstrapOwnerContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_REF, bootstrapOwnerSynonymContextNode);

					ContextNode bootstrapOwnerSynonymPeerRootContextNode = XdiCommonRoot.findCommonRoot(graph).getPeerRoot(bootstrapOwnerSynonym, true).getContextNode();

					bootstrapOwnerSynonymPeerRootContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_REF);
					bootstrapOwnerSynonymPeerRootContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, bootstrapOwnerSelfPeerRootContextNode);
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
			bootstrapRootLinkContract.setPermissionTargetXDIAddress(XDILinkContractConstants.XDI_ADD_ALL, XDIConstants.XDI_ADD_ROOT);

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
			XDIAddress publicAddress = XDIAddressUtil.concatXDIAddresses(this.getBootstrapOwner(), XDILinkContractConstants.XDI_ADD_PUBLIC);
			bootstrapPublicLinkContract.setPermissionTargetXDIAddress(XDILinkContractConstants.XDI_ADD_GET, publicAddress);

			XDIStatement selfPeerRootRefStatement = XDIStatement.fromRelationComponents(XDIConstants.XDI_ADD_ROOT, XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_COMMON_VARIABLE);
			bootstrapPublicLinkContract.setPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_GET, selfPeerRootRefStatement);

			XDIStatement bootstrapOwnerSynonymsIsRefStatement = XDIStatement.fromRelationComponents(this.getBootstrapOwner(), XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_COMMON_VARIABLE);
			bootstrapPublicLinkContract.setPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_GET, bootstrapOwnerSynonymsIsRefStatement);

			if (this.getBootstrapOwnerSynonyms() != null) {

				for (XDIAddress bootstrapOwnerSynonym : this.getBootstrapOwnerSynonyms()) {

					XDIStatement bootstrapOwnerSynonymRefStatement = XDIStatement.fromRelationComponents(bootstrapOwnerSynonym, XDIDictionaryConstants.XDI_ADD_REF, this.getBootstrapOwner());
					bootstrapPublicLinkContract.setPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_GET, bootstrapOwnerSynonymRefStatement);
				}
			}
		}

		// create bootstrap timestamp

		if (this.getBootstrapTimestamp()) {

			Timestamps.setContextNodeTimestamp(graph.getRootContextNode(), XDITimestampsConstants.XDI_ADD_AS_CREATED, new Date());
		}

		// create bootstrap graph

		if (this.getBootstrapGraph() != null) {

			CopyStrategy copyStrategy = new ReplaceXDIAddressCopyStrategy(XDI_ARC_SELF, BootstrapInterceptor.this.getBootstrapOwner());

			Graph bootstrapGraph = MemoryGraphFactory.getInstance().openGraph();
			CopyUtil.copyGraph(this.getBootstrapGraph(), bootstrapGraph, copyStrategy);

			if (log.isDebugEnabled()) log.debug("Creating bootstrap graph: " + bootstrapGraph.toString());

			CopyUtil.copyGraph(bootstrapGraph, graph, null);

			bootstrapGraph.close();
		}

		// execute bootstrap message envelope

		if (this.getBootstrapMessageEnvelope() != null) {

			CopyStrategy copyStrategy = new ReplaceXDIAddressCopyStrategy(XDI_ARC_SELF, BootstrapInterceptor.this.getBootstrapOwner());

			MessageEnvelope bootstrapMessageEnvelope = new MessageEnvelope();
			CopyUtil.copyGraph(this.getBootstrapMessageEnvelope().getGraph(), bootstrapMessageEnvelope.getGraph(), copyStrategy);

			if (log.isDebugEnabled()) log.debug("Executing bootstrap message envelope: " + bootstrapMessageEnvelope.getGraph().toString());

			ToInterceptor toInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(ToInterceptor.class);
			if (toInterceptor != null) toInterceptor.setDisabledForMessageEnvelope(bootstrapMessageEnvelope);

			RefInterceptor refInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(RefInterceptor.class);
			if (refInterceptor != null) refInterceptor.setDisabledForMessageEnvelope(bootstrapMessageEnvelope);

			LinkContractInterceptor linkContractInterceptor = graphMessagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessageEnvelope(bootstrapMessageEnvelope);

			graphMessagingTarget.execute(bootstrapMessageEnvelope, null, null);
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);
	}

	/*
	 * Getters and setters
	 */

	public XDIAddress getBootstrapOwner() {

		return this.bootstrapOwner;
	}

	public void setBootstrapOwner(XDIAddress bootstrapOwner) {

		this.bootstrapOwner = bootstrapOwner;
	}

	public XDIAddress[] getBootstrapOwnerSynonyms() {

		return this.bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(XDIAddress[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = bootstrapOwnerSynonyms;
	}

	public void setBootstrapOwnerSynonyms(String[] bootstrapOwnerSynonyms) {

		this.bootstrapOwnerSynonyms = new XDIAddress[bootstrapOwnerSynonyms.length];
		for (int i=0; i<this.bootstrapOwnerSynonyms.length; i++) this.bootstrapOwnerSynonyms[i] = XDIAddress.create(bootstrapOwnerSynonyms[i]);
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

	public boolean getBootstrapTimestamp() {

		return this.bootstrapTimestamp;
	}

	public void setBootstrapTimestamp(boolean bootstrapTimestamp) {

		this.bootstrapTimestamp = bootstrapTimestamp;
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
