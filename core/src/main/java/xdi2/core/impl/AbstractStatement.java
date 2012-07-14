package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public abstract class AbstractStatement implements Statement {

	private static final long serialVersionUID = -8879896347494275688L;

	/**
	 * Creates an XDI statement from its three components
	 * @param subject The statement's subject
	 * @param predicate The statement's predicate
	 * @param object The statement's object
	 * @return An XDI statement
	 */
	public static Statement fromComponents(final XRI3Segment subject, final XRI3Segment predicate, final XRI3Segment object) {

		if (XDIConstants.XRI_S_CONTEXT.equals(predicate)) {

			return new AbstractContextNodeStatement() {

				private static final long serialVersionUID = -8541962994048481222L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		} else if (XDIConstants.XRI_S_LITERAL.equals(predicate)) {

			return new AbstractLiteralStatement() {

				private static final long serialVersionUID = -7808697310492171096L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		} else {

			return new AbstractRelationStatement() {

				private static final long serialVersionUID = 7444790938914661808L;

				@Override
				public XRI3Segment getSubject() {

					return subject;
				}

				@Override
				public XRI3Segment getPredicate() {

					return predicate;
				}

				@Override
				public XRI3Segment getObject() {

					return object;
				}
			};
		}
	}

	/**
	 * Creates an XDI statement from an XRI reference in the form subject/predicate/object
	 * @param reference The XRI reference
	 * @return An XDI statement
	 */
	public static Statement fromXriReference(XRI3Reference reference) throws Xdi2ParseException {

		XRI3Segment subject, predicate, object;

		try {

			if (reference.hasAuthority()) {

				if (reference.getPath().getNumSegments() != 2) throw new Xdi2ParseException("Invalid number of segments.");

				subject = new XRI3Segment(reference.getAuthority().toString());
				predicate = new XRI3Segment(reference.getPath().getSegment(0).toString());
				object = new XRI3Segment(reference.getPath().getSegment(1).toString());
			} else {

				if (reference.getPath().getNumSegments() != 3) throw new Xdi2ParseException("Invalid number of segments.");

				subject = new XRI3Segment(reference.getPath().getSegment(0).toString());
				predicate = new XRI3Segment(reference.getPath().getSegment(1).toString());
				object = new XRI3Segment(reference.getPath().getSegment(2).toString());
			}
		} catch (Exception ex) {

			throw new Xdi2ParseException("Cannot parse statement: " + ex.getMessage(), ex);
		}

		return fromComponents(subject, predicate, object);
	}

	/**
	 * Creates an XDI statement from an XRI segment in the form (subject/predicate/object)
	 * @param segment The XRI segment
	 * @return An XDI statement
	 */
	public static Statement fromXriSegment(XRI3Segment segment) throws Xdi2ParseException {

		XRI3SubSegment subSegment = (XRI3SubSegment) segment.getFirstSubSegment();
		if (subSegment == null) throw new Xdi2ParseException("No subsegment found: " + segment.toString());

		XRI3XRef xref = (XRI3XRef) subSegment.getXRef();
		if (xref == null) throw new Xdi2ParseException("No cross-reference found: " + segment.toString());

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference == null) throw new Xdi2ParseException("No XRI cross-reference found: " + segment.toString());

		return fromXriReference(reference);
	}

	/**
	 * Creates an XDI statement from a string in the form subject/predicate/object
	 * @param string The string
	 * @return An XDI statement
	 */
	public static Statement fromString(String string) throws Xdi2ParseException {

		return fromXriReference(new XRI3Reference(string));
	}

	/*
	 * Instance methods
	 */

	@Override
	public XRI3 getXRI3() {

		return new XRI3(this.toString());
	}

	@Override
	public Graph getGraph() {

		return null;
	}

	@Override
	public void delete() {

	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getSubject());
		builder.append("/");
		builder.append(this.getPredicate());
		builder.append("/");
		builder.append(this.getObject());

		return builder.toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Statement)) return false;
		if (object == this) return true;

		Statement other = (Statement) object;

		// two statements are equal if their components are equals

		if (! this.getSubject().equals(other.getSubject())) return false;
		if (! this.getPredicate().equals(other.getPredicate())) return false;
		if (! this.getObject().equals(other.getObject())) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getSubject() == null ? 0 : this.getSubject().hashCode());
		hashCode = (hashCode * 31) + (this.getPredicate() == null ? 0 : this.getPredicate().hashCode());
		hashCode = (hashCode * 31) + (this.getObject() == null ? 0 : this.getObject().hashCode());

		return hashCode;
	}

	@Override
	public int compareTo(Statement other) {

		if (other == null || other == this) return(0);

		int c;

		// compare subject

		c = this.getSubject().compareTo(other.getSubject());
		if (c != 0) return c;

		// compare predicate

		c = this.getPredicate().compareTo(other.getPredicate());
		if (c != 0) return c;

		// compare objects

		c = this.getObject().compareTo(other.getObject());
		if (c != 0) return c;

		return 0;
	}

	/*
	 * Sub-classes
	 */

	public static abstract class AbstractContextNodeStatement extends AbstractStatement implements ContextNodeStatement {

		private static final long serialVersionUID = -7006808512493295364L;

		@Override
		public ContextNode getContextNode() {

			return null;
		}
	}

	public static abstract class AbstractRelationStatement extends AbstractStatement implements RelationStatement {

		private static final long serialVersionUID = -2393268622327844933L;

		@Override
		public Relation getRelation() {

			return null;
		}
	}

	public static abstract class AbstractLiteralStatement extends AbstractStatement implements LiteralStatement {

		private static final long serialVersionUID = -7876412291137305476L;

		@Override
		public Literal getLiteral() {

			return null;
		}
	}
}
