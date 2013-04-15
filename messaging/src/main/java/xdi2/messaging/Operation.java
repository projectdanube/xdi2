package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiAbstractSubGraph;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.features.roots.XdiInnerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

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
				SetOperation.isValid(relation) ||
				DelOperation.isValid(relation) ||
				DoOperation.isValid(relation);
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param message The XDI message to which this XDI operation belongs.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromMessageAndRelation(Message message, Relation relation) {

		if (GetOperation.isValid(relation)) return new GetOperation(message, relation);
		if (AddOperation.isValid(relation)) return new AddOperation(message, relation);
		if (ModOperation.isValid(relation)) return new ModOperation(message, relation);
		if (SetOperation.isValid(relation)) return new SetOperation(message, relation);
		if (DelOperation.isValid(relation)) return new DelOperation(message, relation);
		if (DoOperation.isValid(relation)) return new DoOperation(message, relation);

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
	 * @return A relation that represents the XDI operation.
	 */
	public Relation getRelation() {

		return this.relation;
	}

	/**
	 * Returns the operation XRI of the XDI operation (e.g. $get, $mod).
	 * @return The operation XRI of the XDI operation.
	 */
	public XDI3Segment getOperationXri() {

		return this.getRelation().getArcXri();
	}

	/**
	 * Returns the target address of the operation.
	 * @return The target address of the operation.
	 */
	public XDI3Segment getTargetAddress() {

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(this.getRelation().follow());

		if (innerRoot != null) {

			return null;
		} else {

			return this.getRelation().getTargetContextNodeXri();
		}
	}

	/**
	 * Returns the target statements of the operation.
	 * @return The target statements of the operation.
	 */
	public Iterator<XDI3Statement> getTargetStatements() {

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(this.getRelation().follow());

		if (innerRoot != null) {

			return innerRoot.getRelativeStatements(true);
		} else {

			return null;
		}
	}

	/**
	 * Sets a parameter value of this operation.
	 * @param parameterXri The parameter XRI.
	 * @param parameterValue The parameter value.
	 */
	public void setParameter(XDI3SubSegment parameterXri, Object parameterValue) {

		XdiEntity parametersXdiEntity = XdiAbstractSubGraph.fromContextNode(this.getMessage().getContextNode()).getXdiEntitySingleton(this.getOperationXri().getFirstSubSegment(), true);
		XdiAttribute parameterXdiAttribute = parametersXdiEntity.getXdiAttributeSingleton(parameterXri, true);
		XdiValue xdiValue = parameterXdiAttribute.getXdiValue(true);
		Literal parameterLiteral = xdiValue.getContextNode().getLiteral();

		if (parameterLiteral == null) 
			parameterLiteral = xdiValue.getContextNode().createLiteral(parameterValue.toString()); 
		else 
			parameterLiteral.setLiteralData(parameterValue.toString());
	}

	/**
	 * Returns a parameter value of this operation.
	 * @param parameterXri The parameter XRI.
	 * @return The parameter value.
	 */
	public String getParameter(XDI3SubSegment parameterXri) {

		XdiEntity parametersXdiEntity = XdiAbstractSubGraph.fromContextNode(this.getMessage().getContextNode()).getXdiEntitySingleton(this.getOperationXri().getFirstSubSegment(), false);
		if (parametersXdiEntity == null) return null;

		XdiAttribute parameterXdiAttribute = parametersXdiEntity.getXdiAttributeSingleton(parameterXri, false);
		if (parameterXdiAttribute == null) return null;

		XdiValue xdiValue = parameterXdiAttribute.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal parameterLiteral = xdiValue.getContextNode().getLiteral();
		if (parameterLiteral == null) return null;

		return parameterLiteral.getLiteralData();
	}
	/**
	 * Returns a parameter value of this operation as a boolean.
	 * @param parameterXri The parameter XRI.
	 * @return The parameter value.
	 */
	public Boolean getParameterAsBoolean(XDI3SubSegment parameterXri) {

		return Boolean.valueOf(this.getParameter(parameterXri));
	}

	/**
	 * Returns the sender of the message's message collection.
	 * @return The sender of the message's message collection.
	 */
	public XDI3Segment getSender() {

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
		if (this instanceof SetOperation) return true;
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
