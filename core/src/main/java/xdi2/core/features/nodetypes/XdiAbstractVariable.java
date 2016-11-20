package xdi2.core.features.nodetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public abstract class XdiAbstractVariable<EQ extends XdiContext<EQ>> extends XdiAbstractContext<EQ> implements XdiVariable<EQ> {

	private static final long serialVersionUID = 3293452777739432663L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractVariable.class);

	protected XdiAbstractVariable(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI variable.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI variable.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiCommonVariable.isValid(contextNode)) return true; 

		if (XdiPeerRoot.Variable.isValid(contextNode)) return true; 
		if (XdiInnerRoot.Variable.isValid(contextNode)) return true; 
		if (XdiEntityCollection.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeCollection.Variable.isValid(contextNode)) return true; 
		if (XdiEntityInstanceOrdered.Variable.isValid(contextNode)) return true; 
		if (XdiEntityInstanceUnordered.Variable.isValid(contextNode)) return true; 
		if (XdiEntitySingleton.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceOrdered.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceUnordered.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeSingleton.Variable.isValid(contextNode)) return true; 

		if (XdiPeerRoot.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiInnerRoot.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiEntityCollection.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeCollection.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiEntityInstanceOrdered.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiEntityInstanceUnordered.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiEntitySingleton.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceOrdered.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceUnordered.Definition.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeSingleton.Definition.Variable.isValid(contextNode)) return true; 

		return false;
	}

	/**
	 * Factory method that creates an XDI variable bound to a given context node.
	 * @param contextNode The context node that is an XDI variable.
	 * @return The XDI variable.
	 */
	public static XdiVariable<? extends XdiContext<?>> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiVariable<? extends XdiContext<?>> xdiVariable = null;

		if ((xdiVariable = XdiCommonVariable.fromContextNode(contextNode)) != null) return xdiVariable;

		if ((xdiVariable = XdiPeerRoot.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiInnerRoot.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityCollection.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeCollection.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityInstanceOrdered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityInstanceUnordered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntitySingleton.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeInstanceOrdered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeInstanceUnordered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeSingleton.Variable.fromContextNode(contextNode)) != null) return xdiVariable;

		if ((xdiVariable = XdiPeerRoot.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiInnerRoot.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityCollection.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeCollection.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityInstanceOrdered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityInstanceUnordered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntitySingleton.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeInstanceOrdered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeInstanceUnordered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeSingleton.Definition.Variable.fromContextNode(contextNode)) != null) return xdiVariable;

		return null;
	}

	public static XdiVariable<?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Variable values
	 */

	public static void setVariableValue(XdiEntity variableValuesXdiEntity, XDIArc variableValueXDIArc, Object variableValue) {

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

	public static Map<XDIArc, Object> getVariableValues(XdiEntity variableValuesXdiEntity) {

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

					if (log.isDebugEnabled()) log.debug("Variable XDI address value: " + variableValueXDIArc + " --> " + variableXDIAddressValue);
					variableXDIAddressValues.add(variableXDIAddressValue);
				}
			}

			if (variableLiteralDataValue != null) {

				if (log.isDebugEnabled()) log.debug("Variable literal data value: " + variableValueXDIArc + " --> " + variableLiteralDataValue);
				variableValues.put(variableValueXDIArc, variableLiteralDataValue);
			}
		}

		return variableValues;
	}

	@SuppressWarnings("unchecked")
	public static List<XDIAddress> getVariableXDIAddressValues(Map<XDIArc, Object> variableValues, XDIArc variableValueXDIArc) {

		Object variableValue = variableValues.get(variableValueXDIArc);
		if (variableValue instanceof List<?>) return (List<XDIAddress>) variableValue;
		if (variableValue instanceof XDIAddress) return Collections.singletonList((XDIAddress) variableValue);
		if (variableValue instanceof XDIArc) return Collections.singletonList(XDIAddress.fromComponent((XDIArc) variableValue));

		return Collections.emptyList();
	}

	public static XDIAddress getVariableXDIAddressValue(Map<XDIArc, Object> variableValues, XDIArc variableValueXDIArc) {

		Object variableValue = variableValues.get(variableValueXDIArc);
		if (variableValue instanceof List<?> && ((List<?>) variableValue).size() == 1) variableValue = ((List<?>) variableValue).get(0);
		if (variableValue instanceof XDIAddress) return (XDIAddress) variableValue;
		if (variableValue instanceof XDIArc) return XDIAddress.fromComponent((XDIArc) variableValue);

		return null;
	}

	public static XDIArc getVariableXDIArcValue(Map<XDIArc, Object> variableValues, XDIArc variableValueXDIArc) {

		Object variableValue = variableValues.get(variableValueXDIArc);
		if (variableValue instanceof List<?> && ((List<?>) variableValue).size() == 1) variableValue = ((List<?>) variableValue).get(0);
		if (variableValue instanceof XDIAddress && ((XDIAddress) variableValue).getNumXDIArcs() == 1) return ((XDIAddress) variableValue).getXDIArc(0);
		if (variableValue instanceof XDIArc) return (XDIArc) variableValue;

		return null;
	}

	public static Object getVariableLiteralDataValue(Map<XDIArc, Object> variableValues, XDIArc variableValueXDIArc) {

		Object variableLiteralDataValue = variableValues.get(variableValueXDIArc);
		if (variableLiteralDataValue instanceof List<?>) return null;

		return variableLiteralDataValue;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableIterator extends NotNullIterator<XdiVariable<? extends XdiContext<?>>> {

		public MappingContextNodeXdiVariableIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariable<? extends XdiContext<?>>> (contextNodes) {

				@Override
				public XdiVariable<? extends XdiContext<?>> map(ContextNode contextNode) {

					return XdiAbstractVariable.fromContextNode(contextNode);
				}
			});
		}
	}
}
