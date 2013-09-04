package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiClass<C extends XdiClass<C, U, O, I>, U extends XdiInstanceUnordered<C, U, O, I>, O extends XdiInstanceOrdered<C, U, O, I>, I extends XdiInstance<C, U, O, I>> extends XdiSubGraph {

	public U setXdiInstanceUnordered(XDI3SubSegment arcXri);
	public U getXdiInstanceUnordered(XDI3SubSegment arcXri);
	public ReadOnlyIterator<U> getXdiInstancesUnordered();
	public long getXdiInstancesUnorderedCount();

	public O setXdiInstanceOrdered(long index);
	public O getXdiInstanceOrdered(long index);
	public ReadOnlyIterator<O> getXdiInstancesOrdered();
	public long getXdiInstancesOrderedCount();

	public ReadOnlyIterator<I> getXdiInstances(boolean deref);
}
