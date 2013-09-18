package xdi2.core.features.signatures;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public class Signatures {

	private Signatures() { }

	/**
	 * Given a graph, lists all signatures.
	 * @param graph The graph.
	 * @return An iterator over signatures.
	 */
	public static Iterator<Signature> getAllSignatures(Graph graph) {

		ContextNode root = graph.getRootContextNode();
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingContextNodeSignatureIterator(allContextNodes);
	}

	/**
	 * Returns an existing XDI signature under a context node, or creates a new one.
	 * @param create Whether to create an XDI signature if it does not exist.
	 * @return The existing or newly created XDI signature.
	 */
	public static Signature getSignature(ContextNode contextNode, boolean create) {

		ContextNode signatureContextNode = create ? contextNode.setDeepContextNode(XDIAuthenticationConstants.XRI_S_SIGNATURE) : contextNode.getDeepContextNode(XDIAuthenticationConstants.XRI_S_SIGNATURE);
		if (signatureContextNode == null) return null;

		XdiAttributeSingleton xdiAttributeSingleton = XdiAttributeSingleton.fromContextNode(signatureContextNode);
		if (xdiAttributeSingleton == null) return null;

		return Signature.fromXdiAttribute(xdiAttributeSingleton);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeSignatureIterator extends NotNullIterator<Signature> {

		public MappingContextNodeSignatureIterator(Iterator<ContextNode> iterator) {

			super(new MappingIterator<ContextNode, Signature> (iterator) {

				@Override
				public Signature map(ContextNode contextNode) {

					XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
					if (xdiAttribute == null) return null;

					return Signature.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
