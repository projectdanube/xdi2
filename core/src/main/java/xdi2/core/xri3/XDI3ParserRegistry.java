package xdi2.core.xri3;

import xdi2.core.xri3.parser.manual.XDI3ParserManual;

public class XDI3ParserRegistry {

	private static XDI3ParserRegistry instance = new XDI3ParserRegistry(new XDI3ParserManual());

	private XDI3Parser parser;

	private XDI3ParserRegistry(XDI3Parser parser) { 
		
		this.parser = parser;
	}

	public static void setInstance(XDI3ParserRegistry instance) {

		XDI3ParserRegistry.instance = instance;
	}

	public static XDI3ParserRegistry getInstance() {

		return XDI3ParserRegistry.instance;
	}

	public XDI3Parser getParser() {

		return this.parser;
	}

	public void setParser(XDI3Parser parser) {

		this.parser = parser;
	}
}
