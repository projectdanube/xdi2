package xdi2.core.syntax;

import java.util.Collections;
import java.util.List;

import xdi2.core.syntax.parser.ParserRegistry;

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

	public static XDIAddress fromComponents(List<XDIArc> arcs) {

		StringBuffer buffer = new StringBuffer();
		for (XDIArc arc : arcs) buffer.append(arc.toString());

		return new XDIAddress(buffer.toString(), arcs);
	}

	public static XDIAddress fromComponent(XDIArc arc) {

		return new XDIAddress(arc.toString(), Collections.singletonList(arc));
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
}
