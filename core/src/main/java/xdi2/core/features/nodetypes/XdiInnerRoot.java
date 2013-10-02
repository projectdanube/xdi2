package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
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
public class XdiInnerRoot extends XdiAbstractRoot {

	private static final long serialVersionUID = -203126514430691007L;

	protected XdiInnerRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI inner root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI inner root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isInnerRootArcXri(contextNode.getArcXri()) &&
				XdiAbstractRoot.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI inner root bound to a given context node.
	 * @param contextNode The context node that is an XDI inner root.
	 * @return The XDI inner root.
	 */
	public static XdiInnerRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiInnerRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying subject context node of this XDI inner root.
	 * @return The subject context node of this XDI inner root.
	 */
	public ContextNode getSubjectContextNode() {

		XDI3Segment subject = XdiInnerRoot.getSubjectOfInnerRootXri(this.getContextNode().getArcXri());
		if (subject == null) return null;

		ContextNode parentContextNode = this.getContextNode().getContextNode();
		if (parentContextNode == null) return null;

		ContextNode subjectContextNode = parentContextNode.getDeepContextNode(subject);
		if (subjectContextNode == null) return null;

		return subjectContextNode;
	}

	/**
	 * Returns the underlying predicate relation of this XDI inner root.
	 * @return The predicate relation of this XDI inner root.
	 */
	public Relation getPredicateRelation() {

		XDI3Segment predicate = XdiInnerRoot.getPredicateOfInnerRootXri(this.getContextNode().getArcXri());
		if (predicate == null) return null;

		ContextNode subjectContextNode = this.getSubjectContextNode();
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
	 * Methods for XDI inner root XRIs
	 */

	/**
	 * Returns the inner root XRI of a subject XRI and a predicate XRI.
	 * @param subject A subject XRI.
	 * @param predicate A subject XRI.
	 * @return The inner root XRI of the subject XRI and the predicate XRI.
	 */
	public static XDI3SubSegment createInnerRootArcXri(XDI3Segment subject, XDI3Segment predicate) {

		return XDI3SubSegment.create("" + XDIConstants.XS_ROOT.charAt(0) + subject.toString() + "/" + predicate.toString() + XDIConstants.XS_ROOT.charAt(1));
	}

	/**
	 * Returns the subject XRI of the inner root XRI.
	 * @param arcXri An inner root XRI.
	 * @return The subject XRI of the inner root XRI.
	 */
	public static XDI3Segment getSubjectOfInnerRootXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isClassXs()) return null;
		if (arcXri.isAttributeXs()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialSubject();
	}

	/**
	 * Returns the predicate XRI of the inner root XRI.
	 * @param arcXri An inner root XRI.
	 * @return The predicate XRI of the inner root XRI.
	 */
	public static XDI3Segment getPredicateOfInnerRootXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isClassXs()) return null;
		if (arcXri.isAttributeXs()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialPredicate();
	}

	/**
	 * Checks if a given XRI is an inner root XRI.
	 * @param arcXri An inner root XRI.
	 * @return True, if the XRI is an inner root XRI.
	 */
	public static boolean isInnerRootArcXri(XDI3SubSegment arcXri) {

		return getSubjectOfInnerRootXri(arcXri) != null && getPredicateOfInnerRootXri(arcXri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeInnerRootIterator extends NotNullIterator<XdiInnerRoot> {

		public MappingContextNodeInnerRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInnerRoot> (contextNodes) {

				@Override
				public XdiInnerRoot map(ContextNode contextNode) {

					return XdiInnerRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
