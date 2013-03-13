package xdi2.core.xri3;

import xdi2.core.util.XDIUtil;
import xdi2.core.util.XRIUtil;

public class XDI3Statement extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -1416735368366011077L;

	public static final XDI3Segment XRI_S_CONTEXT = XDI3Segment.create("()");
	public static final XDI3Segment XRI_S_LITERAL = XDI3Segment.create("!");

	private XDI3Segment subject;
	private XDI3Segment predicate;
	private XDI3Segment object;

	XDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3Segment object) {

		super(string);

		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public static XDI3Statement create(XDI3Parser parser, String string) {

		return parser.parseXDI3Statement(string);
	}

	public static XDI3Statement create(String string) {

		return create(XDI3ParserRegistry.getInstance().getParser(), string);
	}

	public XDI3Segment getSubject() {

		return this.subject;
	}

	public XDI3Segment getPredicate() {

		return this.predicate;
	}

	public XDI3Segment getObject() {

		return this.object;
	}

	public boolean isContextNodeStatement() {

		return XRI_S_CONTEXT.equals(this.getPredicate());
	}

	public boolean isLiteralStatement() {

		return XRI_S_LITERAL.equals(this.getPredicate()) && XDIUtil.isLiteralSegment(this.getObject());
	}

	public boolean isRelationStatement() {

		return (! XRI_S_CONTEXT.equals(this.getPredicate())) && (! XRI_S_LITERAL.equals(this.getPredicate()));
	}

	public boolean hasInnerRootStatement() {

		return isRelationStatement() && 
				this.getTargetContextNodeXri().getNumSubSegments() == 1 &&
				this.getTargetContextNodeXri().getFirstSubSegment().hasXRef() &&
				this.getTargetContextNodeXri().getFirstSubSegment().getXRef().hasStatement();
	}

	public XDI3Segment getContextNodeXri() {

		if (this.isContextNodeStatement()) {

			return XRIUtil.expandXri(this.getObject(), this.getSubject());
		} else {

			return this.getSubject();
		}
	}

	public XDI3Segment getArcXri() {

		if (! this.isRelationStatement()) return null;

		return this.getPredicate();
	}

	public XDI3Segment getTargetContextNodeXri() {

		if (! this.isRelationStatement()) return null;

		return this.getObject();
	}

	public String getLiteralData() {

		if (! this.isLiteralStatement()) return null;

		return XDIUtil.literalSegmentToString(this.getObject());
	}

	public XDI3Statement getInnerRootStatement() {

		if (! this.isRelationStatement()) return null;

		XDI3Segment targetContextNodeXri = this.getTargetContextNodeXri();
		if (targetContextNodeXri == null) return null;

		XDI3XRef xref = targetContextNodeXri.getFirstSubSegment().getXRef();
		if (xref == null) return null;

		XDI3Statement statement = xref.getStatement();
		if (statement == null) return null;

		return statement;
	}
}
