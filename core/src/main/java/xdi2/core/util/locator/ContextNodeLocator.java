package xdi2.core.util.locator;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XDI3Segment;

public interface ContextNodeLocator {

	public XDI3Segment getContextNodeXri(XDI3Segment xri);
	public ContextNode locateContextNode(XDI3Segment xri);
}
