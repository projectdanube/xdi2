package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;

/**
 * A MappingIterator that maps XDI context nodes to their arcs.
 * 
 * @author markus
 */
public class MappingContextNodeXDIArcIterator extends MappingIterator<ContextNode, XDIArc> {

	public MappingContextNodeXDIArcIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public XDIArc map(ContextNode contextNode) {

		return contextNode.getXDIArc();
	}
}
