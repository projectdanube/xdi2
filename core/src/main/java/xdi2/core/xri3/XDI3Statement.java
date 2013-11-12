package xdi2.core.xri3;

import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.util.StatementUtil;
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

	/**
	 * Parses an XDI statement
	 * @param string A string to parse
	 * @return An XDI statement
	 */
	public static XDI3Statement create(String string) {

		return XDI3ParserRegistry.getInstance().getParser().parseXDI3Statement(string);
	}

	/**
	 * Creates an XDI statement from its three components
	 * @param subject The statement's subject
	 * @param predicate The statement's predicate
	 * @param object The statement's object
	 * @return An XDI statement
	 */
	public static XDI3Statement fromComponents(final XDI3Segment subject, final XDI3Segment predicate, final Object object) {

		if (XDIConstants.XRI_S_CONTEXT.equals(predicate)) {

			return fromContextNodeComponents(subject, (XDI3SubSegment) object);
		} else if (XDIConstants.XRI_S_LITERAL.equals(predicate)) {

			return fromLiteralComponents(subject, object);
		} else {

			return fromRelationComponents(subject, predicate, (XDI3Segment) object);
		}
	}

	/**
	 * Creates an XDI statement from a context node XRI and an arc XRI.
	 * @param contextNodeXri The context node XRI
	 * @param contextNodeArcXri The arc XRI
	 * @return An XDI statement
	 */
	public static XDI3Statement fromContextNodeComponents(final XDI3Segment contextNodeXri, final XDI3SubSegment contextNodeArcXri) {

		String string = contextNodeXri.toString() + "/" + XDIConstants.XRI_S_CONTEXT.toString() + "/" + contextNodeArcXri.toString();

		return new XDI3Statement(string, contextNodeXri, XDIConstants.XRI_S_CONTEXT, contextNodeArcXri);
	}

	/**
	 * Creates an XDI statement from a context node XRI, arc XRI, and target context node XRI.
	 * @param contextNodeXri The context node XRI
	 * @param relationArcXri The arc XRI
	 * @return An XDI statement
	 */
	public static XDI3Statement fromRelationComponents(final XDI3Segment contextNodeXri, final XDI3Segment relationArcXri, final XDI3Segment targetContextNodeXri) {

		String string = contextNodeXri.toString() + "/" + relationArcXri.toString() + "/" + targetContextNodeXri.toString();

		return new XDI3Statement(string, contextNodeXri, relationArcXri, targetContextNodeXri);
	}

	/**
	 * Creates an XDI statement from a context node XRI and literal data.
	 * @param contextNodeXri The context node XRI
	 * @param literalData The literal data
	 * @return An XDI statement
	 */
	public static XDI3Statement fromLiteralComponents(final XDI3Segment contextNodeXri, final Object literalData) {

		String string = contextNodeXri.toString() + "/" + XDIConstants.XRI_S_LITERAL.toString() + "/" + AbstractLiteral.literalDataToString(literalData);

		return new XDI3Statement(string, contextNodeXri, XDIConstants.XRI_S_LITERAL, literalData);
	}

	/**
	 * Creates an XDI statement from an XRI segment in the form (subject/predicate/object)
	 * @param segment The XRI segment
	 * @return An XDI statement
	 */
	public static XDI3Statement fromXriSegment(XDI3Segment segment) throws Xdi2ParseException {

		XDI3SubSegment subSegment = segment.getFirstSubSegment();
		if (subSegment == null) throw new Xdi2ParseException("No subsegment found: " + segment.toString());

		XDI3XRef xref = subSegment.getXRef();
		if (xref == null) throw new Xdi2ParseException("No cross-reference found: " + segment.toString());

		XDI3Statement statement = xref.getStatement();
		if (statement == null) throw new Xdi2ParseException("No statement found: " + segment.toString());

		return statement;
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

	public Object getLiteralData() {

		if (this.isLiteralStatement()) {

			return this.getObject();
		}

		return null;
	}

	/*
	 * Methods related to inner root notation
	 */

	public XDI3Statement getInnerRootNotationStatement() {

		if (! this.isRelationStatement()) return null;

		XDI3Segment innerRootNotationTargetContextNodeSegment = this.getTargetContextNodeXri();
		if (innerRootNotationTargetContextNodeSegment == null) return null;

		XDI3SubSegment innerRootNotationTargetContextNodeSubSegment = innerRootNotationTargetContextNodeSegment.getFirstSubSegment();
		if (innerRootNotationTargetContextNodeSubSegment == null) return null;

		XDI3XRef innerRootNotationXref = innerRootNotationTargetContextNodeSubSegment.getXRef();
		if (innerRootNotationXref == null) return null;

		XDI3Statement innerRootNotationStatement = innerRootNotationXref.getStatement();
		if (innerRootNotationStatement == null) return null;

		return innerRootNotationStatement;
	}

	public boolean isInnerRootNotation() {

		return this.getInnerRootNotationStatement() != null;
	}

	public XDI3Statement fromInnerRootNotation(boolean recursive) {

		XDI3Statement innerRootStatement = this.getInnerRootNotationStatement();
		if (innerRootStatement == null) return this;

		if (innerRootStatement.isInnerRootNotation()) {

			XDI3Statement statement = XDI3Statement.fromRelationComponents(this.getContextNodeXri(), this.getRelationArcXri(), XDI3Segment.fromComponent(XDI3SubSegment.fromComponents(null, false, false, null, XDI3XRef.fromComponents(XDIConstants.XS_ROOT, null, innerRootStatement.fromInnerRootNotation(recursive), null, null, null, null))));

			if (recursive) statement = statement.fromInnerRootNotation(recursive);

			return statement;
		} else {

			XDI3SubSegment innerRootSubSegment = XDI3SubSegment.fromComponents(null, false, false, null, XDI3XRef.fromComponents(XDIConstants.XS_ROOT, null, null, this.getSubject(), this.getPredicate(), null, null));
			XDI3Segment innerRootSegment = XDI3Segment.fromComponent(innerRootSubSegment);

			XDI3Statement statement = StatementUtil.concatXriStatement(innerRootSegment, innerRootStatement, true);

			return statement;
		}
	}

	public XDI3Statement toInnerRootNotation(boolean recursive) {

		if (this.isInnerRootNotation()) {

			XDI3Statement innerRootStatement = this.getInnerRootNotationStatement();

			XDI3Statement statement = XDI3Statement.fromRelationComponents(this.getContextNodeXri(), this.getRelationArcXri(), XDI3Segment.fromComponent(XDI3SubSegment.fromComponents(null, false, false, null, XDI3XRef.fromComponents(XDIConstants.XS_ROOT, null, innerRootStatement.toInnerRootNotation(recursive), null, null, null, null))));

			return statement;
		} else {

			XDI3SubSegment subjectFirstSubSegment = this.getSubject().getFirstSubSegment();

			if (subjectFirstSubSegment == null || (! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return this;

			XDI3Segment innerRootSubject = this.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
			XDI3Segment innerRootPredicate = this.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

			XDI3Statement innerRootStatement = StatementUtil.removeStartXriStatement(this, XDI3Segment.fromComponent(subjectFirstSubSegment), true);
			if (innerRootStatement == null) return this;

			if (recursive) innerRootStatement = innerRootStatement.toInnerRootNotation(recursive);

			XDI3Statement statement = XDI3Statement.fromRelationComponents(innerRootSubject, innerRootPredicate, XDI3Segment.fromComponent(XDI3SubSegment.fromComponents(null, false, false, null, XDI3XRef.fromComponents(XDIConstants.XS_ROOT, null, innerRootStatement, null, null, null, null))));

			return statement;
		}
	}
}
