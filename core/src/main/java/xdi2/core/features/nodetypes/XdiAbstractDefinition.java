package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractDefinition<EQ extends XdiContext<EQ>> extends XdiAbstractContext<EQ> implements XdiDefinition<EQ> {

	private static final long serialVersionUID = -6867120632356031373L;

	protected XdiAbstractDefinition(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI definition.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI definition.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiCommonVariable.isValid(contextNode)) return true; 

		if (XdiPeerRoot.Definition.isValid(contextNode)) return true; 
		if (XdiInnerRoot.Definition.isValid(contextNode)) return true; 
		if (XdiEntityCollection.Definition.isValid(contextNode)) return true; 
		if (XdiAttributeCollection.Definition.isValid(contextNode)) return true; 
		if (XdiEntityInstanceOrdered.Definition.isValid(contextNode)) return true; 
		if (XdiEntityInstanceUnordered.Definition.isValid(contextNode)) return true; 
		if (XdiEntitySingleton.Definition.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceOrdered.Definition.isValid(contextNode)) return true; 
		if (XdiAttributeInstanceUnordered.Definition.isValid(contextNode)) return true; 
		if (XdiAttributeSingleton.Definition.isValid(contextNode)) return true; 

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
	 * Factory method that creates an XDI definition bound to a given context node.
	 * @param contextNode The context node that is an XDI definition.
	 * @return The XDI definition.
	 */
	public static XdiDefinition<? extends XdiContext<?>> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiDefinition<? extends XdiContext<?>> xdiDefinition = null;

		if ((xdiDefinition = XdiCommonDefinition.fromContextNode(contextNode)) != null) return xdiDefinition;

		if ((xdiDefinition = XdiPeerRoot.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiInnerRoot.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityCollection.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeCollection.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityInstanceOrdered.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityInstanceUnordered.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntitySingleton.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeInstanceOrdered.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeInstanceUnordered.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeSingleton.Definition.fromContextNode(contextNode)) != null) return xdiDefinition;

		if ((xdiDefinition = XdiPeerRoot.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiInnerRoot.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityCollection.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeCollection.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityInstanceOrdered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntityInstanceUnordered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiEntitySingleton.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeInstanceOrdered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeInstanceUnordered.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;
		if ((xdiDefinition = XdiAttributeSingleton.Definition.Variable.fromContextNode(contextNode)) != null) return xdiDefinition;

		return null;
	}

	public static XdiDefinition<?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableIterator extends NotNullIterator<XdiDefinition<? extends XdiContext<?>>> {

		public MappingContextNodeXdiVariableIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiDefinition<? extends XdiContext<?>>> (contextNodes) {

				@Override
				public XdiDefinition<? extends XdiContext<?>> map(ContextNode contextNode) {

					return XdiAbstractDefinition.fromContextNode(contextNode);
				}
			});
		}
	}
}
