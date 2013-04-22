package xdi2.tests.core.xri3;

import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.parser.manual.XDI3ParserManual;

public class XDI3ParserManualTest extends XDI3ParserTest {

	private XDI3Parser parser = new XDI3ParserManual();

	@Override
	public XDI3Parser getParser() {

		return this.parser;
	}
}
