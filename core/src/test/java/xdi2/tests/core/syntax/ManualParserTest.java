package xdi2.tests.core.syntax;

import xdi2.core.syntax.Parser;
import xdi2.core.syntax.manual.ManualParser;

public class ManualParserTest extends AbstractParserTest {

	private Parser parser = new ManualParser();

	@Override
	public Parser getParser() {

		return this.parser;
	}
}
