package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * An XDI message, represented as a context node.
 * 
 * @author markus
 */
public final class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = 7063040731631258931L;

	private MessageContainer messageContainer;
	private ContextNode contextNode;

	protected Message(MessageContainer messageContainer, ContextNode contextNode) {

		if (messageContainer == null || contextNode == null) throw new NullPointerException();
		
		this.messageContainer = messageContainer;
		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI message.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI message.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return contextNode.containsContextNode(XDIMessagingConstants.XRI_SS_DO);
	}

	/**
	 * Factory method that creates an XDI message bound to a given context node.
	 * @param contextNode The context node that is an XDI message.
	 * @return The XDI message.
	 */
	public static Message fromMessageContainerAndContextNode(MessageContainer messageContainer, ContextNode contextNode) {

		return new Message(messageContainer, contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the message container to which this message belongs.
	 * @return A message container.
	 */
	public MessageContainer getMessageContainer() {

		return this.messageContainer;
	}

	/**
	 * Returns the message envelope to which this message belongs.
	 * @return A message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.getMessageContainer().getMessageEnvelope();
	}

	/**
	 * Returns the underlying context node to which this XDI message is bound.
	 * @return A context node that represents the XDI message.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/**
	 * Returns the ID of the message.
	 * @return The ID of the message.
	 */
	public XRI3SubSegment getID() {

		return this.getContextNode().getArcXri();
	}

	/**
	 * Returns the sender of the message's message container.
	 * @return The sender of the message's message container.
	 */
	public XRI3Segment getSender() {

		return this.getMessageContainer().getSender();
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
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The newly created, empty operation, or null if the operation XRI is not valid.
	 */
	public Operation createOperation(XRI3Segment operationXri, XRI3Segment targetXri) {

		Relation relation = this.getOperationsContextNode().createRelation(operationXri, targetXri);

		return Operation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $get operation and adds it to this XDI message.
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public GetOperation createGetOperation(XRI3Segment targetXri) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_GET, targetXri);

		return GetOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $add operation and adds it to this XDI message.
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The newly created $get operation.
	 */
	public AddOperation createAddOperation(XRI3Segment targetXri) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_ADD, targetXri);

		return AddOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $mod operation and adds it to this XDI message.
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The newly created $mod operation.
	 */
	public ModOperation createModOperation(XRI3Segment targetXri) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_MOD, targetXri);

		return ModOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Creates a new $del operation and adds it to this XDI message.
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The newly created $del operation.
	 */
	public DelOperation createDelOperation(XRI3Segment targetXri) {

		Relation relation = this.getOperationsContextNode().createRelation(XDIMessagingConstants.XRI_S_DEL, targetXri);

		return DelOperation.fromMessageAndRelation(this, relation);
	}

	/**
	 * Returns all XDI operations in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public Iterator<Operation> getOperations() {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations();

		return new SelectingMappingIterator<Relation, Operation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return Operation.isValid(relation);
			}

			@Override
			public Operation map(Relation relation) {

				return Operation.fromMessageAndRelation(Message.this, relation);
			}
		};
	}

	/**
	 * Returns all XDI operations with a given operation XRI in this XDI message.
	 * @return An iterator over all XDI operations.
	 */
	public Iterator<Operation> getOperations(XRI3Segment operationXri) {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(operationXri);

		return new SelectingMappingIterator<Relation, Operation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return Operation.isValid(relation);
			}

			@Override
			public Operation map(Relation relation) {

				return Operation.fromMessageAndRelation(Message.this, relation);
			}
		};
	}

	/**
	 * Returns all XDI $get operations in this XDI message.
	 * @return An iterator over all XDI $get operations.
	 */
	public Iterator<GetOperation> getGetOperations() {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_GET);

		return new SelectingMappingIterator<Relation, GetOperation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return GetOperation.isValid(relation);
			}

			@Override
			public GetOperation map(Relation relation) {

				return GetOperation.fromMessageAndRelation(Message.this, relation);
			}
		};
	}

	/**
	 * Returns all XDI $add operations in this XDI message.
	 * @return An iterator over all XDI $add operations.
	 */
	public Iterator<AddOperation> getAddOperations() {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_ADD);

		return new SelectingMappingIterator<Relation, AddOperation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return AddOperation.isValid(relation);
			}

			@Override
			public AddOperation map(Relation relation) {

				return AddOperation.fromMessageAndRelation(Message.this, relation);
			}
		};
	}

	/**
	 * Returns all XDI $mod operations in this XDI message.
	 * @return An iterator over all XDI $mod operations.
	 */
	public Iterator<ModOperation> getModOperations() {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_MOD);

		return new SelectingMappingIterator<Relation, ModOperation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return ModOperation.isValid(relation);
			}

			@Override
			public ModOperation map(Relation relation) {

				return ModOperation.fromMessageAndRelation(Message.this, relation);
			}
		};
	}

	/**
	 * Returns all XDI $del operations in this XDI message.
	 * @return An iterator over all XDI $del operations.
	 */
	public Iterator<DelOperation> getDelOperations() {

		// look for valid relations

		Iterator<Relation> relations = this.getOperationsContextNode().getRelations(XDIMessagingConstants.XRI_S_DEL);

		return new SelectingMappingIterator<Relation, DelOperation> (relations) {

			@Override
			public boolean select(Relation relation) {

				return DelOperation.isValid(relation);
			}

			@Override
			public DelOperation map(Relation relation) {

				return DelOperation.fromMessageAndRelation(Message.this, relation);
			}
		};
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

	public int compareTo(Message other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
