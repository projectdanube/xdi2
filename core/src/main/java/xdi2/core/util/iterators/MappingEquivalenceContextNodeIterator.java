package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.equivalence.Equivalence;

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

		ContextNode referenceContextNode = Equivalence.getReferenceContextNode(contextNode);
		if (referenceContextNode != null) return referenceContextNode;

		ContextNode replacementContextNode = Equivalence.getReplacementContextNode(contextNode);
		if (replacementContextNode != null) return replacementContextNode;
		
		return contextNode;
	}
}
