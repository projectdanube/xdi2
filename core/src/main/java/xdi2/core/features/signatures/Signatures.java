package xdi2.core.features.signatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractAttribute.MappingContextNodeXdiAttributeIterator;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil.AbstractCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public class Signatures {

	private Signatures() { }

	/**
	 * Given a graph, lists all signatures.
	 * @param graph The graph.
	 * @return An iterator over signatures.
	 */
	public static Iterator<Signature> getAllSignatures(Graph graph) {

		ContextNode root = graph.getRootContextNode(true);
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingXdiAttributeSignatureIterator(new MappingContextNodeXdiAttributeIterator(allContextNodes));
	}

	/**
	 * Creates an XDI signature on a context node.
	 * @return The XDI signature.
	 */
	public static Signature createSignature(ContextNode contextNode, String digestAlgorithm, Integer digestVersion, String keyAlgorithm, Integer keyLength, boolean singleton) {

		XdiAttribute signatureXdiAttribute;

		if (singleton)
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDISecurityConstants.XDI_ARC_SIGNATURE), true);
		else
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeCollection(XdiAttributeCollection.createXDIArc(XDISecurityConstants.XDI_ARC_SIGNATURE), true).setXdiInstanceUnordered(true, false);

		XDIAddress dataTypeXDIAddress = createDataTypeXDIAddress(digestAlgorithm, digestVersion, keyAlgorithm, keyLength);
		if (dataTypeXDIAddress != null) DataTypes.setDataType(signatureXdiAttribute.getContextNode(), dataTypeXDIAddress);

		return Signature.fromXdiAttribute(signatureXdiAttribute);
	}

	/**
	 * Returns the XDI signatures on a context node.
	 */
	public static ReadOnlyIterator<Signature> getSignatures(ContextNode contextNode) {

		List<Iterator<? extends Signature>> iterators = new ArrayList<Iterator<? extends Signature>> ();

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);

		// add signature that is an XDI attribute singleton

		XdiAttributeSingleton signatureAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDISecurityConstants.XDI_ARC_SIGNATURE), false);
		Signature signatureSingleton = signatureAttributeSingleton == null ? null : Signature.fromXdiAttribute(signatureAttributeSingleton);

		if (signatureSingleton != null) iterators.add(new SingleItemIterator<Signature> (signatureSingleton));

		// add signatures that are XDI attribute instances

		XdiAttributeCollection signatureAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createXDIArc(XDISecurityConstants.XDI_ARC_SIGNATURE), false);

		if (signatureAttributeCollection != null) iterators.add(new MappingXdiAttributeSignatureIterator(signatureAttributeCollection.getXdiInstancesDeref()));

		return new CompositeIterator<Signature> (iterators.iterator());
	}

	/*
	 * Helper methods
	 */

	public static String getDigestAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getDigestAlgorithm(dataTypeXDIAddress);
	}

	public static String getDigestAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc digestAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 3 ? dataTypeXDIAddress.getXDIArc(0) : null;
		if (digestAlgorithmAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestAlgorithmAddress.getCs())) return null;
		if (digestAlgorithmAddress.hasXRef()) return null;
		if (! digestAlgorithmAddress.hasLiteral()) return null;

		return digestAlgorithmAddress.getLiteral();
	}

	public static Integer getDigestVersion(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getDigestVersion(dataTypeXDIAddress);
	}

	public static Integer getDigestVersion(XDIAddress dataTypeXDIAddress) {

		XDIArc digestVersionAddress = dataTypeXDIAddress.getNumXDIArcs() > 3 ? dataTypeXDIAddress.getXDIArc(1) : null;
		if (digestVersionAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestVersionAddress.getCs())) return null;
		if (digestVersionAddress.hasXRef()) return null;
		if (! digestVersionAddress.hasLiteral()) return null;

		return Integer.valueOf(digestVersionAddress.getLiteral());
	}

	public static String getKeyAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getKeyAlgorithm(dataTypeXDIAddress);
	}

	public static String getKeyAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc keyAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 2 ? dataTypeXDIAddress.getXDIArc(2) : dataTypeXDIAddress.getXDIArc(0);
		if (keyAlgorithmAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyAlgorithmAddress.getCs())) return null;
		if (keyAlgorithmAddress.hasXRef()) return null;
		if (! keyAlgorithmAddress.hasLiteral()) return null;

		return keyAlgorithmAddress.getLiteral();
	}

	public static Integer getKeyLength(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getKeyLength(dataTypeXDIAddress);
	}

	public static Integer getKeyLength(XDIAddress dataTypeXDIAddress) {

		XDIArc keyLengthAddress = dataTypeXDIAddress.getNumXDIArcs() > 3 ? dataTypeXDIAddress.getXDIArc(3) : dataTypeXDIAddress.getXDIArc(1);
		if (keyLengthAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyLengthAddress.getCs())) return null;
		if (keyLengthAddress.hasXRef()) return null;
		if (! keyLengthAddress.hasLiteral()) return null;

		return Integer.valueOf(keyLengthAddress.getLiteral());
	}

	public static XDIAddress createDataTypeXDIAddress(String digestAlgorithm, Integer digestVersion, String keyAlgorithm, Integer keyLength) {

		StringBuilder builder = new StringBuilder();

		if (digestAlgorithm != null) builder.append(XDIConstants.CS_CLASS_RESERVED + digestAlgorithm.toLowerCase());
		if (digestVersion != null) builder.append(XDIConstants.CS_CLASS_RESERVED + digestVersion.toString());
		if (keyAlgorithm != null) builder.append(XDIConstants.CS_CLASS_RESERVED + keyAlgorithm.toLowerCase());
		if (keyLength != null) builder.append(XDIConstants.CS_CLASS_RESERVED + keyLength.toString());

		if (builder.length() == 0) return null;

		return XDIAddress.create(builder.toString());
	}

	/*
	 * Helper classes
	 */

	public static class NoSignaturesCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			if (contextNode == null) return null;

			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (xdiAttribute == null) return contextNode;

			Signature signature = Signature.fromXdiAttribute(xdiAttribute);
			if (signature == null) return contextNode;

			return null;
		}
	}

	public static class MappingXdiAttributeSignatureIterator extends NotNullIterator<Signature> {

		public MappingXdiAttributeSignatureIterator(Iterator<XdiAttribute> iterator) {

			super(new MappingIterator<XdiAttribute, Signature> (iterator) {

				@Override
				public Signature map(XdiAttribute xdiAttribute) {

					return Signature.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
