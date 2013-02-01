package xdi2.core.xri3.parser;

import xdi2.core.xri3.parser.apg.XDI3ParserAPG;

public class XDI3ParserRegistry {

	private static XDI3Parser instance = new XDI3ParserAPG();

	private XDI3ParserRegistry() { }
	
	public static void setInstance(XDI3Parser instance) {

		XDI3ParserRegistry.instance = instance;
	}

	public static XDI3Parser getInstance() {

		return XDI3ParserRegistry.instance;
	}
}
