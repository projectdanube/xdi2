package xdi2.core.xri3.impl;

import xdi2.core.xri3.XRIQuery;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$iquery;

public class XRI3Query extends XRI3SyntaxComponent implements XRIQuery {

	private static final long serialVersionUID = 8838957773108506171L;

	private Rule rule;
	
	private String value;

	public XRI3Query(String string) throws ParserException {

		this.rule = Parser.parse("iquery", string);
		this.read();
	}

	XRI3Query(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {
		
		this.value = null;
	}

	private void read() {

		this.reset();
		
		Object object = this.rule;	// iquery

		this.value = ((Rule$iquery) object).spelling;
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public String getValue() {
		
		return(this.value);
	}
}
