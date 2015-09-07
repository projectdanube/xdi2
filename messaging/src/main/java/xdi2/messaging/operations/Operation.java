package xdi2.messaging.operations;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAbstractVariable.MappingContextNodeXdiVariableIterator;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeXDIStatementIterator;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;

/**
 * An XDI messaging operation, represented as a relation.
 * 
 * @author markus
 */
public abstract class Operation implements Serializable, Comparable<Operation> {

	private static final long serialVersionUID = 8816045435464636862L;

	private static final Logger log = LoggerFactory.getLogger(Operation.class);

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
				SetOperation.isValid(relation) ||
				DelOperation.isValid(relation) ||
				DoOperation.isValid(relation) ||
				ConnectOperation.isValid(relation) ||
				SendOperation.isValid(relation) ||
				PushOperation.isValid(relation);
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param message The XDI message to which this XDI operation belongs.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromMessageAndRelation(Message message, Relation relation) {

		if (GetOperation.isValid(relation)) return new GetOperation(message, relation);
		if (SetOperation.isValid(relation)) return new SetOperation(message, relation);
		if (DelOperation.isValid(relation)) return new DelOperation(message, relation);
		if (DoOperation.isValid(relation)) return new DoOperation(message, relation);
		if (ConnectOperation.isValid(relation)) return new ConnectOperation(message, relation);
		if (SendOperation.isValid(relation)) return new SendOperation(message, relation);
		if (PushOperation.isValid(relation)) return new PushOperation(message, relation);

		return null;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromRelation(Relation relation) {

		XdiEntity xdiEntity = XdiAbstractEntity.fromContextNode(relation.getContextNode());

		Message message = xdiEntity == null ? null : Message.fromXdiEntity(xdiEntity);
		if (xdiEntity == null) return null;

		return fromMessageAndRelation(message, relation);
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
	 * Returns the operation identifier of the XDI operation (e.g. $get, $mod).
	 * @return The operation identifier of the XDI operation.
	 */
	public XDIAddress getOperationXDIAddress() {

		return this.getRelation().getXDIAddress();
	}

	/**
	 * Returns the target inner root of the operation.
	 * @return The target inner root of the operation.
	 */
	public XdiInnerRoot getTargetXdiInnerRoot() {

		ContextNode targetContextNode = this.getRelation().followContextNode();
		if (targetContextNode == null) return null;

		XdiInnerRoot xdiInnerRoot = XdiInnerRoot.fromContextNode(targetContextNode);
		if (xdiInnerRoot == null) return null;

		if (! xdiInnerRoot.getSubjectOfInnerRoot().equals(this.getRelation().getContextNode().getXDIAddress())) return null;
		if (! xdiInnerRoot.getPredicateOfInnerRoot().equals(this.getRelation().getXDIAddress())) return null;

		return xdiInnerRoot;
	}

	/**
	 * Returns the target address of the operation.
	 * @return The target address of the operation.
	 */
	public XDIAddress getTargetXDIAddress() {

		XdiInnerRoot targetInnerRoot = this.getTargetXdiInnerRoot();

		if (targetInnerRoot != null) {

			return null;
		} else {

			return this.getRelation().getTargetXDIAddress();
		}
	}

	/**
	 * Returns the target statements of the operation.
	 * @return The target statements of the operation.
	 */
	public IterableIterator<XDIStatement> getTargetXDIStatements() {

		XdiInnerRoot targetInnerRoot = this.getTargetXdiInnerRoot();

		if (targetInnerRoot != null) {

			return new MappingAbsoluteToRelativeXDIStatementIterator(
					targetInnerRoot,
					new MappingXDIStatementIterator(
							new SelectingNotImpliedStatementIterator(
									targetInnerRoot.getContextNode().getAllStatements())));
		} else {

			return null;
		}
	}

	/**
	 * Returns the sender of the operation's message collection.
	 * @return The sender of the operation's message collection.
	 */
	public ContextNode getSender() {

		return this.getMessage().getMessageCollection().getSender();
	}

	/**
	 * Returns the sender address of the operation's message collection.
	 * @return The sender address of the operation's message collection.
	 */
	public XDIAddress getSenderXDIAddress() {

		return this.getMessage().getMessageCollection().getSenderXDIAddress();
	}

	/**
	 * Is this a read-only operation?
	 */
	public boolean isReadOnlyOperation() {

		if (this instanceof GetOperation) return true;

		return false;
	}

	/*
	 * Operation parameters and variable values
	 */

	/**
	 * Sets a parameter value of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @param parameterValue The parameter value.
	 */
	public void setParameter(XDIAddress parameterXDIAddress, Object parameterValue) {

		XdiEntitySingleton parameterXdiEntity = this.getMessage().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), true);
		XdiAttributeSingleton parameterXdiAttribute = parameterXdiEntity.getXdiAttributeSingleton(parameterXDIAddress, true);

		parameterXdiAttribute.setLiteralData(parameterValue);
	}

