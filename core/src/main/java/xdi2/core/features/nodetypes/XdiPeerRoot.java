package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XDI peer root, represented as a context node.
 * 
 * @author markus
 */
public final class XdiPeerRoot extends XdiAbstractRoot {

	private static final long serialVersionUID = -4689596452249483618L;

	protected XdiPeerRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI peer root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI peer root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return 
				isPeerRootArcXri(contextNode.getArcXri()) &&
				XdiAbstractRoot.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI peer root bound to a given context node.
	 * @param contextNode The context node that is an XDI peer root.
	 * @return The XDI peer root.
	 */
	public static XdiPeerRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiPeerRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Checks if this XDI peer root is the self XDI peer root of the graph.
	 * @return True, if this is the self XDI peer root.
	 */
	public boolean isSelfPeerRoot() {

		XdiPeerRoot selfPeerRoot = this.findLocalRoot().getSelfPeerRoot();
		if (this.equals(selfPeerRoot)) return true;

		ContextNode refContextNode = Equivalence.getReferenceContextNode(this.getContextNode());
		XdiPeerRoot refPeerRoot = refContextNode == null ? null : XdiPeerRoot.fromContextNode(refContextNode);
		if (refPeerRoot != null && refPeerRoot.equals(selfPeerRoot)) return true;

		ContextNode repContextNode = Equivalence.getReplacementContextNode(this.getContextNode());
		XdiPeerRoot repPeerRoot = repContextNode == null ? null : XdiPeerRoot.fromContextNode(repContextNode);
		if (repPeerRoot != null && repPeerRoot.equals(selfPeerRoot)) return true;

		return false;
	}

	public XDI3Segment getXriOfPeerRoot() {

		return getXriOfPeerRootArcXri(this.getContextNode().getArcXri());
	}

	/*
	 * Methods for XDI peer root XRIs
	 */

	/**
	 * Returns the peer root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The peer root XRI of the XRI.
	 */
	public static XDI3SubSegment createPeerRootArcXri(XDI3Segment xri) {

		return XDI3SubSegment.create("" + XDIConstants.XS_ROOT.charAt(0) + xri + XDIConstants.XS_ROOT.charAt(1));
	}

	/**
	 * Returns the XRI of the peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return The XRI of the peer root XRI.
	 */
	public static XDI3Segment getXriOfPeerRootArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isClassXs()) return null;
		if (arcXri.isAttributeXs()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasSegment()) return null;

		return xref.getSegment();
	}

	/**
	 * Returns the IRI of the peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return The IRI of the peer root XRI.
	 */
	public static String getIriOfPeerRootArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isClassXs()) return null;
		if (arcXri.isAttributeXs()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasIri()) return null;

		return xref.getIri();
	}

	/**
	 * Returns the literal of the peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return The literal of the peer root XRI.
	 */
	public static String getLiteralOfPeerRootArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isClassXs()) return null;
		if (arcXri.isAttributeXs()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasLiteral()) return null;

		return xref.getLiteral();
	}

	/**
	 * Checks if a given XRI is a peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return True, if the XRI is a peer root XRI.
	 */
	public static boolean isPeerRootArcXri(XDI3SubSegment arcXri) {

		return getXriOfPeerRootArcXri(arcXri) != null || getIriOfPeerRootArcXri(arcXri) != null || getLiteralOfPeerRootArcXri(arcXri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodePeerRootIterator extends NotNullIterator<XdiPeerRoot> {

		public MappingContextNodePeerRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiPeerRoot> (contextNodes) {

				@Override
				public XdiPeerRoot map(ContextNode contextNode) {

					return XdiPeerRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
