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
 * An XDI inner root, represented as a context node.
 * 
 * @author markus
 */
public class InnerRoot extends AbstractRoot {

	private static final long serialVersionUID = -203126514430691007L;

	protected InnerRoot(ContextNode contextNode) {

		super(contextNode);
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
	public static boolean isValid(ContextNode contextNode) {

		XDI3Segment subject = InnerRoot.getSubjectOfInnerRootXri(contextNode.getArcXri());
		XDI3Segment predicate = InnerRoot.getPredicateOfInnerRootXri(contextNode.getArcXri());
		if (subject == null || predicate == null) return false;

		ContextNode parentContextNode = contextNode.getContextNode();
		if (parentContextNode == null) return false;

		ContextNode subjectContextNode = parentContextNode.findContextNode(subject, false);
		if (subjectContextNode == null) return false;

		if (! subjectContextNode.containsRelation(predicate, contextNode.getXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI inner root bound to a given context node.
	 * @param contextNode The context node that is an XDI inner root.
	 * @return The XDI inner root.
	 */
	public static InnerRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new InnerRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying subject context node of this XDI inner root.
	 * @return The subject context node of this XDI inner root.
	 */
	public ContextNode getSubjectContexNode() {

		XDI3Segment subject = InnerRoot.getSubjectOfInnerRootXri(this.getContextNode().getArcXri());
		if (subject == null) return null;

		ContextNode parentContextNode = this.getContextNode().getContextNode();
		if (parentContextNode == null) return null;

		ContextNode subjectContextNode = parentContextNode.findContextNode(subject, false);
		if (subjectContextNode == null) return null;

		return subjectContextNode;
	}

	/**
	 * Returns the underlying predicate relation of this XDI inner root.
	 * @return The predicate relation of this XDI inner root.
	 */
	public Relation getPredicateRelation() {

		XDI3Segment predicate = InnerRoot.getPredicateOfInnerRootXri(this.getContextNode().getArcXri());
		if (predicate == null) return null;

		ContextNode subjectContextNode = this.getSubjectContexNode();
		if (subjectContextNode == null) return null;

		Relation predicateRelation = subjectContextNode.getRelation(predicate, this.getContextNode().getXri());
		if (predicateRelation == null) return null;

		return predicateRelation;
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

		if (xri == null) return null;
		
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

		if (xri == null) return null;
		
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

	public static class MappingContextNodeInnerRootIterator extends NotNullIterator<InnerRoot> {

		public MappingContextNodeInnerRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, InnerRoot> (contextNodes) {

				@Override
				public InnerRoot map(ContextNode contextNode) {

					return InnerRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
