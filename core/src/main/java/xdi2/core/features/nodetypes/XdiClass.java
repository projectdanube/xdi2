package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiClass<U extends XdiInstanceUnordered, O extends XdiInstanceOrdered, I extends XdiInstance> extends XdiSubGraph {

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
