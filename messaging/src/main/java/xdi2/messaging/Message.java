package xdi2.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message, represented as a context node.
 * 
 * @author markus
 */
public final class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = 7063040731631258931L;

	private MessageCollection messageCollection;
	private XdiEntityMember entityMember;

	protected Message(MessageCollection messageCollection, XdiEntityMember entityMember) {

		if (messageCollection == null || entityMember == null) throw new NullPointerException();

		this.messageCollection = messageCollection;
		this.entityMember = entityMember;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity member is a valid XDI message.
	 * @param xdiEntityMember The XDI entity member to check.
	 * @return True if the XDI entity member is a valid XDI message.
	 */
	public static boolean isValid(XdiEntityMember xdiEntityMember) {

		return xdiEntityMember.getContextNode().containsContextNode(XDIMessagingConstants.XRI_SS_DO);
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity member.
	 * @param messageCollection The XDI message collection to which this XDI message belongs.
	 * @param xdiEntityMember The XDI entity member that is an XDI message.
	 * @return The XDI message.
	 */
	public static Message fromMessageCollectionAndEntityMember(MessageCollection messageCollection, XdiEntityMember xdiEntityMember) {

		if (! isValid(xdiEntityMember)) return null;

		return new Message(messageCollection, xdiEntityMember);
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
	 * Returns the underlying XDI entity member to which this XDI message is bound.
	 * @return An XDI entity member that represents the XDI message.
	 */
	public XdiEntityMember getEntityMember() {

		return this.entityMember;
	}

	/**
	 * Returns the underlying context node to which this XDI message is bound.
	 * @return A context node that represents the XDI message.
	 */
	public ContextNode getContextNode() {

		return this.getEntityMember().getContextNode();
	}

	/**
	 * Returns the ID of the message.
	 * @return The ID of the message.
	 */
	public XDI3SubSegment getID() {

		return this.getContextNode().getArcXri();
	}

	/**
	 * Returns the sender of the message's message collection.
	 * @return The sender of the message's message collection.
	 */
	public XDI3Segment getSender() {

		return this.getMessageCollection().getSender();
	}

	/**
	 * Return the FROM address.
	 */
	public XDI3Segment getFromAddress() {

		for (Iterator<Relation> incomingRelations = this.getContextNode().getIncomingRelations(); incomingRelations.hasNext(); ) {

			Relation incomingRelation = incomingRelations.next();

			if (incomingRelation.getArcXri().equals(XDIMessagingConstants.XRI_S_FROM_ADDRESS)) {

				return incomingRelation.getContextNode().getXri();
			}
		}

		return null;
	}

	/**
	 * Set the FROM address.
	 */
	public void setFromAddress(XDI3Segment fromAddress) {

		ContextNode fromAddressContextNode = this.getMessageEnvelope().getGraph().findContextNode(fromAddress, true);

		fromAddressContextNode.createRelation(XDIMessagingConstants.XRI_S_FROM_ADDRESS, this.getContextNode());
	}

	/**
	 * Return the TO address of the message.
	 */
	public XDI3Segment getToAddress() {

		Relation toAddressRelation = this.getContextNode().getRelation(XDIMessagingConstants.XRI_S_TO_ADDRESS);
		if (toAddressRelation == null) return null;

		return toAddressRelation.getTargetContextNodeXri();
	}

	/**
	 * Set the TO address of the message.
	 */
	public void setToAddress(XDI3Segment toAddress) {

		this.getContextNode().createRelation(XDIMessagingConstants.XRI_S_TO_ADDRESS, toAddress);
	}

	/**
	 * Returns the timestamp.
	 * @return The timestamp.
	 */
	public Date getTimestamp() {

		return Timestamps.getContextNodeTimestamp(this.getContextNode());
	}

	/**
	 * Returns the link contract XRI.
	 * @return The link contract XRI.
	 */
	public XDI3Segment getLinkContractXri() {

		Relation linkContractRelation = this.getContextNode().getRelation(XDILinkContractConstants.XRI_S_DO);
		if (linkContractRelation == null) return null;

		return linkContractRelation.getTargetContextNodeXri();
	}

	/**
	 * Returns an existing XDI root policy in this XDI messages, or creates a new one.
	 * @param create Whether to create an XDI root policy if it does not exist.
	 * @return The existing or newly created XDI root policy.
	 */
	public PolicyRoot getPolicyRoot(boolean create) {

		ContextNode contextNode = this.getOperationsContextNode().getContextNode(XDIPolicyConstants.XRI_SS_IF);
		if (contextNode == null && create) contextNode = this.getContextNode().createContextNode(XDIPolicyConstants.XRI_SS_IF);
		if (contextNode == null) return null;

		XdiSubGraph xdiSubGraph = XdiSubGraph.fromContextNode(contextNode);

		return PolicyRoot.fromSubGraph(xdiSubGraph);
	}

	/**
	 * Returns the context node with XDI operations.
	 * @return A context node with XDI operations.
	 */
	public ContextNode getOperationsContextNode() {

		return this.getContextNode().getContextNode(XDIMessagingConstants.XRI_SS_DO);
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation XRI is not valid.
	 */
	public Operation createOperation(XDI3Segment operationXri, XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(operationXri, targetAddress);

		return Operation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation XRI is not valid.
	 */
	public Operation createOperation(XDI3Segment operationXri, Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), operationXri, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return Operation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new operation and adds it to this XDI message.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation XRI is not valid.
	 */
	public Operation createOperation(XDI3Segment operationXri, XDI3Statement targetStatement) {

		return this.createOperation(operationXri, new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_GET, targetAddress);

		return GetOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), XDIMessagingConstants.XRI_S_GET, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return GetOperation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(XDI3Statement targetStatement) {

		return this.createGetOperation(new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Creates a new $add operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $add operation.
	 */
	public AddOperation createAddOperation(XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_ADD, targetAddress);

		return AddOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $add operation and adds it to this XDI message.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created $add operation.
	 */
	public AddOperation createAddOperation(Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), XDIMessagingConstants.XRI_S_ADD, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return AddOperation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $add operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $add operation.
	 */
	public AddOperation createAddOperation(XDI3Statement targetStatement) {

		return this.createAddOperation(new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Creates a new $mod operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $mod operation.
	 */
	public ModOperation createModOperation(XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_MOD, targetAddress);

		return ModOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $mod operation and adds it to this XDI message.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created $mod operation.
	 */
	public ModOperation createModOperation(Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), XDIMessagingConstants.XRI_S_MOD, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return ModOperation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $mod operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $mod operation.
	 */
	public ModOperation createModOperation(XDI3Statement targetStatement) {

		return this.createModOperation(new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_DEL, targetAddress);

		return DelOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), XDIMessagingConstants.XRI_S_DEL, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return DelOperation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(XDI3Statement targetStatement) {

		return this.createDelOperation(new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetAddress The target address to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(XDI3Segment targetAddress) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_DO, targetAddress);

		return DoOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetStatements The target statements to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(Iterator<XDI3Statement> targetStatements) {

		InnerRoot innerRoot = Roots.findLocalRoot(this.getContextNode().getGraph()).findInnerRoot(this.getOperationsContextNode().getXri(), XDIMessagingConstants.XRI_S_DO, true);
		while (targetStatements.hasNext()) innerRoot.createRelativeStatement(targetStatements.next());

		return DoOperation.fromMessageAndRelation(this, innerRoot.getPredicateRelation());
	}

	/**
	 * Creates a new $do operation and adds it to this XDI message.
	 * @param targetStatement The target statement to which the operation applies.
	 * @return The newly created $do operation.
	 */
	public DoOperation createDoOperation(XDI3Statement targetStatement) {

		return this.createDoOperation(new SingleItemIterator<XDI3Statement> (targetStatement));
	}

	/**
	 * Returns all XDI operations in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		// get all relations that are valid XDI operations

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(Ordering.getOrderedRelations(this.getOperationsContextNode()));
		iterators.add(this.getOperationsContextNode().getRelations());

		return new MappingRelationOperationIterator(this, new NoDuplicatesIterator<Relation> (new CompositeIterator<Relation> (iterators.iterator())));
	}

	/**
	 * Returns all XDI operations with a given operation XRI in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public ReadOnlyIterator<Operation> getOperations(XDI3Segment operationXri) {

		// get all relations that are valid XDI operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(operationXri);

		return new MappingRelationOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $get operations in this XDI message.
	 * @return An iterator over all XDI $get operations.
	 */
	public ReadOnlyIterator<GetOperation> getGetOperations() {

		// get all relations that are valid XDI $get operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_GET);

		return new MappingRelationGetOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $add operations in this XDI message.
	 * @return An iterator over all XDI $add operations.
	 */
	public ReadOnlyIterator<AddOperation> getAddOperations() {

		// get all relations that are valid XDI $add operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_ADD);

		return new MappingRelationAddOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $mod operations in this XDI message.
	 * @return An iterator over all XDI $mod operations.
	 */
	public ReadOnlyIterator<ModOperation> getModOperations() {

		// get all relations that are valid XDI $mod operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_MOD);

		return new MappingRelationModOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $del operations in this XDI message.
	 * @return An iterator over all XDI $del operations.
	 */
	public ReadOnlyIterator<DelOperation> getDelOperations() {

		// get all relations that are valid XDI $del operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_DEL);

		return new MappingRelationDelOperationIterator(this, relations);
	}

	/**
	 * Returns all XDI $do operations in this XDI message.
	 * @return An iterator over all XDI $do operations.
	 */
	public ReadOnlyIterator<DoOperation> getDoOperations() {

		// get all relations that are valid XDI $do operations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_DO);

		return new MappingRelationDoOperationIterator(this, relations);
	}

	/**
	 * Deletes all operations from this message.
	 */
	public void deleteOperations() {

		for (Operation operation : new IteratorListMaker<Operation> (this.getOperations()).list()) {

			operation.getRelation().delete();
		}
	}

	/**
	 * Returns the number of XDI operations in this XDI message.
	 */
	public int getOperationCount() {

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

	public static class MappingRelationAddOperationIterator extends NotNullIterator<AddOperation> {

		public MappingRelationAddOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, AddOperation> (relations) {

				@Override
				public AddOperation map(Relation relation) {

					return AddOperation.fromMessageAndRelation(message, relation);
				}
			});
		}
	}

	public static class MappingRelationModOperationIterator extends NotNullIterator<ModOperation> {

		public MappingRelationModOperationIterator(final Message message, Iterator<Relation> relations) {

			super(new MappingIterator<Relation, ModOperation> (relations) {

				@Override
				public ModOperation map(Relation relation) {

					return ModOperation.fromMessageAndRelation(message, relation);
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
