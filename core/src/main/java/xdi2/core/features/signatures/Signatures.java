package xdi2.core.features.signatures;

import java.security.Key;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;

public class Signatures {

	private Signatures() { }

	/**
	 * Given a graph, lists all signatures.
	 * @param graph The graph.
	 * @return An iterator over signatures.
	 */
	public static Iterator<Signature<? extends Key, ? extends Key>> getAllSignatures(Graph graph) {

		ContextNode root = graph.getRootContextNode();
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingContextNodeSignatureIterator(allContextNodes);
	}

	/**
	 * Returns an existing XDI signature under a context node.
	 * @return The existing XDI signature.
	 */
	public static Signature<? extends Key, ? extends Key> getSignature(ContextNode contextNode) {

		ContextNode signatureContextNode = contextNode.getDeepContextNode(XDIAuthenticationConstants.XRI_S_SIGNATURE);
		if (signatureContextNode == null) return null;

		XdiAttributeSingleton xdiAttributeSingleton = XdiAttributeSingleton.fromContextNode(signatureContextNode);
		if (xdiAttributeSingleton == null) return null;

		return Signature.fromXdiAttribute(xdiAttributeSingleton);
	}

	/**
	 * Returns an existing XDI signature under a context node.
	 * @return The existing XDI signature.
	 */
	public static Signature<? extends Key, ? extends Key> setSignature(ContextNode contextNode, String digestAlgorithm, int digestLength, String keyAlgorithm, int keyLength) {

		ContextNode signatureContextNode = contextNode.setDeepContextNode(XDIAuthenticationConstants.XRI_S_SIGNATURE);
		if (signatureContextNode == null) return null;

		XDI3Segment dataTypeXri = Signature.getDataTypeXri(digestAlgorithm, digestLength, keyAlgorithm, keyLength);
		DataTypes.setDataType(signatureContextNode, dataTypeXri);

		XdiAttributeSingleton xdiAttributeSingleton = XdiAttributeSingleton.fromContextNode(signatureContextNode);
		if (xdiAttributeSingleton == null) return null;

		return Signature.fromXdiAttribute(xdiAttributeSingleton);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeSignatureIterator extends NotNullIterator<Signature<? extends Key, ? extends Key>> {

		public MappingContextNodeSignatureIterator(Iterator<ContextNode> iterator) {

			super(new MappingIterator<ContextNode, Signature<? extends Key, ? extends Key>> (iterator) {

				@Override
				public Signature<? extends Key, ? extends Key> map(ContextNode contextNode) {

					XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
					if (xdiAttribute == null) return null;

					return Signature.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
