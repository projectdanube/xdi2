package xdi2.core.xri3;

import java.util.List;

import xdi2.core.xri3.parser.XDI3Parser;
import xdi2.core.xri3.parser.XDI3ParserRegistry;

public class XDI3Segment extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 2153450076797516335L;

	private String literal;
	private List<XDI3SubSegment> subSegments;

	public XDI3Segment(String string, String literal, List<XDI3SubSegment> subSegments) {

		super(string);

		this.literal = literal;
		this.subSegments = subSegments;
	}

	public static XDI3Segment create(XDI3Parser parser, String string) {

		return parser.parseXDI3Segment(string);
	}

	public static XDI3Segment create(String string) {

		return create(XDI3ParserRegistry.getInstance(), string);
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public String getLiteral() {

		return this.literal;
	}

	public List<XDI3SubSegment> getSubSegments() {

		return this.subSegments;
	}

	public int getNumSubSegments() {

		return this.subSegments.size();
	}

	public XDI3SubSegment getSubSegment(int i) {

		return this.subSegments.get(i);
	}

	public XDI3SubSegment getFirstSubSegment() {

		if (this.subSegments.size() < 1) return null;

		return this.subSegments.get(0);
	}

	public XDI3SubSegment getLastSubSegment() {

		if (this.subSegments.size() < 1) return null;

		return this.subSegments.get(this.subSegments.size() - 1);
	}

	public boolean isINumber() {

		// all subsegments must be persistent

		for (XDI3SubSegment subSegment : this.getSubSegments()) {

			if (! subSegment.isPersistent()) return false;
		}

		return true;
	}

	public boolean startsWith(XDI3SubSegment[] subSegments) {

		if (this.subSegments.size() < subSegments.length) return false;

		for (int i=0; i<subSegments.length; i++) {

			if (! (this.subSegments.get(i).equals(subSegments[i]))) return false;
		}

		return true;
	}
}
