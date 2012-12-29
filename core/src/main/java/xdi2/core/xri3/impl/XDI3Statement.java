package xdi2.core.xri3.impl;

import java.util.List;

import xdi2.core.constants.XDIConstants;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$xdi_object;
import xdi2.core.xri3.impl.parser.Rule$xdi_predicate;
import xdi2.core.xri3.impl.parser.Rule$xdi_segment;
import xdi2.core.xri3.impl.parser.Rule$xdi_statement;
import xdi2.core.xri3.impl.parser.Rule$xdi_subject;

public class XDI3Statement extends XRI3SyntaxComponent {

	private static final long serialVersionUID = -1416735368366011077L;

	public static final XDI3Segment XRI_S_CONTEXT = new XDI3Segment("()");
	public static final XDI3Segment XRI_S_LITERAL = new XDI3Segment("!");

	private Rule rule;

	private XDI3Segment subject;
	private XDI3Segment predicate;
	private XDI3Segment object;

	public XDI3Statement(String string) throws ParserException {

		this.rule = Parser.parse("xdi-statement", string);
		this.read();
	}

	XDI3Statement(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {

		this.subject = null;
		this.predicate = null;
		this.object = null;
	}

	private void read() {

		this.reset();

		Object object = this.rule;	// xdi_statement

		// read xdi_subject from xdi_statement

		List list_xdi_statement = ((Rule$xdi_statement) object).rules;
		if (list_xdi_statement.size() < 1) return;
		object = list_xdi_statement.get(0);	// xdi_subject

		// read xdi_segment from xdi_subject

		List list_xdi_subject = ((Rule$xdi_subject) object).rules;
		if (list_xdi_subject.size() < 1) return;
		object = list_xdi_subject.get(0); // xdi_segment
		this.subject = new XDI3Segment((Rule$xdi_segment) object);

		// read xdi_predicate from xdi_statement

		if (list_xdi_statement.size() < 3) return;
		object = list_xdi_statement.get(2);	// xdi_predicate

		// read xdi_segment from xdi_predicate

		List list_xdi_predicate = ((Rule$xdi_predicate) object).rules;
		if (list_xdi_predicate.size() < 1) return;
		object = list_xdi_predicate.get(0); // xdi_segment
		this.predicate = new XDI3Segment((Rule$xdi_segment) object);

		// read xdi_object from xdi_statement

		if (list_xdi_statement.size() < 5) return;
		object = list_xdi_statement.get(4);	// xdi_object

		// read xdi_segment from xdi_object

		List list_xdi_object = ((Rule$xdi_object) object).rules;
		if (list_xdi_object.size() < 1) return;
		object = list_xdi_object.get(0); // xdi_segment
		this.object = new XDI3Segment((Rule$xdi_segment) object);
	}

	public Rule getParserObject() {

		return(this.rule);
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

		return (! this.isContextNodeStatement()) && (! this.isLiteralStatement());
	}

	public XDI3Segment getContextNodeXri() {

		if (XDIConstants.XRI_S_CONTEXT.equals(this.getPredicate())) {

			if (XDIConstants.XRI_S_ROOT.equals(this.getSubject())) {

				return this.getObject();
			} else {

				return new XDI3Segment("" + this.getSubject() + this.getObject());
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

		return new XDI3Segment("(" + this.toString() + ")");
	}
}
