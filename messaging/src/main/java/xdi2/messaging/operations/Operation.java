package xdi2.messaging.operations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2RuntimeException;
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
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageBase;
import xdi2.messaging.MessageTemplate;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI messaging operation, represented as a relation.
 * 
 * @author markus
 */
public abstract class Operation implements Serializable, Comparable<Operation> {

	private static final long serialVersionUID = 8816045435464636862L;

	private static final Logger log = LoggerFactory.getLogger(Operation.class);

	protected MessageBase<?> messageBase;
	protected Relation relation;

	protected Operation(MessageBase<?> messageBase, Relation relation) {

		if (messageBase == null || relation == null) throw new NullPointerException();

		this.messageBase = messageBase;
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

	public static boolean isValidOperationXDIAddress(XDIAddress operationXDIAddress) {

		return
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_GET) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_SET) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_DEL) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_DO) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_CONNECT) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_SEND) != null ||
				XDIAddressUtil.startsWithXDIAddress(operationXDIAddress, XDIMessagingConstants.XDI_ADD_PUSH) != null;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param messageBase The XDI message (template) to which this XDI operation belongs.
	 * @param relation The relation that is an XDI operation.
	 * @return The XDI operation.
	 */
	public static Operation fromMessageBaseAndRelation(MessageBase<?> messageBase, Relation relation) {

		if (GetOperation.isValid(relation)) return new GetOperation(messageBase, relation);
		if (SetOperation.isValid(relation)) return new SetOperation(messageBase, relation);
		if (DelOperation.isValid(relation)) return new DelOperation(messageBase, relation);
		if (DoOperation.isValid(relation)) return new DoOperation(messageBase, relation);
		if (ConnectOperation.isValid(relation)) return new ConnectOperation(messageBase, relation);
		if (SendOperation.isValid(relation)) return new SendOperation(messageBase, relation);
		if (PushOperation.isValid(relation)) return new PushOperation(messageBase, relation);

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

		return fromMessageBaseAndRelation(message, relation);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message (template) to which this XDI operation belongs.
	 * @return An XDI message (template).
	 */
	public MessageBase<?> getMessageBase() {

		return this.messageBase;
	}

	/**
	 * Returns the XDI message (template) to which this XDI operation belongs.
	 * @return An XDI message (template).
	 */
	public Message getMessage() {

		if (! (this.getMessageBase() instanceof Message)) return null;

		return (Message) this.getMessageBase();
	}

	/**
	 * Returns the XDI message template to which this XDI operation belongs.
	 * @return An XDI message template.
	 */
	public MessageTemplate getMessageTemplate() {

		if (! (this.getMessageBase() instanceof MessageTemplate)) return null;

		return (MessageTemplate) this.getMessageBase();
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

	/*
	 * Operation parameters and variable values
	 */

	/**
	 * Sets a parameter value of this operation.
	 * @param parameterXDIAddress The parameter identifier.
	 * @param parameterValue The parameter value.
	 */
	public void setParameter(XDIAddress parameterXDIAddress, Object parameterValue) {

		XdiEntitySingleton parameterXdiEntity = this.getMessageBase().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), true);
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

		XdiEntitySingleton parameterXdiEntity = this.getMessageBase().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), false);
		if (parameterXdiEntity == null) return null;

		XdiAttributeSingleton parameterXdiAttribute = parameterXdiEntity.getXdiAttributeSingleton(parameterXDIAddress, false);
		if (parameterXdiAttribute == null) return null;

		LiteralNode parameterLiteral = parameterXdiAttribute.getLiteralNode();
		if (parameterLiteral == null) return null;

		return parameterLiteral;
	}

	public void setVariableValue(XDIArc variableValueXDIArc, Object variableValue) {

		XdiEntitySingleton variableValuesXdiEntity = this.getMessageBase().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), true);
		if (variableValuesXdiEntity == null) return;

		if (variableValue instanceof XDIArc) {

			variableValue = XDIAddress.fromComponent((XDIArc) variableValue);
		}

		if (variableValue instanceof XDIAddress) {

			Equivalence.setIdentityContextNode(variableValuesXdiEntity.getContextNode().setContextNode(variableValueXDIArc), (XDIAddress) variableValue);
		} else {

			variableValuesXdiEntity.getContextNode().setContextNode(variableValueXDIArc).setLiteralNode(variableValue);
		}
	}

	public Map<XDIArc, Object> getVariableValues() {

		XdiEntitySingleton variableValuesXdiEntity = this.getMessageBase().getOperationsXdiEntity().getXdiEntitySingleton(this.getOperationXDIAddress(), false);
		if (variableValuesXdiEntity == null) return Collections.emptyMap();

		Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> ();
		MappingContextNodeXdiVariableIterator xdiVariablesIterator = new MappingContextNodeXdiVariableIterator(variableValuesXdiEntity.getContextNode().getContextNodes());

		for (XdiVariable<?> xdiVariable : xdiVariablesIterator) {

			XDIArc variableValueXDIArc = xdiVariable.getXDIArc();

			ReadOnlyIterator<ContextNode> variableValueContextNodes = Equivalence.getIdentityContextNodes(xdiVariable.getContextNode());
			Object variableLiteralDataValue = xdiVariable.getContextNode().getLiteralData();

			if (variableValueContextNodes.hasNext() && variableLiteralDataValue != null) throw new Xdi2RuntimeException("Variable has both an XDI address and a literal data value.");
			if ((! variableValueContextNodes.hasNext()) && variableLiteralDataValue == null) throw new Xdi2RuntimeException("Variable has neither XDI address nor literal data value.");

			if (variableValueContextNodes.hasNext()) {

				List<XDIAddress> variableXDIAddressValues = new ArrayList<XDIAddress> ();
				variableValues.put(variableValueXDIArc, variableXDIAddressValues);

				for (ContextNode variableValueContextNode : variableValueContextNodes) {

					XDIAddress variableXDIAddressValue = variableValueContextNode.getXDIAddress();

					if (log.isDebugEnabled()) log.debug("Variable XDI address value for " + this.getOperationXDIAddress() + " operation: " + variableValueXDIArc + " --> " + variableXDIAddressValue);
					variableXDIAddressValues.add(variableXDIAddressValue);
				}
			}

			if (variableLiteralDataValue != null) {

				if (log.isDebugEnabled()) log.debug("Variable literal data value for " + this.getOperationXDIAddress() + " operation: " + variableValueXDIArc + " --> " + variableLiteralDataValue);
				variableValues.put(variableValueXDIArc, variableLiteralDataValue);
			}
		}

		return variableValues;
	}

	@SuppressWarnings("unchecked")
	public List<XDIAddress> getVariableXDIAddressValues(XDIArc variableValueXDIArc) {

		Map<XDIArc, Object> variableValues = this.getVariableValues();
		Object variableXDIAddressValues = variableValues.get(variableValueXDIArc);
		if (! (variableXDIAddressValues instanceof List<?>)) return Collections.emptyList();

		return (List<XDIAddress>) variableXDIAddressValues;
	}

	public XDIAddress getVariableXDIAddressValue(XDIArc variableValueXDIArc) {

		List<XDIAddress> variableXDIAddressValues = this.getVariableXDIAddressValues(variableValueXDIArc);
		if (variableXDIAddressValues == null || variableXDIAddressValues.size() < 1) return null;

		return variableXDIAddressValues.get(0);
	}

	public Object getVariableLiteralDataValue(XDIArc variableValueXDIArc) {

		Map<XDIArc, Object> variableValues = this.getVariableValues();
		Object variableLiteralDataValue = variableValues.get(variableValueXDIArc);
		if (variableLiteralDataValue instanceof List<?>) return null;

		return variableLiteralDataValue;
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
