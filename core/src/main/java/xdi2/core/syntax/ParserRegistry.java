package xdi2.core.syntax;

import xdi2.core.syntax.manual.ManualParser;

public class ParserRegistry {

	private static ParserRegistry instance = new ParserRegistry(new ManualParser());

	private Parser parser;

	private ParserRegistry(Parser parser) { 
		
		this.parser = parser;
	}

	public static void setInstance(ParserRegistry instance) {

		ParserRegistry.instance = instance;
	}

	public static ParserRegistry getInstance() {

		return ParserRegistry.instance;
	}

	public Parser getParser() {

		return this.parser;
	}

	public void setParser(Parser parser) {

		this.parser = parser;
	}
}
