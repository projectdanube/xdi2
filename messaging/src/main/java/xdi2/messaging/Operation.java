package xdi2.messaging;

import java.io.Serializable;

import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * An XDI messaging operation, represented as a relation.
 * 
 * @author markus
 */
public abstract class Operation implements Serializable, Comparable<Operation> {

	private static final long serialVersionUID = 8816045435464636862L;

	protected Message message;
	protected Relation relation;

	protected Operation(Message message, Relation relation) {

		this.message = message;
		this.relation = relation;

		if (this.message == null) this.message = Message.fromContextNode(relation.getContextNode().getContextNode());
	}

	/*
	 * Static methods
	 */
	
	/**
	 * Checks if an relation is a valid XDI operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI operation.
	 */
	public static boolean isValid(Relation relation) {

		return
				GetOperation.isValid(relation) ||
				AddOperation.isValid(relation) ||
				ModOperation.isValid(relation) ||
				DelOperation.isValid(relation);
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromRelation(Relation relation) {

		if (GetOperation.isValid(relation)) return GetOperation.fromRelation(relation);
		if (AddOperation.isValid(relation)) return AddOperation.fromRelation(relation);
		if (ModOperation.isValid(relation)) return ModOperation.fromRelation(relation);
		if (DelOperation.isValid(relation)) return DelOperation.fromRelation(relation);

		return(null);
	}

	/**
	 * Factory method that casts an Operation to the right subclass, e.g. to a GetOperation.
	 * @param operation The Operation to be cast.
	 * @return The casted Operation.
	 */
	public static Operation castOperation(Operation operation) {

		if (operation == null) return null;

		return fromRelation(operation.getRelation());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the message to which this operation belongs.
	 * @return A message.
	 */
	public Message getMessage() {

		return this.message;
	}

	/**
	 * Returns the message container to which this operation belongs.
	 * @return A message container.
	 */
	public MessageContainer getMessageContainer() {

		return this.getMessage().getMessageContainer();
	}

	/**
	 * Returns the message envelope to which this operation belongs.
	 * @return A message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.getMessageContainer().getMessageEnvelope();
	}

	/**
	 * Returns the underlying relation to which this XDI operation is bound.
	 * @return A relation that represents an XDI operation.
	 */
	public Relation getRelation() {

		return this.relation;
	}

	/**
	 * Returns the operation XRI of the operation (e.g. $get, $mod).
	 * @return The operation XRI of the operation.
	 */
	public XRI3Segment getOperationXri() {

		return this.getRelation().getArcXri();
	}

	/**
	 * Returns the target XRI of the operation.
	 * @return The target XRI of the operation.
	 */
	public XRI3Segment getTargetXri() {

		return this.getRelation().getRelationXri();
	}

	/**
	 * Returns the sender of the message's message container.
	 * @return The sender of the message's message container.
	 */
	public XRI3Segment getSender() {

		return this.getMessage().getMessageContainer().getSender();
	}

	/**
	 * Is this a read operation?
	 */
	public boolean isReadOperation() {

		if (this instanceof GetOperation) return true;

		return false;
	}

	/**
	 * Is this a write operation?
	 */
	public boolean isWriteOperation() {

		if (this instanceof AddOperation) return true;
		if (this instanceof ModOperation) return true;
		if (this instanceof DelOperation) return true;

		return false;
	}

	/*
	 * Object methods
	 */
	
	@Override
	public String toString() {

		return this.getRelation().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Operation)) return(false);
		if (object == this) return(true);

		Operation other = (Operation) object;

		return this.getRelation().equals(other.getRelation());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getRelation().hashCode();

		return hashCode;
	}

	public int compareTo(Operation other) {

		if (other == this || other == null) return(0);

		return this.getRelation().compareTo(other.getRelation());
	}
}
