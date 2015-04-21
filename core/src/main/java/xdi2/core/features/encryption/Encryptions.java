package xdi2.core.features.encryption;

import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
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
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public class Encryptions {

	private Encryptions() { }

	/**
	 * Given a graph, lists all encryptions.
	 * @param graph The graph.
	 * @return An iterator over encryptions.
	 */
	public static Iterator<Encryption<? extends Key, ? extends Key>> getAllEncryptions(Graph graph) {

		ContextNode root = graph.getRootContextNode(true);
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingXdiAttributeEncryptionIterator(new MappingContextNodeXdiAttributeIterator(allContextNodes));
	}

	/**
	 * Creates an XDI encryption on a context node.
	 * @return The XDI encryption.
	 */
	public static Encryption<? extends Key, ? extends Key> createEncryption(ContextNode contextNode, String keyAlgorithm, int keyLength, boolean singleton) {

		XdiAttribute encryptionXdiAttribute;

		if (singleton)
			encryptionXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION), true);
		else
			encryptionXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeCollection(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION), true).setXdiMemberUnordered(null);

		XDIAddress dataTypeXDIAddress = getDataTypeXDIAddress(keyAlgorithm, keyLength);
		DataTypes.setDataType(encryptionXdiAttribute.getContextNode(), dataTypeXDIAddress);

		return Encryption.fromXdiAttribute(encryptionXdiAttribute);
	}

	/**
	 * Returns the XDI encryptions on a context node.
	 */
	public static ReadOnlyIterator<Encryption<? extends Key, ? extends Key>> getEncryptions(ContextNode contextNode) {

		List<Iterator<? extends Encryption<? extends Key, ? extends Key>>> iterators = new ArrayList<Iterator<? extends Encryption<? extends Key, ? extends Key>>> ();

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);

		// add encryption that is an XDI attribute singleton

		XdiAttributeSingleton encryptionAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION), false);
		Encryption<?, ?> encryptionSingleton = encryptionAttributeSingleton == null ? null : Encryption.fromXdiAttribute(encryptionAttributeSingleton);

		if (encryptionSingleton != null) iterators.add(new SingleItemIterator<Encryption<?, ?>> (encryptionSingleton));

		// add encryptions that are XDI attribute instances

		XdiAttributeCollection encryptionAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION), false);

		if (encryptionAttributeCollection != null) iterators.add(new MappingXdiAttributeEncryptionIterator(encryptionAttributeCollection.getXdiMembersDeref()));

		return new CompositeIterator<Encryption<? extends Key, ? extends Key>> (iterators.iterator());
	}

	/*
	 * Helper methods
	 */

	public static String getKeyAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getKeyAlgorithm(dataTypeXDIAddress);
	}

	public static String getKeyAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc keyAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 0 ? dataTypeXDIAddress.getXDIArc(0) : null;
		if (keyAlgorithmAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyAlgorithmAddress.getCs())) return null;
		if (keyAlgorithmAddress.hasXRef()) return null;
		if (! keyAlgorithmAddress.hasLiteralNode()) return null;

		return keyAlgorithmAddress.getLiteralNode();
	}

	public static Integer getKeyLength(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getKeyLength(dataTypeXDIAddress);
	}

	public static Integer getKeyLength(XDIAddress dataTypeXDIAddress) {

		XDIArc keyLengthAddress = dataTypeXDIAddress.getNumXDIArcs() > 1 ? dataTypeXDIAddress.getXDIArc(1) : null;
		if (keyLengthAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyLengthAddress.getCs())) return null;
		if (keyLengthAddress.hasXRef()) return null;
		if (! keyLengthAddress.hasLiteralNode()) return null;

		return Integer.valueOf(keyLengthAddress.getLiteralNode());
	}

	public static XDIAddress getDataTypeXDIAddress(String keyAlgorithm, int keyLength) {

		StringBuilder builder = new StringBuilder();

		builder.append(XDIConstants.CS_CLASS_RESERVED + keyAlgorithm.toLowerCase());
		builder.append(XDIConstants.CS_CLASS_RESERVED + Integer.toString(keyLength));

		return XDIAddress.create(builder.toString());
	}

	/*
	 * Helper classes
	 */

	public static class NoEncryptionsCopyStrategy extends CopyStrategy {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (xdiAttribute == null) return contextNode;

			Encryption<?, ?> encryption = Encryption.fromXdiAttribute(xdiAttribute);
			if (encryption == null) return contextNode;

			return null;
		}
	}

	public static class MappingXdiAttributeEncryptionIterator extends NotNullIterator<Encryption<? extends Key, ? extends Key>> {

		public MappingXdiAttributeEncryptionIterator(Iterator<XdiAttribute> iterator) {

			super(new MappingIterator<XdiAttribute, Encryption<? extends Key, ? extends Key>> (iterator) {

				@Override
				public Encryption<? extends Key, ? extends Key> map(XdiAttribute xdiAttribute) {

					return Encryption.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
