package xdi2.core.features.contextfunctions;

import xdi2.core.xri3.XDI3SubSegment;

public interface XdiAttribute extends XdiSubGraph {

	public XdiValue getXdiValue(XDI3SubSegment arcXri, boolean create);
}
