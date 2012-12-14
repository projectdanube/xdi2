package xdi2.core.xri3.impl;

import java.util.List;

import xdi2.core.xri3.XRILiteral;
import xdi2.core.xri3.XRISubSegment;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$gcs_char;
import xdi2.core.xri3.impl.parser.Rule$global_subseg;
import xdi2.core.xri3.impl.parser.Rule$lcs_char;
import xdi2.core.xri3.impl.parser.Rule$literal;
import xdi2.core.xri3.impl.parser.Rule$local_subseg;
import xdi2.core.xri3.impl.parser.Rule$subseg;
import xdi2.core.xri3.impl.parser.Rule$xdi_global_subseg;
import xdi2.core.xri3.impl.parser.Rule$xdi_local_subseg;
import xdi2.core.xri3.impl.parser.Rule$xdi_subseg;
import xdi2.core.xri3.impl.parser.Rule$xdi_xref;
import xdi2.core.xri3.impl.parser.Rule$xref;

public class XDI3SubSegment extends XRI3SyntaxComponent implements XRISubSegment {

	private static final long serialVersionUID = -645927779266394209L;

	private Rule rule;

	private Character gcs;
	private Character lcs;
	private XRI3Literal literal;
	private XDI3XRef xref;

	public XDI3SubSegment(String string) throws ParserException {

		this.rule = Parser.parse("xdi-subseg", string);
		this.read();
	}

	XDI3SubSegment(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.gcs = null;
		this.lcs = null;
		this.literal = null;
		this.xref = null;
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// subseg or xdi_subseg or global_subseg or xdi_global_subseg or local_subseg or xdi_local_subseg or xref or xdi_xref or literal

		// subseg?

		if (object instanceof Rule$subseg) {

			// read global_subseg or local_subseg or xref from subseg

			List list_subseg = ((Rule$subseg) object).rules;
			if (list_subseg.size() < 1) return;
			object = list_subseg.get(0);	// global_subseg or local_subseg or xref
		}

		// xdi_subseg?

		if (object instanceof Rule$xdi_subseg) {

			// read xdi_global_subseg or xdi_local_subseg or xdi_xref from xdi_subseg

			List list_xdi_subseg = ((Rule$xdi_subseg) object).rules;
			if (list_xdi_subseg.size() < 1) return;
			object = list_xdi_subseg.get(0);	// global_subseg or local_subseg or xdi_xref
		}

		// global_subseg?

		if (object instanceof Rule$global_subseg) {

			// read gcs_char from global_subseg;

			List list_global_subseg = ((Rule$global_subseg) object).rules;
			if (list_global_subseg.size() < 1) return;
			object = list_global_subseg.get(0);	// gcs_char
			this.gcs = new Character(((Rule$gcs_char) object).spelling.charAt(0));

			// read local_subseg or xref or literal from global_subseg

			if (list_global_subseg.size() < 2) return;
			object = list_global_subseg.get(1);	// local_subseg or xref or literal
		}

		// xdi_global_subseg?

		if (object instanceof Rule$xdi_global_subseg) {

			// read gcs_char from xdi_global_subseg;

			List list_xdi_global_subseg = ((Rule$xdi_global_subseg) object).rules;
			if (list_xdi_global_subseg.size() < 1) return;
			object = list_xdi_global_subseg.get(0);	// gcs_char
			this.gcs = new Character(((Rule$gcs_char) object).spelling.charAt(0));

			// read xdi_local_subseg or xdi_xref or literal from xdi_global_subseg

			if (list_xdi_global_subseg.size() < 2) return;
			object = list_xdi_global_subseg.get(1);	// xdi_local_subseg or xdi_xref or literal
		}

		// local_subseg?

		if (object instanceof Rule$local_subseg) {

			// read lcs_char from local_subseg;

			List list_local_subseg = ((Rule$local_subseg) object).rules;
			if (list_local_subseg.size() < 1) return;
			object = list_local_subseg.get(0);	// lcs_char
			this.lcs = new Character(((Rule$lcs_char) object).spelling.charAt(0));

			// read xref or literal from local_subseg

			if (list_local_subseg.size() < 2) return;
			object = list_local_subseg.get(1);	// xref or literal
		}

		// xdi_local_subseg?

		if (object instanceof Rule$xdi_local_subseg) {

			// read lcs_char from xdi_local_subseg;

			List list_xdi_local_subseg = ((Rule$xdi_local_subseg) object).rules;
			if (list_xdi_local_subseg.size() < 1) return;
			object = list_xdi_local_subseg.get(0);	// lcs_char
			this.lcs = new Character(((Rule$lcs_char) object).spelling.charAt(0));

			// read xdi_xref or literal from local_subseg

			if (list_xdi_local_subseg.size() < 2) return;
			object = list_xdi_local_subseg.get(1);	// xdi_xref or literal
		}

		// literal or literal_nc or xref or xdi_xref?

		if (object instanceof Rule$literal) {

			this.literal = new XRI3Literal((Rule$literal) object);
		} else if (object instanceof Rule$xref) {

			this.xref = new XDI3XRef((Rule$xref) object);
		} else if (object instanceof Rule$xdi_xref) {

			this.xref = new XDI3XRef((Rule$xdi_xref) object);
		} else {

			return;
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public boolean hasGCS() {

		return(this.gcs != null);
	}

	public boolean hasLCS() {

		return(this.lcs != null);
	}

	public boolean hasLiteral() {

		return(this.literal != null);
	}

	public boolean hasXRef() {

		return(this.xref != null);
	}

	public Character getGCS() {

		return(this.gcs);
	}

	public Character getLCS() {

		return(this.lcs);
	}

	public XRILiteral getLiteral() {

		return(this.literal);
	}

	public XDI3XRef getXRef() {

		return(this.xref);
	}

	public boolean isGlobal() {

		return(this.hasGCS());
	}

	public boolean isLocal() {

		return(this.hasLCS() && ! this.hasGCS());
	}

	public boolean isPersistent() {

		return(this.hasLCS() && this.getLCS().equals(XRI3Constants.LCS_BANG));
	}

	public boolean isReassignable() {

		return((this.hasGCS() && ! this.hasLCS()) || (this.hasLCS() && this.getLCS().equals(XRI3Constants.LCS_STAR)));
	}
}
