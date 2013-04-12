package xdi2.core.features.nodetypes;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiSubGraph extends Serializable, Comparable<XdiSubGraph> {

	public ContextNode getContextNode();
	public XDI3SubSegment getBaseArcXri();

	public XdiEntityClass getXdiEntityClass(XDI3SubSegment arcXri, boolean create);
	public XdiAttributeClass getXdiAttributeClass(XDI3SubSegment arcXri, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDI3SubSegment arcXri, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3SubSegment arcXri, boolean create);
}
