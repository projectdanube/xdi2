package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

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
				isPeerRootXDIArc(contextNode.getXDIArc()) &&
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

	public XDIAddress getXDIAddressOfPeerRoot() {

		return getXDIAddressOfPeerRootXDIArc(this.getContextNode().getXDIArc());
	}

	/*
	 * Methods for XDI peer root arcs
	 */

	/**
	 * Returns the peer root arc of an address.
	 * @param address An address.
	 * @return The peer root arc of the address.
	 */
	public static XDIArc createPeerRootXDIArc(XDIAddress address) {

		return XDIArc.create("" + XDIConstants.XS_ROOT.charAt(0) + address + XDIConstants.XS_ROOT.charAt(1));
	}

	/**
	 * Returns the address of the peer root arc.
	 * @param arc A peer root arc.
	 * @return The address of the peer root arc.
	 */
	public static XDIAddress getXDIAddressOfPeerRootXDIArc(XDIArc arc) {

		if (arc == null) return null;

		if (arc.hasCs()) return null;
		if (arc.isClassXs()) return null;
		if (arc.isAttributeXs()) return null;
		if (! arc.hasXRef()) return null;

		XDIXRef xref = arc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasXDIAddress()) return null;

		return xref.getXDIAddress();
	}

	/**
	 * Returns the IRI of the peer root arc.
	 * @param arc A peer root arc.
	 * @return The IRI of the peer root arc.
	 */
	public static String getIriOfPeerRootXDIArc(XDIArc arc) {

		if (arc == null) return null;

		if (arc.hasCs()) return null;
		if (arc.isClassXs()) return null;
		if (arc.isAttributeXs()) return null;
		if (! arc.hasXRef()) return null;

		XDIXRef xref = arc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasIri()) return null;

		return xref.getIri();
	}

	/**
	 * Returns the literal of the peer root arc.
	 * @param arc A peer root arc.
	 * @return The literal of the peer root arc.
	 */
	public static String getLiteralOfPeerRootXDIArc(XDIArc arc) {

		if (arc == null) return null;

		if (arc.hasCs()) return null;
		if (arc.isClassXs()) return null;
		if (arc.isAttributeXs()) return null;
		if (! arc.hasXRef()) return null;

		XDIXRef xref = arc.getXRef();
		if (! XDIConstants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasLiteral()) return null;

		return xref.getLiteral();
	}

	/**
	 * Checks if a given arc is a peer root arc.
	 * @param arc A peer root arc.
	 * @return True, if the arc is a peer root arc.
	 */
	public static boolean isPeerRootXDIArc(XDIArc arc) {

		return getXDIAddressOfPeerRootXDIArc(arc) != null || getIriOfPeerRootXDIArc(arc) != null || getLiteralOfPeerRootXDIArc(arc) != null;
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
