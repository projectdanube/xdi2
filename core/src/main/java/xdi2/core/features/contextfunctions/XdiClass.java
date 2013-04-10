package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.xri3.XDI3SubSegment;

public interface XdiClass extends XdiSubGraph {

	public XdiAbstractInstance getXdiInstance(XDI3SubSegment arcXri, boolean create);
	public XdiAbstractInstance getXdiInstance();
	public Iterator<? extends XdiAbstractInstance> instances();
	public int instancesSize();
	
	public XdiAbstractElement getXdiElement(int index, boolean create);
	public Iterator<? extends XdiAbstractElement> elements();
	public int elementsSize();

	public Iterator<? extends XdiSubGraph> instancesAndElements();
}
