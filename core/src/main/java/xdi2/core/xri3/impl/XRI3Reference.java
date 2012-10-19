package xdi2.core.xri3.impl;

import java.util.List;

import xdi2.core.xri3.XRI;
import xdi2.core.xri3.XRIAuthority;
import xdi2.core.xri3.XRIFragment;
import xdi2.core.xri3.XRIPath;
import xdi2.core.xri3.XRIQuery;
import xdi2.core.xri3.XRIReference;
import xdi2.core.xri3.XRISyntaxComponent;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$ifragment;
import xdi2.core.xri3.impl.parser.Rule$ipath_empty;
import xdi2.core.xri3.impl.parser.Rule$iquery;
import xdi2.core.xri3.impl.parser.Rule$relative_xri_part;
import xdi2.core.xri3.impl.parser.Rule$relative_xri_ref;
import xdi2.core.xri3.impl.parser.Rule$xri;
import xdi2.core.xri3.impl.parser.Rule$xri_path_abs;
import xdi2.core.xri3.impl.parser.Rule$xri_path_noscheme;
import xdi2.core.xri3.impl.parser.Rule$xri_reference;

public class XRI3Reference extends XRI3SyntaxComponent implements XRIReference {

	private static final long serialVersionUID = 4191016969141944835L;

	private Rule rule;

	private XRI3 xri;
	private XRI3Path path;
	private XRI3Query query;
	private XRI3Fragment fragment;

	public XRI3Reference(String string) throws ParserException {

		this.rule = Parser.parse("xri-reference", string);
		this.read();
	}

	public XRI3Reference(XRIReference xriReference, XRISyntaxComponent xriPart) throws ParserException {

		StringBuffer buffer = new StringBuffer();

		buffer.append(xriReference.toString());
		buffer.append(xriPart.toString());

		this.rule = Parser.parse("xri-reference", buffer.toString());
		this.read();
	}

	public XRI3Reference(XRIReference xriReference, String xriPart) throws ParserException {

		StringBuffer buffer = new StringBuffer();

		buffer.append(xriReference.toString());
		buffer.append(xriPart);

		this.rule = Parser.parse("xri-reference", buffer.toString());
		this.read();
	}

	XRI3Reference(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.xri = null;
		this.path = null;
		this.query = null;
		this.fragment = null;
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xri_reference

		// read xri or relative_xri_ref from xri_reference

		List list_xri_reference = ((Rule$xri_reference) object).rules;
		if (list_xri_reference.size() < 1) return;
		object = list_xri_reference.get(0);	// xri or relative_xri_ref

		// xri or relative_xri_ref ?

		if (object instanceof Rule$xri) {

			this.xri = new XRI3((Rule$xri) object);
		} else if (object instanceof Rule$relative_xri_ref) {

			// read relative_xri_part from relative_xri_ref

			List list_relative_xri_ref = ((Rule$relative_xri_ref) object).rules;
			if (list_relative_xri_ref.size() < 1) return;
			object = list_relative_xri_ref.get(0);	// relative_xri_part

			// read xri_path_abs or xri_path_noscheme or ipath_empty from relative_xri_part

			List list_relative_xri_part = ((Rule$relative_xri_part) object).rules;
			if (list_relative_xri_part.size() < 1) return;
			object = list_relative_xri_part.get(0);	// xri_path_abs or xri_path_noscheme or ipath_empty	

			// read xri_path_abs or xri_path_noscheme or ipath_emptry ?

			if (object instanceof Rule$xri_path_abs) {

				this.path = new XRI3Path((Rule$xri_path_abs) object);
			} else if (object instanceof Rule$xri_path_noscheme) {

				this.path = new XRI3Path((Rule$xri_path_noscheme) object);
			} else if (object instanceof Rule$ipath_empty) {

				this.path = new XRI3Path((Rule$ipath_empty) object);
			} else {

				throw new ClassCastException(object.getClass().getName());
			}

			// read iquery from relative_xri_ref

			if (list_relative_xri_ref.size() < 3) return;
			object = list_relative_xri_ref.get(2);	// iquery
			this.query = new XRI3Query((Rule$iquery) object);

			// read ifragment from relative_xri_ref

			if (list_relative_xri_ref.size() < 5) return;
			object = list_relative_xri_ref.get(4);	// ifragment
			this.fragment = new XRI3Fragment((Rule$ifragment) object);
		} else {

			throw new ClassCastException(object.getClass().getName());
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public boolean hasAuthority() {

		if (this.xri != null) return(this.xri.hasAuthority());

		return(false);
	}

	public boolean hasPath() {

		if (this.xri != null) return(this.xri.hasPath());

		return(this.path != null);
	}

	public boolean hasQuery() {

		if (this.xri != null) return(this.xri.hasQuery());

		return(this.query != null);
	}

	public boolean hasFragment() {

		if (this.xri != null) return(this.xri.hasFragment());

		return(this.fragment != null);
	}

	public XRIAuthority getAuthority() {

		if (this.xri != null) return(this.xri.getAuthority());

		return(null);
	}

	public XRIPath getPath() {

		if (this.xri != null) return(this.xri.getPath());

		return(this.path);
	}

	public XRIQuery getQuery() {

		if (this.xri != null) return(this.xri.getQuery());

		return(this.query);
	}

	public XRIFragment getFragment() {

		if (this.xri != null) return(this.xri.getFragment());

		return(this.fragment);
	}

	public String toIRINormalForm() {

		if (this.xri != null) return(this.xri.toIRINormalForm());

		return(super.toIRINormalForm());
	}

	public boolean isValidXRI() {

		XRI xri;
		
		try {
			
			xri = this.toXRI();
		} catch (Exception ex) {
			
			return(false);
		}
		
		return(xri != null);
	}
	
	public XRI toXRI() throws ParserException {

		return(new XRI3(this.toString()));
	}

	public XRI3 toXRI3() throws ParserException {

		return(new XRI3(this.toString()));
	}
}
