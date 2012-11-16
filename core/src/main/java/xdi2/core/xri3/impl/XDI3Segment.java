package xdi2.core.xri3.impl;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.xri3.XRILiteral;
import xdi2.core.xri3.XRISegment;
import xdi2.core.xri3.XRISubSegment;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$literal;
import xdi2.core.xri3.impl.parser.Rule$xdi_segment;
import xdi2.core.xri3.impl.parser.Rule$xdi_subseg;

public class XDI3Segment extends XRI3SyntaxComponent implements XRISegment {

	private static final long serialVersionUID = 2153450076797516335L;

	private Rule rule;

	private XRI3Literal literal;
	private List subSegments;

	public XDI3Segment(String string) throws ParserException {

		this.rule = Parser.parse("xdi-segment", string);
		this.read();
	}

	XDI3Segment(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.subSegments = new ArrayList();
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xdi_segment

		// read literal or xdi_subseg from xdi_segment

		List list_xdi_segment = ((Rule$xdi_segment) object).rules;
		if (list_xdi_segment.size() < 1) return;
		object = list_xdi_segment.get(0);	// literal or xdi_subseg

		// literal or xdi_subseg?

		if (object instanceof Rule$literal) {

			this.literal = new XRI3Literal((Rule$literal) object);
		} else if (object instanceof Rule$xdi_subseg) {

			this.subSegments.add(new XDI3SubSegment((Rule$xdi_subseg) object));
		} else {

			throw new ClassCastException(object.getClass().getName());
		}

		// read xdi_subsegs from xdi_segment

		if (list_xdi_segment.size() < 2) return;
		for (int i=1; i<list_xdi_segment.size(); i++) {

			object = list_xdi_segment.get(i);	// xdi_subseg
			this.subSegments.add(new XDI3SubSegment((Rule$xdi_subseg) object));
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public boolean hasLiteral() {

		return(this.literal != null);
	}

	public XRILiteral getLiteral() {

		return(this.literal);
	}

	public List getSubSegments() {

		return(this.subSegments);
	}

	public int getNumSubSegments() {

		return(this.subSegments.size());
	}

	public XDI3SubSegment getSubSegment(int i) {

		return((XDI3SubSegment) this.subSegments.get(i));
	}

	public XDI3SubSegment getFirstSubSegment() {

		if (this.subSegments.size() < 1) return(null);

		return((XDI3SubSegment) this.subSegments.get(0));
	}

	public XDI3SubSegment getLastSubSegment() {

		if (this.subSegments.size() < 1) return(null);

		return((XDI3SubSegment) this.subSegments.get(this.subSegments.size() - 1));
	}

	public boolean startsWith(XRISubSegment[] subSegments) {

		if (this.subSegments.size() < subSegments.length) return(false);

		for (int i=0; i<subSegments.length; i++) {

			if (! (this.subSegments.get(i).equals(subSegments[i]))) return(false);
		}

		return(true);
	}
}
