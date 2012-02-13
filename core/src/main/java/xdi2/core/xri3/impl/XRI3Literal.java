package xdi2.core.xri3.impl;

import xdi2.core.xri3.XRILiteral;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Parser.literal;
import xdi2.core.xri3.impl.parser.Parser.literal_nc;

public class XRI3Literal extends XRI3SyntaxComponent implements XRILiteral {

	private static final long serialVersionUID = -2070825542439606624L;

	private Rule rule;
	
	private String value;

	public XRI3Literal(String string) throws ParserException {

		this.rule = XRI3Util.getParser().parse("literal", string);
		this.read();
	}

	XRI3Literal(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {
		
		this.value = null;
	}

	private void read() {

		this.reset();
		
		Object object = this.rule;	// literal or literal_nc

		// literal of literal_nc
		
		if (object instanceof literal) {
			
			this.value = ((literal) object).spelling;
		} else if (object instanceof literal_nc) {
			
			this.value = ((literal_nc) object).spelling;
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public String getValue() {
		
		return(this.value);
	}
}
