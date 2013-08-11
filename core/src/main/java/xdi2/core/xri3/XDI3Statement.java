package xdi2.core.xri3;

import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.util.XDI3Util;

public class XDI3Statement extends XDI3SyntaxComponent {

	private static final long serialVersionUID = -1416735368366011077L;

	private XDI3Segment subject;
	private XDI3Segment predicate;
	private Object object;

	XDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3SubSegment object) {

		this(string, subject, predicate, (Object) object);
	}

	XDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, XDI3Segment object) {

		this(string, subject, predicate, (Object) object);
	}

	XDI3Statement(String string, XDI3Segment subject, XDI3Segment predicate, Object object) {

		super(string);

		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public static XDI3Statement create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3Statement(string);
	}

	public XDI3Segment getSubject() {

		return this.subject;
	}

	public XDI3Segment getPredicate() {

		return this.predicate;
	}

	public Object getObject() {

		return this.object;
	}

	public boolean isContextNodeStatement() {

		return XDIConstants.XRI_S_CONTEXT.equals(this.getPredicate()) && (this.getObject() instanceof XDI3SubSegment);
	}

	public boolean isRelationStatement() {

		return (! XDIConstants.XRI_S_CONTEXT.equals(this.getPredicate())) && (! XDIConstants.XRI_S_LITERAL.equals(this.getPredicate())) && (this.getObject() instanceof XDI3Segment);
	}

	public boolean isLiteralStatement() {

		return XDIConstants.XRI_S_LITERAL.equals(this.getPredicate()) && AbstractLiteral.isValidLiteralData(this.getObject());
	}

	public boolean hasInnerRootStatement() {

		return isRelationStatement() && 
				this.getTargetContextNodeXri().getNumSubSegments() == 1 &&
				this.getTargetContextNodeXri().getFirstSubSegment().hasXRef() &&
				this.getTargetContextNodeXri().getFirstSubSegment().getXRef().hasStatement();
	}

	public XDI3Segment getContextNodeXri() {

		return this.getSubject();
	}

	public XDI3SubSegment getContextNodeArcXri() {

		if (this.isContextNodeStatement()) {

			return (XDI3SubSegment) this.getObject();
		}

		return null;
	}

	public XDI3Segment getRelationArcXri() {

		if (this.isRelationStatement()) {

			return this.getPredicate();
		}

		return null;
	}

	public XDI3Segment getTargetContextNodeXri() {

		if (this.isContextNodeStatement()) {

			return XDI3Util.concatXris(this.getSubject(), (XDI3SubSegment) this.getObject());
		} else if (this.isRelationStatement()) {

			return (XDI3Segment) this.getObject();
		}

		return null;
	}

	public XDI3Statement getInnerRootStatement() {

		if (this.isRelationStatement()) {

			XDI3Segment targetContextNodeXri = this.getTargetContextNodeXri();
			if (targetContextNodeXri == null) return null;

			XDI3XRef xref = targetContextNodeXri.getFirstSubSegment().getXRef();
			if (xref == null) return null;

			XDI3Statement statement = xref.getStatement();
			if (statement == null) return null;

			return statement;
		}

		return null;
	}

	public Object getLiteralData() {

		if (this.isLiteralStatement()) {

			return this.getObject();
		}

		return null;
	}
}
