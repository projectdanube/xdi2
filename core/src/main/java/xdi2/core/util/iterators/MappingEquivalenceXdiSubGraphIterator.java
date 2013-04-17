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
public class MappingEquivalenceXdiSubGraphIterator<I extends XdiSubGraph> extends MappingIterator<I, I> {

	private Class<? extends I> i;

	public MappingEquivalenceXdiSubGraphIterator(Iterator<? extends I> xdiSubGraphs, Class<? extends I> i) {

		super(xdiSubGraphs);

		this.i = i;
	}

	@Override
	public I map(I xdiSubGraph) {

		ContextNode referenceContextNode = Equivalence.getReferenceContextNode(xdiSubGraph.getContextNode());
		if (referenceContextNode != null) return XdiAbstractSubGraph.fromContextNode(referenceContextNode, this.getI());

		ContextNode replacementContextNode = Equivalence.getReplacementContextNode(xdiSubGraph.getContextNode());
		if (replacementContextNode != null) return XdiAbstractSubGraph.fromContextNode(replacementContextNode, this.getI());

		return xdiSubGraph;
	}

	public Class<? extends I> getI() {

		return this.i;
	}
}
