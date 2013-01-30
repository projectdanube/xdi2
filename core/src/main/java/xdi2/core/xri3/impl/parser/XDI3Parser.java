package xdi2.core.xri3.impl.parser;

import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3Statement;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XDI3XRef;

public interface XDI3Parser {

	public XDI3Statement parseXDI3Statement(String string);
	public XDI3Segment parseXDI3Segment(String string);
	public XDI3SubSegment parseXDI3SubSegment(String string);
	public XDI3XRef parseXDI3XRef(String string);
}
