package xdi2.core.features.nodetypes;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiContext extends Serializable, Comparable<XdiContext> {

	public ContextNode getContextNode();
	public XDI3SubSegment getBaseArcXri();

	public XdiEntityCollection getXdiEntityCollection(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3SubSegment contextNodeArcXri, boolean create);
}
