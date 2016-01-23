package xdi2.tests.core.syntax;

import xdi2.core.syntax.Parser;
import xdi2.core.syntax.parser.ParserImpl;

public class ParserImplTest extends ParserAbstractTest {

	private Parser parser = new ParserImpl();

	@Override
	public Parser getParser() {

		return this.parser;
	}
}
