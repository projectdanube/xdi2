package xdi2.core.xri3.impl;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.xri3.XRIAuthority;
import xdi2.core.xri3.XRISubSegment;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$global_subseg;
import xdi2.core.xri3.impl.parser.Rule$subseg;
import xdi2.core.xri3.impl.parser.Rule$xri_authority;

public class XRI3Authority extends XRI3SyntaxComponent implements XRIAuthority {

	private static final long serialVersionUID = -3281614016180358848L;

	private Rule rule;

	private List subSegments;

	public XRI3Authority(String string) throws ParserException {

		this.rule = Parser.parse("xri-authority", string);
		this.read();
	}

	public XRI3Authority(XRIAuthority xriAuthority, XRISubSegment xriSubSegment) throws ParserException {

		StringBuffer buffer = new StringBuffer();

		buffer.append(xriAuthority.toString());
		buffer.append(xriSubSegment.toString());

		this.rule = Parser.parse("xri-authority", buffer.toString());
		this.read();
	}

	XRI3Authority(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.subSegments = new ArrayList();
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xri_authority

		// read global_subseg from xri_authority

		List list_xri_authority = ((Rule$xri_authority) object).rules;
		if (list_xri_authority.size() < 1) return;
		object = list_xri_authority.get(0);	// global_subseg
		this.subSegments.add(new XRI3SubSegment((Rule$global_subseg) object));

		// read subsegs from xri_authority

		if (list_xri_authority.size() < 2) return;
		for (int i=1; i<list_xri_authority.size(); i++) {

			object = list_xri_authority.get(i);	// subseg
			this.subSegments.add(new XRI3SubSegment((Rule$subseg) object));
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public List getSubSegments() {

		return(this.subSegments);
	}

	public int getNumSubSegments() {

		return(this.subSegments.size());
	}

	public XRISubSegment getSubSegment(int i) {

		return((XRISubSegment) this.subSegments.get(i));
	}

	public XRISubSegment getFirstSubSegment() {

		if (this.subSegments.size() < 1) return(null);

		return((XRISubSegment) this.subSegments.get(0));
	}

	public XRISubSegment getLastSubSegment() {

		if (this.subSegments.size() < 1) return(null);

		return((XRISubSegment) this.subSegments.get(this.subSegments.size() - 1));
	}

	public boolean startsWith(XRISubSegment[] subSegments) {

		if (this.subSegments.size() < subSegments.length) return(false);

		for (int i=0; i<subSegments.length; i++) {

			if (! (this.subSegments.get(i).equals(subSegments[i]))) return(false);
		}

		return(true);
	}
}
