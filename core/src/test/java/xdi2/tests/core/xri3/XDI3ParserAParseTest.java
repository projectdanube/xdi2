package xdi2.tests.core.xri3;

import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.parser.aparse.XDI3ParserAParse;

public class XDI3ParserAParseTest extends XDI3ParserTest {

	private XDI3Parser parser = new XDI3ParserAParse();
	
	@Override
	public XDI3Parser getParser() {
		
		return this.parser;
	}

	@Override
	public void testBasic() throws Exception {

	}

	@Override
	public void testBasicXRef() throws Exception {
		
	}

	@Override
	public void testXDI3Statement() throws Exception {

	}

	@Override
	public void testXDI3XRef() throws Exception {

	}

	@Override
	public void testLiteralXRef() {

	}
}
