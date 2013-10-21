package xdi2.core.features.nodetypes;

import xdi2.core.xri3.XDI3Segment;

public interface XdiEntity extends XdiSubGraph<XdiEntity> {

	public XdiInnerRoot getXdiInnerRoot(XDI3Segment innerRootPredicateXri, boolean create);
}
