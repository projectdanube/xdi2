package xdi2.core.xri3.impl;

import java.util.List;

import xdi2.core.xri3.XRIReference;
import xdi2.core.xri3.XRIXRef;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$IRI;
import xdi2.core.xri3.impl.parser.Rule$xdi_address;
import xdi2.core.xri3.impl.parser.Rule$xdi_context;
import xdi2.core.xri3.impl.parser.Rule$xdi_segment;
import xdi2.core.xri3.impl.parser.Rule$xdi_statement;
import xdi2.core.xri3.impl.parser.Rule$xdi_xref;
import xdi2.core.xri3.impl.parser.Rule$xdi_xref_IRI;
import xdi2.core.xri3.impl.parser.Rule$xdi_xref_address;
import xdi2.core.xri3.impl.parser.Rule$xdi_xref_empty;
import xdi2.core.xri3.impl.parser.Rule$xref_IRI;
import xdi2.core.xri3.impl.parser.Rule$xref_empty;
import xdi2.core.xri3.impl.parser.Rule$xref_xri_reference;

public class XDI3XRef extends XRI3SyntaxComponent implements XRIXRef {

	private static final long serialVersionUID = 4875921569202236777L;

	private Rule rule;

	private String value;
	private XDI3Segment node;
	private XDI3Statement statement;
	private String iri;

	public XDI3XRef(String string) throws ParserException {

		this.rule = Parser.parse("xdi-xref", string);
		this.read();
	}

	XDI3XRef(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.value = null;
		this.node = null;
		this.statement = null;
		this.iri = null;
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xdi_xref or xdi_xref_empty or xdi_xref_address or xdi_xref_IRI

		// xdi_xref or xdi_xref_empty or xdi_xref_address or xdi_xref_IRI ?

		if (object instanceof Rule$xdi_xref) {

			List list_xdi_xref = ((Rule$xdi_xref) object).rules;
			if (list_xdi_xref.size() < 1) return;
			object = list_xdi_xref.get(0);	// xdi_xref_empty or xdi_xref_address or xdi_xref_IRI
		} else if (object instanceof Rule$xref_empty) {

		} else if (object instanceof Rule$xref_xri_reference) {

		} else if (object instanceof Rule$xref_IRI) {

		} else {

			throw new ClassCastException(object.getClass().getName());
		}

		// xdi_xref_empty or xdi_xref_address or xdi_xref_IRI ?

		if (object instanceof Rule$xdi_xref_empty) {

		} else if (object instanceof Rule$xdi_xref_address) {

			// read xdi_address from xref_xdi_address

			List list_xdi_xref_address = ((Rule$xdi_xref_address) object).rules;
			if (list_xdi_xref_address.size() < 2) return;
			object = list_xdi_xref_address.get(1);	// xdi_address
			this.value = ((Rule$xdi_address) object).spelling;

			// read xdi_context or xdi_statement from xdi_address

			List list_xdi_address = ((Rule$xdi_address) object).rules;
			if (list_xdi_address.size() < 1) return;
			object = list_xdi_address.get(0);	// xdi_context or xdi_statement

			// xdi_context or xdi_statement ?

			if (object instanceof Rule$xdi_context) {

				// read xdi_segment from xdi_context

				List list_xdi_context = ((Rule$xdi_context) object).rules;
				if (list_xdi_context.size() < 1) return;
				object = list_xdi_context.get(0);	// xdi_segment
				this.node = new XDI3Segment((Rule$xdi_segment) object);
			} else if (object instanceof Rule$xdi_statement) {

				this.statement = new XDI3Statement((Rule$xdi_statement) object);
			}
		} else if (object instanceof Rule$xdi_xref_IRI) {

			// read IRI from xdi_xref_IRI

			List list_xdi_xref_IRI = ((Rule$xdi_xref_IRI) object).rules;
			if (list_xdi_xref_IRI.size() < 2) return;
			object = list_xdi_xref_IRI.get(1);	// IRI
			this.value = ((Rule$IRI) object).spelling;
			this.iri = ((Rule$IRI) object).spelling;
		} else {

			throw new ClassCastException(object.getClass().getName());
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public boolean hasXRIReference() {

		return(false);
	}

	public boolean hasNode() {

		return(this.node != null);
	}

	public boolean hasStatement() {

		return(this.statement != null);
	}

	public boolean hasIRI() {

		return(this.iri != null);
	}

	public XRIReference getXRIReference() {

		return(null);
	}

	public String getValue() {

		return(this.value);
	}

	public XDI3Segment getNode() {

		return(this.node);
	}

	public XDI3Statement getStatement() {

		return(this.statement);
	}

	public String getIRI() {

		return(this.iri);
	}
}
