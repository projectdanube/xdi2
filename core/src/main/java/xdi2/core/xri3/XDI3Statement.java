package xdi2.core.xri3;

import xdi2.core.constants.XDIConstants;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.parser.XDI3Parser;
import xdi2.core.xri3.parser.XDI3ParserRegistry;

public class XDI3Statement extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -1416735368366011077L;

	public static final XDI3Segment XRI_S_CONTEXT = XDI3Segment.create("()");
	public static final XDI3Segment XRI_S_LITERAL = XDI3Segment.create("!");

	private XDI3Segment subject;
	private XDI3Segment predicate;
	private XDI3Segment object;

	public XDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3Segment object) {

		super(string);
		
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public static XDI3Statement create(XDI3Parser parser, String string) {

		return parser.parseXDI3Statement(string);
	}

	public static XDI3Statement create(String string) {

		return create(XDI3ParserRegistry.getInstance(), string);
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

		return XRI_S_LITERAL.equals(this.getPredicate()) && XDIUtil.isDataXriSegment(this.getObject());
	}

	public boolean isRelationStatement() {

		return (! XRI_S_CONTEXT.equals(this.getPredicate())) && (! XRI_S_LITERAL.equals(this.getPredicate()));
	}

	public XDI3Segment getContextNodeXri() {

		if (XDIConstants.XRI_S_CONTEXT.equals(this.getPredicate())) {

			if (XDIConstants.XRI_S_ROOT.equals(this.getSubject())) {

				return this.getObject();
			} else {

				return XDI3Segment.create("" + this.getSubject() + this.getObject());
			}
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

		return XDIUtil.dataXriSegmentToString(this.getObject());
	}

	public XDI3Segment toXriSegment() {

		return XDI3Segment.create("(" + this.toString() + ")");
	}
}
