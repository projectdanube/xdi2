package xdi2.tests.core.syntax;

import xdi2.core.syntax.Parser;
import xdi2.core.syntax.apg.APGParser;

public class APGParserTest extends AbstractParserTest {

	private Parser parser = new APGParser();

	@Override
	public Parser getParser() {

		return this.parser;
	}
}