	/**
	 * Returns a parameter value of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @return The parameter value.
	 */
	public Object getParameter(XDIAddress parameterXDIAddress) {

		LiteralNode parameterLiteral = this.getParameterLiteralNode(parameterXDIAddress);
		if (parameterLiteral == null) return null;

		return parameterLiteral.getLiteralData();
	}

	/**
	 * Returns a parameter value string of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @return The parameter value string.
	 */
	public String getParameterString(XDIAddress parameterXDIAddress) {

		LiteralNode parameterLiteral = this.getParameterLiteralNode(parameterXDIAddress);
		if (parameterLiteral == null) return null;

		return parameterLiteral.getLiteralDataString();
	}

	/**
	 * Returns a parameter value number of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @return The parameter value number.
	 */
	public Number getParameterNumber(XDIAddress parameterXDIAddress) {

		LiteralNode parameterLiteral = this.getParameterLiteralNode(parameterXDIAddress);
		if (parameterLiteral == null) return null;

		return parameterLiteral.getLiteralDataNumber();
	}

	/**
	 * Returns a parameter value boolean of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @return The parameter value boolean.
	 */
	public Boolean getParameterBoolean(XDIAddress parameterXDIAddress) {

		LiteralNode parameterLiteral = this.getParameterLiteralNode(parameterXDIAddress);
		if (parameterLiteral == null) return null;

		return parameterLiteral.getLiteralDataBoolean();
	}

	private LiteralNode getParameterLiteralNode(XDIAddress parameterXDIAddress) {

		XdiEntitySingleton parameterXdiEntity = this.getMessage().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), false);
		if (parameterXdiEntity == null) return null;

		XdiAttributeSingleton parameterXdiAttribute = parameterXdiEntity.getXdiAttributeSingleton(parameterXDIAddress, false);
		if (parameterXdiAttribute == null) return null;

		LiteralNode parameterLiteral = parameterXdiAttribute.getLiteralNode();
		if (parameterLiteral == null) return null;

		return parameterLiteral;
	}

	public Map<XDIArc, XDIAddress> getVariableValues() {

		XdiEntitySingleton variableValuesXdiEntity = this.getMessage().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), false);
		if (variableValuesXdiEntity == null) return Collections.emptyMap();

		Map<XDIArc, XDIAddress> variableValues = new HashMap<XDIArc, XDIAddress> ();
		MappingContextNodeXdiVariableIterator xdiVariablesIterator = new MappingContextNodeXdiVariableIterator(variableValuesXdiEntity.getContextNode().getContextNodes());

		for (XdiVariable<?> xdiVariable : xdiVariablesIterator) {

			XDIArc variableValueXDIArc = xdiVariable.getXDIArc();
			ContextNode variableValueContextNode = Equivalence.getIdentityContextNode(xdiVariable.getContextNode());
			XDIAddress variableValueXDIAddress = variableValueContextNode == null ? null : variableValueContextNode.getXDIAddress();

			if (log.isDebugEnabled()) log.debug("Variable value for " + this.getOperationXDIAddress() + " operation: " + variableValueXDIArc + " --> " + variableValueXDIAddress);

			if (variableValueXDIArc == null || variableValueXDIAddress == null) continue;

			variableValues.put(variableValueXDIArc, variableValueXDIAddress);
		}

		return variableValues;
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
