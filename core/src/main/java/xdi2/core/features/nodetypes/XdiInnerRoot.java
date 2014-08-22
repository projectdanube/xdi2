package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

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
				isInnerRootarc(contextNode.getArc()) &&
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

		return getSubjectContextNode(this.getContextNode());
	}

	/**
	 * Returns the underlying predicate relation of this XDI inner root.
	 * @return The predicate relation of this XDI inner root.
	 */
	public Relation getPredicateRelation() {

		return getPredicateRelation(this.getContextNode());
	}

	/**
	 * Returns the subject XRI of this XDI inner root.
	 * @return The subject XRI.
	 */
	public XDIAddress getSubjectOfInnerRoot() {

		return getSubjectOfInnerRootXri(this.getContextNode().getArc());
	}

	/**
	 * Returns the predicate XRI of this XDI inner root.
	 * @return The predicate XRI.
	 */
	public XDIAddress getPredicateOfInnerRoot() {

		return getPredicateOfInnerRootXri(this.getContextNode().getArc());
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
	public static XDIArc createInnerRootarc(XDIAddress subject, XDIAddress predicate) {

		return XDIArc.fromComponents(null, false, false, null, 
				XDIXRef.fromComponents(XDIConstants.XS_ROOT, null, subject, predicate, null, null));
	}

	/**
	 * Returns the subject context node of the XDI inner root context node.
	 * @param contextNode An XDI inner root context node.
	 * @return The subject context node of the inner root context node.
	 */
	public static ContextNode getSubjectContextNode(ContextNode contextNode) {

		XDIAddress subject = XdiInnerRoot.getSubjectOfInnerRootXri(contextNode.getArc());
		if (subject == null) return null;

		ContextNode parentContextNode = contextNode.getContextNode();
		if (parentContextNode == null) return null;

		ContextNode subjectContextNode = parentContextNode.getDeepContextNode(subject, false);
		if (subjectContextNode == null) return null;

		return subjectContextNode;
	}

	/**
	 * Returns the predicate relation of the XDI inner root context node.
	 * @param contextNode An XDI inner root context node.
	 * @return The predicate relation of the inner root context node.
	 */
	public static Relation getPredicateRelation(ContextNode contextNode) {

		XDIAddress predicate = XdiInnerRoot.getPredicateOfInnerRootXri(contextNode.getArc());
		if (predicate == null) return null;

		ContextNode subjectContextNode = getSubjectContextNode(contextNode);
		if (subjectContextNode == null) return null;

		Relation predicateRelation = subjectContextNode.getRelation(predicate, contextNode.getAddress());
		if (predicateRelation == null) return null;

		return predicateRelation;
	}

	/**
	 * Returns the subject XRI of the inner root XRI.
	 * @param arc An inner root XRI.
	 * @return The subject XRI of the inner root XRI.
	 */
	public static XDIAddress getSubjectOfInnerRootXri(XDIArc arc) {

		if (arc == null) return null;

		if (arc.hasCs()) return null;
		if (arc.isClassXs()) return null;
		if (arc.isAttributeXs()) return null;
		if (! arc.hasXRef()) return null;

		XDIXRef xref = arc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialSubject();
	}

	/**
	 * Returns the predicate XRI of the inner root XRI.
	 * @param arc An inner root XRI.
	 * @return The predicate XRI of the inner root XRI.
	 */
	public static XDIAddress getPredicateOfInnerRootXri(XDIArc arc) {

		if (arc == null) return null;

		if (arc.hasCs()) return null;
		if (arc.isClassXs()) return null;
		if (arc.isAttributeXs()) return null;
		if (! arc.hasXRef()) return null;

		XDIXRef xref = arc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialPredicate();
	}

	/**
	 * Checks if a given XRI is an inner root XRI.
	 * @param arc An inner root XRI.
	 * @return True, if the XRI is an inner root XRI.
	 */
	public static boolean isInnerRootarc(XDIArc arc) {

		return getSubjectOfInnerRootXri(arc) != null && getPredicateOfInnerRootXri(arc) != null;
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
