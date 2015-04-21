package xdi2.core.syntax.parser;

import xdi2.core.syntax.Parser;

public class ParserRegistry {

	private static ParserRegistry instance = new ParserRegistry(new ParserImpl());

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
