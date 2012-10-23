package xdi2.core.xri3.impl;

import xdi2.core.xri3.XRIFragment;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$ifragment;

public class XRI3Fragment extends XRI3SyntaxComponent implements XRIFragment {

	private static final long serialVersionUID = 5034503145058610708L;

	private Rule rule;
	
	private String value;

	public XRI3Fragment(String string) throws ParserException {

		this.rule = Parser.parse("ifragment", string);
		this.read();
	}

	XRI3Fragment(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {
		
		this.value = null;
	}
	
	private void read() {

		this.reset();
		
		Object object = this.rule;	// ifragment

		this.value = ((Rule$ifragment) object).spelling;
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public String getValue() {
		
		return(this.value);
	}
}
