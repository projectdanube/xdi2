package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.features.nodetypes.XdiContext;

/**
 * A MappingIterator that maps XDI contexts to the target contexts of equivalence links. 
 * 
 * @author markus
 */
public class MappingEquivalenceXdiContextIterator<EQ extends XdiContext<EQ>> extends MappingIterator<EQ, EQ> {

	public MappingEquivalenceXdiContextIterator(Iterator<EQ> xdiContexts) {

		super(xdiContexts);
	}

	@Override
	public EQ map(EQ xdiContext) {

		return xdiContext.dereference();
	}
}
