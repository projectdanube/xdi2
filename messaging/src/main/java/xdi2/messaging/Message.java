package xdi2.messaging;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.features.policy.PolicyRoot;
import xdi2.core.features.signatures.Signature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message, represented as a context node.
 * 
 * @author markus
 */
public final class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = 7063040731631258931L;

	private MessageCollection messageCollection;
	private XdiEntity xdiEntity;

	protected Message(MessageCollection messageCollection, XdiEntity xdiEntity) {

		if (messageCollection == null || xdiEntity == null) throw new NullPointerException();

		this.messageCollection = messageCollection;
		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI message.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI message.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		return xdiEntity.getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, false) != null;
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param messageCollection The XDI message collection to which this XDI message belongs.
	 * @param xdiEntity The XDI entity that is an XDI message.
	 * @return The XDI message.
	 */
	public static Message fromMessageCollectionAndXdiEntity(MessageCollection messageCollection, XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new Message(messageCollection, xdiEntity);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message collection to which this XDI message belongs.
	 * @return An XDI message collection.
	 */
	public MessageCollection getMessageCollection() {

		return this.messageCollection;
	}

	/**
	 * Returns the message envelope to which this message belongs.
	 * @return A message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.getMessageCollection().getMessageEnvelope();
	}

	/**
	 * Returns the underlying XDI entity to which this XDI message is bound.
	 * @return An XDI entity that represents the XDI message.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	/**
	 * Returns the underlying context node to which this XDI message is bound.
	 * @return A context node that represents the XDI message.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntity().getContextNode();
	}

	/**
	 * Returns the ID of the message.
	 * @return The ID of the message.
	 */
	public XDIArc getID() {

		return this.getContextNode().getXDIArc();
	}

	/**
	 * Returns the sender of the message's message collection.
	 * @return The sender of the message's message collection.
	 */
	public ContextNode getSender() {

		return this.getMessageCollection().getSender();
	}

	/**
	 * Returns the sender address of the message's message collection.
	 * @return The sender address of the message's message collection.
	 */
	public XDIAddress getSenderXDIAddress() {

		return this.getMessageCollection().getSenderXDIAddress();
	}

	/**
	 * Return the FROM peer root arc.
	 */
	public XDIArc getFromPeerRootXDIArc() {

		for (Iterator<Relation> incomingRelations = this.getContextNode().getIncomingRelations(); incomingRelations.hasNext(); ) {

			Relation incomingRelation = incomingRelations.next();

			if (incomingRelation.getXDIAddress().equals(XDIMessagingConstants.XDI_ADD_FROM_PEER_ROOT_ARC)) {

				XDIArc XDIarc = incomingRelation.getContextNode().getXDIArc();

				if (XdiPeerRoot.isPeerRootXDIArc(XDIarc)) return XDIarc;
			}
		}

		return null;
	}

	/**
	 * Set the FROM peer root arc.
	 */
	public void setFromPeerRootXDIArc(XDIArc fromPeerRootXDIarc) {

		this.getMessageEnvelope().getGraph().setDeepRelation(XDIAddress.fromComponent(fromPeerRootXDIarc), XDIMessagingConstants.XDI_ADD_FROM_PEER_ROOT_ARC, this.getContextNode());
	}

	/**
	 * Return the TO peer root arc of the message.
	 */
	public XDIArc getToPeerRootXDIArc() {

		Relation toPeerRootXDIarcRelation = this.getContextNode().getRelation(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC);
		if (toPeerRootXDIarcRelation == null) return null;

		XDIAddress toPeerRootAddress = toPeerRootXDIarcRelation.getTargetContextNodeXDIAddress();
		if (toPeerRootAddress.getNumXDIArcs() > 1 || ! XdiPeerRoot.isPeerRootXDIArc(toPeerRootAddress.getFirstXDIArc())) return null;

		return toPeerRootAddress.getFirstXDIArc();
	}

	/**
	 * Set the TO peer root arc of the message.
	 */
	public void setToPeerRootXDIArc(XDIArc toPeerRootXDIarc) {

		this.getContextNode().delRelations(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC);

		if (toPeerRootXDIarc != null) {

			this.getContextNode().setRelation(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC, XDIAddress.fromComponent(toPeerRootXDIarc));
		}
	}

	/**
	 * Returns the timestamp.
	 * @return The timestamp.
	 */
	public Date getTimestamp() {

		return Timestamps.getContextNodeTimestamp(this.getContextNode());
	}

	/**
	 * Set the timestamp.
	 */
	public void setTimestamp(Date timestamp) {

		Timestamps.setContextNodeTimestamp(this.getContextNode(), timestamp);
	}

	/**
	 * Returns the link contract address.
	 * @return The link contract address.
	 */
	public XDIAddress getLinkContractXDIAddress() {

		Relation linkContractRelation = this.getContextNode().getRelation(XDILinkContractConstants.XDI_ADD_DO);
		if (linkContractRelation == null) return null;

		return linkContractRelation.getTargetContextNodeXDIAddress();
	}

	/**
	 * Set the link contract address.
	 */
	public void setLinkContractXDIAddress(XDIAddress linkContractAddress) {

		this.getContextNode().delRelations(XDILinkContractConstants.XDI_ADD_DO);
		this.getContextNode().setRelation(XDILinkContractConstants.XDI_ADD_DO, linkContractAddress);
	}

	/**
	 * Set a link contract class.
	 */
	public void setLinkContract(Class<? extends LinkContract> clazz) {

		XDIAddress ownerAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(this.getToPeerRootXDIArc());
		if (ownerAddress == null) throw new Xdi2RuntimeException("No TO peer root arc has been set yet.");

		if (RootLinkContract.class.isAssignableFrom(clazz)) {

			this.setLinkContractXDIAddress(RootLinkContract.createRootLinkContractXDIAddress(ownerAddress));
		} else if (PublicLinkContract.class.isAssignableFrom(clazz)) {

			this.setLinkContractXDIAddress(PublicLinkContract.createPublicLinkContractXDIAddress(ownerAddress));
		} else {

			throw new Xdi2RuntimeException("Cannot automatically set link contract of type " + clazz.getSimpleName());
		}
	}

	/**
	 * Returns an existing XDI root policy in this XDI messages, or creates a new one.
	 * @param create Whether to create an XDI root policy if it does not exist.
	 * @return The existing or newly created XDI root policy.
	 */
	public PolicyRoot getPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getOperationsXdiEntity().getXdiEntitySingleton(XDIPolicyConstants.XDI_ARC_IF, create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/*
	 * Methods releated to message authentication
	 */

	/**
	 * Set a secret token on the message.
	 * @param secretToken The secret token to set.
	 */
	public void setSecretToken(String secretToken) {

		if (secretToken != null) {

			XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(this.getContextNode().setDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN));
			XdiValue xdiValue = xdiAttribute.getXdiValue(true);
			xdiValue.getContextNode().setLiteral(secretToken);
		} else {

			XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(this.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN, true));
			XdiValue xdiValue = xdiAttribute == null ? null : xdiAttribute.getXdiValue(false);
			if (xdiValue != null) xdiValue.getContextNode().delete();
		}
	}

	/**
	 * Returns the secret token from the message.
	 * @return The secret token.
	 */
	public String getSecretToken() {

		ContextNode contextNode = this.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN, true);
		if (contextNode == null) return null;

		XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(contextNode);
		if (xdiAttribute == null) return null;

		XdiValue xdiValue = xdiAttribute.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal literal = xdiValue.getContextNode().getLiteral();
		if (literal == null) return null;

		return literal.getLiteralDataString();
	}

	/**
	 * Returns the signature from the message.
	 * @return The signature.
	 */
	public ReadOnlyIterator<Signature<?, ?>> getSignatures() {

		return Signatures.getSignatures(this.getContextNode());
	}

	/**
	 * Sets a signature on the message.
	 * @return The signature.
	 */
	public Signature<? extends Key, ? extends Key> createSignature(String digestAlgorithm, int digestLength, String keyAlgorithm, int keyLength, boolean singleton) {

		return Signatures.createSignature(this.getContextNode(), digestAlgorithm, digestLength, keyAlgorithm, keyLength, singleton);
	}

	/*
	 * Methods related to message types
	 */

	public Iterator<XDIAddress> getMessageTypes() {

		return Dictionary.getContextNodeTypes(this.getContextNode());
	}

	public XDIAddress getMessageType() {

		return Dictionary.getContextNodeType(this.getContextNode());
	}

	public boolean isMessageType(XDIAddress type) {

		return Dictionary.isContextNodeType(this.getContextNode(), type);
	}

	public void setMessageType(XDIAddress type) {

		Dictionary.setContextNodeType(this.getContextNode(), type);
	}

	public void delMessageType(XDIAddress type) {

		Dictionary.delContextNodeType(this.getContextNode(), type);
	}

	public void delMessageTypes() {

		Dictionary.delContextNodeTypes(this.getContextNode());
	}

	public void replaceMessageType(XDIAddress type) {

		Dictionary.replaceContextNodeType(this.getContextNode(), type);
	}

	/*
	 * Methods related to operations
	 */

	/**
	 * Returns the XDI entity with XDI operations.
	 * @return A XDI entity with XDI operations.
	 */
	public XdiEntity getOperationsXdiEntity() {

		return this.getXdiEntity().getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, true);
	}

	/**
	 * Returns the context node with XDI operations.
	 * @return A context node with XDI operations.
	 */
	public ContextNode getOperationsContextNode() {

		return this.getOperationsXdiEntity().getContextNode();
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationAddress The operation address to use for the new operation.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation address is not valid.
	 */
	public Operation createOperation(XDIAddress operationAddress, XDIAddress targetAddress) {

		Relation relation = this.getOperationsContextNode().setRelation(operationAddress, targetAddress);

		return Operation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationAddress The operation address to use for the new operation.
	 * @param targetStatementAddresses The target statements to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation address is not valid.
	 */
	public Operation createOperation(XDIAddress operationAddress, Iterator<XDIStatement> targetStatementAddresses) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getContextNode().getGraph()).getInnerRoot(this.getOperationsContextNode().getXDIAddress(), operationAddress, true);
		if (targetStatementAddresses != null) while (targetStatementAddresses.hasNext()) xdiInnerRoot.getContextNode().setStatement(targetStatementAddresses.next());

		return Operation.fromMessageAndRelation(this, xdiInnerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationAddress The operation address to use for the new operation.
	 * @param targetStatementAddress The target statement to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation address is not valid.
	 */
	public Operation createOperation(XDIAddress operationAddress, XDIStatement targetStatementAddress) {

		return this.createOperation(operationAddress, new SingleItemIterator<XDIStatement> (targetStatementAddress));
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationAddress The operation address to use for the new operation.
	 * @param targetGraph The target graph with statements to which this operation applies.
	 * @return The newly created, empty operation, or null if the operation address is not valid.
	 */
	public Operation createOperation(XDIAddress operationAddress, Graph targetGraph) {

		return this.createOperation(operationAddress, new MappingXDIStatementIterator(new SelectingNotImpliedStatementIterator(targetGraph.getAllStatements())));
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationAddress The operation address to use for the new operation.
	 * @param target The target address or target statement to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation address is not valid.
	 */
	public Operation createOperation(XDIAddress operationAddress, String target) {

		try {

			return this.createOperation(operationAddress, XDIAddress.create(target));
		} catch (Exception ex) {

			return this.createOperation(operationAddress, XDIStatement.create(target));
		}
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(XDIAddress targetAddress) {

		Relation relation = this.getOperationsContextNode().setRelation(XDIMessagingConstants.XDI_ADD_GET, targetAddress);

		return GetOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetStatementAddresses The target statements to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(Iterator<XDIStatement> targetStatementAddresses) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getContextNode().getGraph()).getInnerRoot(this.getOperationsContextNode().getXDIAddress(), XDIMessagingConstants.XDI_ADD_GET, true);
		if (targetStatementAddresses != null) while (targetStatementAddresses.hasNext()) xdiInnerRoot.getContextNode().setStatement(targetStatementAddresses.next());

		return GetOperation.fromMessageAndRelation(this, xdiInnerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(XDIStatement targetStatement) {

		return this.createGetOperation(new SingleItemIterator<XDIStatement> (targetStatement));
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetGraph The target graph with statements to which this operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(Graph targetGraph) {

		return this.createGetOperation(new MappingXDIStatementIterator(new SelectingNotImpliedStatementIterator(targetGraph.getAllStatements())));
	}

	/**
	 * Creates a new $set operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $set operation.
	 */
	public SetOperation createSetOperation(XDIAddress targetAddress) {

		Relation relation = this.getOperationsContextNode().setRelation(XDIMessagingConstants.XDI_ADD_SET, targetAddress);

		return SetOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $set operation and adds it to this XDI message.
	 * @param targetStatementAddresses The target statements to which the operation applies.
	 * @return The newly created $set operation.
	 */
	public SetOperation createSetOperation(Iterator<XDIStatement> targetStatementAddresses) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getContextNode().getGraph()).getInnerRoot(this.getOperationsContextNode().getXDIAddress(), XDIMessagingConstants.XDI_ADD_SET, true);
		if (targetStatementAddresses != null) while (targetStatementAddresses.hasNext()) xdiInnerRoot.getContextNode().setStatement(targetStatementAddresses.next());

		return SetOperation.fromMessageAndRelation(this, xdiInnerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $set operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $set operation.
	 */
	public SetOperation createSetOperation(XDIStatement targetStatement) {

		return this.createSetOperation(new SingleItemIterator<XDIStatement> (targetStatement));
	}

	/**
	 * Creates a new $set operation and adds it to this XDI message.
	 * @param targetGraph The target graph with statements to which this operation applies.
	 * @return The newly created $set operation.
	 */
	public SetOperation createSetOperation(Graph targetGraph) {

		return this.createSetOperation(new MappingXDIStatementIterator(new SelectingNotImpliedStatementIterator(targetGraph.getAllStatements())));
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(XDIAddress targetAddress) {

		Relation relation = this.getOperationsContextNode().setRelation(XDIMessagingConstants.XDI_ADD_DEL, targetAddress);

		return DelOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetStatementAddresses The target statements to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(Iterator<XDIStatement> targetStatementAddresses) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getContextNode().getGraph()).getInnerRoot(this.getOperationsContextNode().getXDIAddress(), XDIMessagingConstants.XDI_ADD_DEL, true);
		if (targetStatementAddresses != null) while (targetStatementAddresses.hasNext()) xdiInnerRoot.getContextNode().setStatement(targetStatementAddresses.next());

		return DelOperation.fromMessageAndRelation(this, xdiInnerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(XDIStatement targetStatement) {

		return this.createDelOperation(new SingleItemIterator<XDIStatement> (targetStatement));
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetGraph The target graph with statements to which this operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(Graph targetGraph) {

		return this.createDelOperation(new MappingXDIStatementIterator(new SelectingNotImpliedStatementIterator(targetGraph.getAllStatements())));
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(XDIAddress targetAddress) {

		Relation relation = this.getOperationsContextNode().setRelation(XDIMessagingConstants.XDI_ADD_DO, targetAddress);

		return DoOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetStatementAddresses The target statements to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(Iterator<XDIStatement> targetStatementAddresses) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getContextNode().getGraph()).getInnerRoot(this.getOperationsContextNode().getXDIAddress(), XDIMessagingConstants.XDI_ADD_DO, true);
		if (targetStatementAddresses != null) while (targetStatementAddresses.hasNext()) xdiInnerRoot.getContextNode().setStatement(targetStatementAddresses.next());

		return DoOperation.fromMessageAndRelation(this, xdiInnerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(XDIStatement targetStatement) {

		return this.createDoOperation(new SingleItemIterator<XDIStatement> (targetStatement));
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetGraph The target graph with statements to which this operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(Graph targetGraph) {

		return this.createDoOperation(new MappingXDIStatementIterator(new SelectingNotImpliedStatementIterator(targetGraph.getAllStatements())));
	}

	/**
	 * Returns all XDI operations in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		// get all relations that are valid XDI operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations();

		return new MappingRelationOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI operations with a given operation address in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public ReadOnlyIterator<Operation> getOperations(XDIAddress operationAddress) {

		// get all relations that are valid XDI operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(operationAddress);

		return new MappingRelationOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $get operations in this XDI message.
	 * @return An iterator over all XDI $get operations.
	 */
	public ReadOnlyIterator<GetOperation> getGetOperations() {

		// get all relations that are valid XDI $get operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XDI_ADD_GET);

		return new MappingRelationGetOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $set operations in this XDI message.
	 * @return An iterator over all XDI $set operations.
	 */
	public ReadOnlyIterator<SetOperation> getSetOperations() {

		// get all relations that are valid XDI $set operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XDI_ADD_SET);

		return new MappingRelationSetOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $del operations in this XDI message.
	 * @return An iterator over all XDI $del operations.
	 */
	public ReadOnlyIterator<DelOperation> getDelOperations() {

		// get all relations that are valid XDI $del operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XDI_ADD_DEL);

		return new MappingRelationDelOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $do operations in this XDI message.
	 * @return An iterator over all XDI $do operations.
	 */
	public ReadOnlyIterator<DoOperation> getDoOperations() {

		// get all relations that are valid XDI $do operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XDI_ADD_DO);

		return new MappingRelationDoOperationIterator(this, relations);
	}

	/**
	 * Deletes all operations from this message.
	 */
	public void deleteOperations() {

		for (Operation operation : new IteratorListMaker<Operation> (this.getOperations()).list()) {

			XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(operation.getRelation().follow());

			if (innerRoot != null) {

				innerRoot.getContextNode().delete();
			} else {

				operation.getRelation().delete();
			}
		}
	}

	/**
	 * Returns the number of XDI operations in this XDI message.
	 */
	public long getOperationCount() {

		Iterator<Operation> iterator = this.getOperations();

		return new IteratorCounter(iterator).count();
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Message)) return false;
		if (object == this) return true;

		Message other = (Message) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Message other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingRelationOperationIterator extends NotNullIterator<Operation> {

		public MappingRelationOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, Operation> (relations) {

				@Override
				public Operation map(Relation relation) {

					return Operation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}

	public static class MappingRelationGetOperationIterator extends NotNullIterator<GetOperation> {

		public MappingRelationGetOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, GetOperation> (relations) {

				@Override
				public GetOperation map(Relation relation) {

					return GetOperation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}

	public static class MappingRelationSetOperationIterator extends NotNullIterator<SetOperation> {

		public MappingRelationSetOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, SetOperation> (relations) {

				@Override
				public SetOperation map(Relation relation) {

					return SetOperation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}

	public static class MappingRelationDelOperationIterator extends NotNullIterator<DelOperation> {

		public MappingRelationDelOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, DelOperation> (relations) {

				@Override
				public DelOperation map(Relation relation) {

					return DelOperation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}

	public static class MappingRelationDoOperationIterator extends NotNullIterator<DoOperation> {

		public MappingRelationDoOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, DoOperation> (relations) {

				@Override
				public DoOperation map(Relation relation) {

					return DoOperation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}
}
