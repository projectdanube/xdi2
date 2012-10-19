package xdi2.core.xri3.impl;

import java.util.List;

import xdi2.core.xri3.XRIReference;
import xdi2.core.xri3.XRIXRef;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$IRI;
import xdi2.core.xri3.impl.parser.Rule$xref;
import xdi2.core.xri3.impl.parser.Rule$xref_IRI;
import xdi2.core.xri3.impl.parser.Rule$xref_empty;
import xdi2.core.xri3.impl.parser.Rule$xref_xri_reference;
import xdi2.core.xri3.impl.parser.Rule$xri_reference;

public class XRI3XRef extends XRI3SyntaxComponent implements XRIXRef {

	private static final long serialVersionUID = 5499307555025868602L;

	private Rule rule;

	private XRI3Reference xriReference;
	private String iri;

	public XRI3XRef(String string) throws ParserException {

		this.rule = Parser.parse("xref", string);
		this.read();
	}

	XRI3XRef(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.xriReference = null;
		this.iri = null;
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xref or xref_empty or xref_xri_reference or xref_IRI

		// xref or xref_empty or xref_xri_reference or xref_IRI ?

		if (object instanceof Rule$xref) {

			List list_xref = ((Rule$xref) object).rules;
			if (list_xref.size() < 1) return;
			object = list_xref.get(0);	// xref_empty or xref_xri_reference or xref_IRI
		} else if (object instanceof Rule$xref_empty) {

		} else if (object instanceof Rule$xref_xri_reference) {

		} else if (object instanceof Rule$xref_IRI) {

		} else {

			throw new ClassCastException(object.getClass().getName());
		}

		// xref_empty or xref_xri_reference or xref_IRI ?


		if (object instanceof Rule$xref_empty) {

		} else if (object instanceof Rule$xref_xri_reference) {

			// read xri_reference from xref_xri_reference
			
			List list_xref_xri_reference = ((Rule$xref_xri_reference) object).rules;
			if (list_xref_xri_reference.size() < 2) return;
			object = list_xref_xri_reference.get(1);	// xri_reference
			this.xriReference = new XRI3Reference((Rule$xri_reference) object);
		} else if (object instanceof Rule$xref_IRI) {

			// read IRI from xref_IRI
			
			List list_xref_IRI = ((Rule$xref_IRI) object).rules;
			if (list_xref_IRI.size() < 2) return;
			object = list_xref_IRI.get(1);	// IRI
			this.iri = ((Rule$IRI) object).spelling;
		} else {

			throw new ClassCastException(object.getClass().getName());
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public boolean hasXRIReference() {

		return(this.xriReference != null);
	}

	public boolean hasIRI() {

		return(this.iri != null);
	}

	public XRIReference getXRIReference() {

		return(this.xriReference);
	}

	public String getIRI() {

		return(this.iri);
	}
}
