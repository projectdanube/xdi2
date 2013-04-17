package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiClass<U extends XdiInstanceUnordered, O extends XdiInstanceOrdered, I extends XdiInstance> extends XdiSubGraph {

	public U setXdiInstanceUnordered(XDI3SubSegment arcXri);
	public U getXdiInstanceUnordered(XDI3SubSegment arcXri);
	public ReadOnlyIterator<U> getXdiInstancesUnordered();
	public int getXdiInstancesUnorderedCount();

	public O setXdiInstanceOrdered(int index);
	public O getXdiInstanceOrdered(int index);
	public ReadOnlyIterator<O> getXdiInstancesOrdered();
	public int getXdiInstancesOrderedCount();

	public ReadOnlyIterator<I> getXdiInstances(boolean deref);
}
