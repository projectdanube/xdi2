package xdi2.core.features.roots;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XDI inner root, represented as a context node and relation.
 * 
 * @author markus
 */
public class InnerRoot extends AbstractRoot {

	private static final long serialVersionUID = -203126514430691007L;

	private Relation relation;

	protected InnerRoot(ContextNode contextNode, Relation relation) {

		super(contextNode);

		this.relation = relation;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node and relation are a valid XDI inner root.
	 * @param contextNode The context node to check.
	 * @param relation The relation to check.
	 * @return True if the context node and relation are a valid XDI inner root.
	 */
	public static boolean isValid(ContextNode contextNode, Relation relation) {

		if (! contextNode.equals(relation.follow())) return false;
		if (! createInnerRootXri(relation.getContextNode().getXri(), relation.getArcXri()).equals(contextNode.getXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI inner root bound to a given relation.
	 * @param relation The relation that is an XDI inner root.
	 * @return The XDI inner root.
	 */
	public static InnerRoot fromContextNodeAndRelation(ContextNode contextNode, Relation relation) {

		if (! isValid(contextNode, relation)) return null;

		return new InnerRoot(contextNode, relation);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying relation to which this XDI inner root is bound.
	 * @return A relation that represents the XDI inner root.
	 */
	public Relation getRelation() {

		return this.relation;
	}

	/**
	 * Returns the subject XRI of this XDI inner root.
	 * @return The subject XRI.
	 */
	public XDI3Segment getSubjectOfInnerRoot() {

		return getSubjectOfInnerRootXri(this.getContextNode().getArcXri());
	}

	/**
	 * Returns the predicate XRI of this XDI inner root.
	 * @return The predicate XRI.
	 */
	public XDI3Segment getPredicateOfInnerRoot() {

		return getPredicateOfInnerRootXri(this.getContextNode().getArcXri());
	}

	/*
	 * Methods for XDI inner root XRIs.
	 */

	/**
	 * Returns the inner root XRI of a subject XRI and a predicate XRI.
	 * @param subject A subject XRI.
	 * @param predicate A subject XRI.
	 * @return The inner root XRI of the subject XRI and the predicate XRI.
	 */
	public static XDI3SubSegment createInnerRootXri(XDI3Segment subject, XDI3Segment predicate) {

		return XDI3SubSegment.create("(" + subject.toString() + "/" + predicate.toString() + ")");
	}

	/**
	 * Returns the subject XRI of the inner root XRI.
	 * @param xri An inner root XRI.
	 * @return The subject XRI of the inner root XRI.
	 */
	public static XDI3Segment getSubjectOfInnerRootXri(XDI3SubSegment xri) {

		if (! xri.hasXRef()) return null;

		XDI3XRef xref = xri.getXRef();
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialSubject();
	}

	/**
	 * Returns the predicate XRI of the inner root XRI.
	 * @param xri An inner root XRI.
	 * @return The predicate XRI of the inner root XRI.
	 */
	public static XDI3Segment getPredicateOfInnerRootXri(XDI3SubSegment xri) {

		if (! xri.hasXRef()) return null;

		XDI3XRef xref = xri.getXRef();
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialPredicate();
	}

	/**
	 * Checks if a given XRI is an inner root XRI.
	 * @param xri An inner root XRI.
	 * @return True, if the XRI is an inner root XRI.
	 */
	public static boolean isInnerRootXri(XDI3SubSegment xri) {

		return getSubjectOfInnerRootXri(xri) != null && getPredicateOfInnerRootXri(xri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingRelationInnerRootIterator extends NotNullIterator<InnerRoot> {

		public MappingRelationInnerRootIterator(Iterator<Relation> relations) {

			super(new MappingIterator<Relation, InnerRoot> (relations) {

				@Override
				public InnerRoot map(Relation relation) {

					return InnerRoot.fromContextNodeAndRelation(relation.follow(), relation);
				}
			});
		}
	}
}
