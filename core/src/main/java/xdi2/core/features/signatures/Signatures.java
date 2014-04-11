package xdi2.core.features.signatures;

import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.nodetypes.XdiAbstractAttribute.MappingContextNodeXdiAttributeIterator;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Segment;

public class Signatures {

	private Signatures() { }

	/**
	 * Given a graph, lists all signatures.
	 * @param graph The graph.
	 * @return An iterator over signatures.
	 */
	public static Iterator<Signature<? extends Key, ? extends Key>> getAllSignatures(Graph graph) {

		ContextNode root = graph.getRootContextNode(true);
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingXdiAttributeSignatureIterator(new MappingContextNodeXdiAttributeIterator(allContextNodes));
	}

	/**
	 * Creates an XDI signature on a context node.
	 * @return The XDI signature.
	 */
	public static Signature<? extends Key, ? extends Key> createSignature(ContextNode contextNode, String digestAlgorithm, int digestLength, String keyAlgorithm, int keyLength, boolean singleton) {

		XdiAttribute signatureXdiAttribute;
		
		if (singleton)
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XdiAttributeSingleton.createArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE), true);
		else
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeCollection(XdiAttributeCollection.createArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE), true).setXdiMemberUnordered(null);

		XDI3Segment dataTypeXri = Signature.getDataTypeXri(digestAlgorithm, digestLength, keyAlgorithm, keyLength);
		DataTypes.setDataType(signatureXdiAttribute.getContextNode(), dataTypeXri);

		return Signature.fromXdiAttribute(signatureXdiAttribute);
	}

	/**
	 * Returns the XDI signatures on a context node.
	 */
	public static ReadOnlyIterator<Signature<? extends Key, ? extends Key>> getSignatures(ContextNode contextNode) {

		List<Iterator<? extends Signature<? extends Key, ? extends Key>>> iterators = new ArrayList<Iterator<? extends Signature<? extends Key, ? extends Key>>> ();

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);
		
		// add signature that is an XDI attribute singleton

		XdiAttributeSingleton signatureAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE), false);

		if (signatureAttributeSingleton != null) iterators.add(new SingleItemIterator<Signature<?, ?>> (Signature.fromXdiAttribute(signatureAttributeSingleton)));

		// add signatures that are XDI attribute instances

		XdiAttributeCollection signatureAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE), false);

		if (signatureAttributeCollection != null) iterators.add(new MappingXdiAttributeSignatureIterator(signatureAttributeCollection.getXdiMembersDeref()));

		return new CompositeIterator<Signature<? extends Key, ? extends Key>> (iterators.iterator());
	}

	/*
	 * Helper classes
	 */

	public static class MappingXdiAttributeSignatureIterator extends NotNullIterator<Signature<? extends Key, ? extends Key>> {

		public MappingXdiAttributeSignatureIterator(Iterator<XdiAttribute> iterator) {

			super(new MappingIterator<XdiAttribute, Signature<? extends Key, ? extends Key>> (iterator) {

				@Override
				public Signature<? extends Key, ? extends Key> map(XdiAttribute xdiAttribute) {

					return Signature.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
