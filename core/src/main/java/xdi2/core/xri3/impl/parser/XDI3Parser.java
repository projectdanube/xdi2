package xdi2.core.xri3.impl.parser;

import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3Statement;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XDI3XRef;

public abstract class XDI3Parser {

	private static XDI3Parser instance = new XDI3ParserAParse();

	public static void setInstance(XDI3Parser instance) {

		XDI3Parser.instance = instance;
	}

	public static XDI3Parser getInstance() {

		return XDI3Parser.instance;
	}

	public abstract XDI3Statement parseXDI3Statement(String string);
	public abstract XDI3Segment parseXDI3Segment(String string);
	public abstract XDI3SubSegment parseXDI3SubSegment(String string);
	public abstract XDI3XRef parseXDI3XRef(String string);
}
