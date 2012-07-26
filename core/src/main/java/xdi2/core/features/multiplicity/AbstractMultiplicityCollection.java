package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

public abstract class AbstractMultiplicityCollection<T> extends AbstractMultiplicityContextNode implements Iterable<T> {

	private static final long serialVersionUID = 284615577959792946L;

	protected AbstractMultiplicityCollection(ContextNode contextNode) {

		super(contextNode);
	}
}
