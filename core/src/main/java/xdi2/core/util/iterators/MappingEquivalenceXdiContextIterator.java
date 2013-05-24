package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;

/**
 * A MappingIterator that maps XDI contexts to the target contexts of equivalence links. 
 * 
 * @author markus
 */
public class MappingEquivalenceXdiContextIterator extends MappingIterator<XdiContext, XdiContext> {

	public MappingEquivalenceXdiContextIterator(Iterator<? extends XdiContext> xdiContexts) {

		super(xdiContexts);
	}

	@Override
	public XdiContext map(XdiContext xdiContext) {

		ContextNode referenceContextNode = Equivalence.getReferenceContextNode(xdiContext.getContextNode());
		if (referenceContextNode != null) return XdiAbstractContext.fromContextNode(referenceContextNode);

		ContextNode replacementContextNode = Equivalence.getReplacementContextNode(xdiContext.getContextNode());
		if (replacementContextNode != null) return XdiAbstractContext.fromContextNode(replacementContextNode);

		return xdiContext;
	}
}
