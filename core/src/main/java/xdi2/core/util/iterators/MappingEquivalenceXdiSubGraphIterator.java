package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractSubGraph;
import xdi2.core.features.nodetypes.XdiSubGraph;

/**
 * A MappingIterator that maps XDI subgraphs to the target subgraphs of equivalence links. 
 * 
 * @author markus
 */
public class MappingEquivalenceXdiSubGraphIterator extends MappingIterator<XdiSubGraph, XdiSubGraph> {

	public MappingEquivalenceXdiSubGraphIterator(Iterator<? extends XdiSubGraph> xdiSubGraphs) {

		super(xdiSubGraphs);
	}

	@Override
	public XdiSubGraph map(XdiSubGraph xdiSubGraph) {

		ContextNode referenceContextNode = Equivalence.getReferenceContextNode(xdiSubGraph.getContextNode());
		if (referenceContextNode != null) return XdiAbstractSubGraph.fromContextNode(referenceContextNode);

		ContextNode replacementContextNode = Equivalence.getReplacementContextNode(xdiSubGraph.getContextNode());
		if (replacementContextNode != null) return XdiAbstractSubGraph.fromContextNode(replacementContextNode);

		return xdiSubGraph;
	}
}
