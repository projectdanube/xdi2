package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;

/**
 * A MappingIterator that maps XDI context nodes to the target context nodes of equivalence links. 
 * 
 * @author markus
 */
public class MappingEquivalenceContextNodeIterator extends MappingIterator<ContextNode, ContextNode> {

	public MappingEquivalenceContextNodeIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public ContextNode map(ContextNode contextNode) {

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);
		xdiContext = xdiContext.dereference();

		return xdiContext == null ? null : xdiContext.getContextNode();
	}
}
