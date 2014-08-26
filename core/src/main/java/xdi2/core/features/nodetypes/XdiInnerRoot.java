package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
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

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() != null && ! XdiAbstractRoot.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI inner root bound to a given context node.
	 * @param contextNode The context node that is an XDI inner root.
	 * @return The XDI inner root.
	 */
	public static XdiInnerRoot fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

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
	 * Returns the subject address of this XDI inner root.
	 * @return The subject address.
	 */
	public XDIAddress getSubjectOfInnerRoot() {

		return getSubjectOfInnerRootXDIArc(this.getContextNode().getXDIArc());
	}

	/**
	 * Returns the predicate address of this XDI inner root.
	 * @return The predicate address.
	 */
	public XDIAddress getPredicateOfInnerRoot() {

		return getPredicateOfInnerRootXDIArc(this.getContextNode().getXDIArc());
	}

	/*
	 * Methods for XDI inner root arcss
	 */

	/**
	 * Returns the inner root arc of a subject address and a predicate address.
	 * @param subject A subject address.
	 * @param predicate A subject address.
	 * @return The inner root arc of the subject address and the predicate address.
	 */
	public static XDIArc createInnerRootXDIArc(XDIAddress subject, XDIAddress predicate) {

		if (subject.getNumXDIArcs() > 0 && XdiAbstractRoot.isValidXDIArc(subject.getFirstXDIArc())) throw new Xdi2GraphException("Cannot create an inner root XDI arc for subject " + subject + " and predicate " + predicate);

		return XDIArc.fromComponents(
				null, 
				false, 
				false, 
				null, 
				XDIXRef.fromComponents(
						XDIConstants.XS_ROOT, 
						null, 
						subject, 
						predicate, 
						null, 
						null));
	}

	/**
	 * Returns the subject context node of the XDI inner root context node.
	 * @param contextNode An XDI inner root context node.
	 * @return The subject context node of the inner root context node.
	 */
	public static ContextNode getSubjectContextNode(ContextNode contextNode) {

		XDIAddress subject = XdiInnerRoot.getSubjectOfInnerRootXDIArc(contextNode.getXDIArc());
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

		XDIAddress predicate = XdiInnerRoot.getPredicateOfInnerRootXDIArc(contextNode.getXDIArc());
		if (predicate == null) return null;

		ContextNode subjectContextNode = getSubjectContextNode(contextNode);
		if (subjectContextNode == null) return null;

		Relation predicateRelation = subjectContextNode.getRelation(predicate, contextNode.getXDIAddress());
		if (predicateRelation == null) return null;

		return predicateRelation;
	}

	/**
	 * Returns the subject address of the inner root arcc.
	 * @param arc An inner root arc.
	 * @return The subject address of the inner root arc.
	 */
	public static XDIAddress getSubjectOfInnerRootXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) return null;

		if (XDIarc.hasCs()) return null;
		if (XDIarc.isClassXs()) return null;
		if (XDIarc.isAttributeXs()) return null;
		if (! XDIarc.hasXRef()) return null;

		XDIXRef xref = XDIarc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialSubject();
	}

	/**
	 * Returns the predicate address of the inner root arc.
	 * @param arc An inner root arc.
	 * @return The predicate address of the inner root arc.
	 */
	public static XDIAddress getPredicateOfInnerRootXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) return null;

		if (XDIarc.hasCs()) return null;
		if (XDIarc.isClassXs()) return null;
		if (XDIarc.isAttributeXs()) return null;
		if (! XDIarc.hasXRef()) return null;

		XDIXRef xref = XDIarc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasPartialSubjectAndPredicate()) return null;

		return xref.getPartialPredicate();
	}

	/**
	 * Checks if a given arc is an inner root arc.
	 * @param arc An inner root arc.
	 * @return True, if the arc is an inner root arc.
	 */
	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (getSubjectOfInnerRootXDIArc(XDIarc) == null) return false;
		if (getPredicateOfInnerRootXDIArc(XDIarc) == null) return false;

		return true;
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
