package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractVariable<EQ extends XdiContext<EQ>> extends XdiAbstractContext<EQ> implements XdiVariable<EQ> {

	private static final long serialVersionUID = 3293452777739432663L;

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

		if (XdiPeerRoot.Variable.isValid(contextNode)) return true; 
		if (XdiInnerRoot.Variable.isValid(contextNode)) return true; 
		if (XdiEntityCollection.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeCollection.Variable.isValid(contextNode)) return true; 
		if (XdiEntityMemberOrdered.Variable.isValid(contextNode)) return true; 
		if (XdiEntityMemberUnordered.Variable.isValid(contextNode)) return true; 
		if (XdiEntitySingleton.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeMemberOrdered.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeMemberUnordered.Variable.isValid(contextNode)) return true; 
		if (XdiAttributeSingleton.Variable.isValid(contextNode)) return true; 

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

		if ((xdiVariable = XdiPeerRoot.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiInnerRoot.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityCollection.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeCollection.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityMemberOrdered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntityMemberUnordered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiEntitySingleton.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeMemberOrdered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeMemberUnordered.Variable.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiAttributeSingleton.Variable.fromContextNode(contextNode)) != null) return xdiVariable;

		return null;
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();
		
		if (XdiPeerRoot.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiInnerRoot.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiEntityCollection.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeCollection.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiEntityMemberOrdered.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiEntityMemberUnordered.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiEntitySingleton.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeMemberOrdered.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeMemberUnordered.Variable.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeSingleton.Variable.isValidXDIArc(XDIarc)) return true; 

		return false;
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
