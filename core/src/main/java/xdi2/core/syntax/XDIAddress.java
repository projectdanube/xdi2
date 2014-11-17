package xdi2.core.syntax;

import java.util.Collections;
import java.util.List;

import xdi2.core.util.XDIAddressUtil;

public final class XDIAddress extends XDIIdentifier {

	private static final long serialVersionUID = 2153450076797516335L;

	private List<XDIArc> arcs;

	XDIAddress(String string, List<XDIArc> arcs) {

		super(string);

		this.arcs = arcs;
	}

	public static XDIAddress create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIAddress(string);
	}

	public static XDIAddress fromComponents(List<XDIArc> XDIarcs) {

		StringBuffer buffer = new StringBuffer();
		for (XDIArc XDIarc : XDIarcs) buffer.append(XDIarc.toString());

		return new XDIAddress(buffer.toString(), XDIarcs);
	}

	public static XDIAddress fromComponent(XDIArc XDIarc) {

		return new XDIAddress(XDIarc.toString(), Collections.singletonList(XDIarc));
	}

	public List<XDIArc> getXDIArcs() {

		return this.arcs;
	}

	public int getNumXDIArcs() {

		return this.arcs.size();
	}

	public XDIArc getXDIArc(int i) {

		return this.arcs.get(i);
	}

	public XDIArc getFirstXDIArc() {

		if (this.arcs.size() < 1) return null;

		return this.arcs.get(0);
	}

	public XDIArc getLastXDIArc() {

		if (this.arcs.size() < 1) return null;

		return this.arcs.get(this.arcs.size() - 1);
	}

	public XDIAddress getContextNodeXDIAddress() {

		if (this.isLiteralNodeXDIAddress()) {

			return XDIAddressUtil.parentXDIAddress(this, -1);
		} else {

			return this;
		}
	}

	public boolean isLiteralNodeXDIAddress() {

		XDIArc XDIarc = this.getLastXDIArc();
		if (XDIarc == null) return false;

		return XDIarc.isLiteralNodeXDIArc();
	}
}
