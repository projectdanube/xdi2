package xdi2.messaging;

import java.io.Serializable;

import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.util.StatementUtil;
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

		if (message == null || relation == null) throw new NullPointerException();

		this.message = message;
		this.relation = relation;
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
	 * @param message The XDI message to which this XDI operation belongs.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromMessageAndRelation(Message message, Relation relation) {

		if (GetOperation.isValid(relation)) return GetOperation.fromMessageAndRelation(message, relation);
		if (AddOperation.isValid(relation)) return AddOperation.fromMessageAndRelation(message, relation);
		if (ModOperation.isValid(relation)) return ModOperation.fromMessageAndRelation(message, relation);
		if (DelOperation.isValid(relation)) return DelOperation.fromMessageAndRelation(message, relation);

		return null;
	}

	/**
	 * Factory method that casts an Operation to the right subclass, e.g. to a GetOperation.
	 * @param operation The Operation to be cast.
	 * @return The casted Operation.
	 */
	public static Operation castOperation(Operation operation) {

		if (operation == null) return null;

		return fromMessageAndRelation(operation.getMessage(), operation.getRelation());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message to which this XDI operation belongs.
	 * @return An XDI message.
	 */
	public Message getMessage() {

		return this.message;
	}

	/**
	 * Returns the XDI message collection to which this XDI operation belongs.
	 * @return An XDI message collection.
	 */
	public MessageCollection getMessageCollection() {

		return this.getMessage().getMessageCollection();
	}

	/**
	 * Returns the XDI message envelope to which this XDI operation belongs.
	 * @return An XDI message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.getMessageCollection().getMessageEnvelope();
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
	 * Returns the target of the operation.
	 * @return The target of the operation.
	 */
	public XRI3Segment getTarget() {

		return this.getRelation().getTargetContextNodeXri();
	}

	public Statement getTargetStatement() {

		try {

			return StatementUtil.fromXriSegment(this.getTarget());
		} catch (Xdi2ParseException ex) {

			return null;
		}
	}

	public XRI3Segment getTargetAddress() {

		try {

			StatementUtil.fromXriSegment(this.getTarget());
			return null;
		} catch (Xdi2ParseException ex) {

			return this.getTarget();
		}
	}

	/**
	 * Returns the sender of the message's message collection.
	 * @return The sender of the message's message collection.
	 */
	public XRI3Segment getSender() {

		return this.getMessage().getMessageCollection().getSender();
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

	@Override
	public int compareTo(Operation other) {

		if (other == this || other == null) return(0);

		return this.getRelation().compareTo(other.getRelation());
	}
}
