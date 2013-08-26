package xdi2.core.xri3;

import java.util.Collections;
import java.util.List;

public final class XDI3Segment extends XDI3SyntaxComponent {

	private static final long serialVersionUID = 2153450076797516335L;

	private List<XDI3SubSegment> subSegments;

	XDI3Segment(String string, List<XDI3SubSegment> subSegments) {

		super(string);

		this.subSegments = subSegments;
	}

	public static XDI3Segment create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3Segment(string);
	}

	public static XDI3Segment fromComponent(XDI3SubSegment subSegment) {

		return new XDI3Segment(subSegment.toString(), Collections.singletonList(subSegment));
	}

	public static XDI3Segment fromComponents(List<XDI3SubSegment> subSegments) {

		StringBuffer buffer = new StringBuffer();
		for (XDI3SubSegment subSegment : subSegments) buffer.append(subSegment.toString());

		return new XDI3Segment(buffer.toString(), subSegments);
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
}
