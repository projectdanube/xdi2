package xdi2.core.features.nodetypes;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.ReadOnlyIterator;

public interface XdiCollection<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiSubGraph<EQC> {

	public I setXdiInstance(XDIArc XDIarc);

	public U setXdiInstanceUnordered();
	public U setXdiInstanceUnordered(String literal);
	public U setXdiInstanceUnordered(boolean immutable, boolean relative);
	public U setXdiInstanceUnordered(boolean immutable, boolean relative, String literal);
	public U getXdiInstanceUnordered(boolean immutable, boolean relative, String literal);
	public ReadOnlyIterator<U> getXdiInstancesUnordered();
	public long getXdiInstancesUnorderedCount();

	public O setXdiInstanceOrdered();
	public O setXdiInstanceOrdered(long index);
	public O setXdiInstanceOrdered(boolean immutable, boolean relative);
	public O setXdiInstanceOrdered(boolean immutable, boolean relative, long index);
	public O getXdiInstanceOrdered(boolean immutable, boolean relative, long index);
	public ReadOnlyIterator<O> getXdiInstancesOrdered();
	public long getXdiInstancesOrderedCount();

	public ReadOnlyIterator<I> getXdiInstances();
	public ReadOnlyIterator<EQI> getXdiInstancesDeref();
}
