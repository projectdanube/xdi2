package xdi2.core.features.signatures;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.nodetypes.XdiAbstractAttribute.MappingContextNodeXdiAttributeIterator;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.signatures.Signature.NoSignaturesCopyStrategy;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public class Signatures {

	private static Logger log = LoggerFactory.getLogger(Signatures.class.getName());

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
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE), true);
		else
			signatureXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeCollection(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE), true).setXdiMemberUnordered(null);

		XDIAddress dataTypeXDIAddress = getDataTypeXDIAddress(digestAlgorithm, digestLength, keyAlgorithm, keyLength);
		DataTypes.setDataType(signatureXdiAttribute.getContextNode(), dataTypeXDIAddress);

		return Signature.fromXdiAttribute(signatureXdiAttribute);
	}

	/**
	 * Returns the XDI signatures on a context node.
	 */
	public static ReadOnlyIterator<Signature<? extends Key, ? extends Key>> getSignatures(ContextNode contextNode) {

		List<Iterator<? extends Signature<? extends Key, ? extends Key>>> iterators = new ArrayList<Iterator<? extends Signature<? extends Key, ? extends Key>>> ();

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);

		// add signature that is an XDI attribute singleton

		XdiAttributeSingleton signatureAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE), false);

		if (signatureAttributeSingleton != null) iterators.add(new SingleItemIterator<Signature<?, ?>> (Signature.fromXdiAttribute(signatureAttributeSingleton)));

		// add signatures that are XDI attribute instances

		XdiAttributeCollection signatureAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE), false);

		if (signatureAttributeCollection != null) iterators.add(new MappingXdiAttributeSignatureIterator(signatureAttributeCollection.getXdiMembersDeref()));

		return new CompositeIterator<Signature<? extends Key, ? extends Key>> (iterators.iterator());
	}

	/*
	 * Helper methods
	 */

	/**
	 * Returns the normalized serialization string of a context node, to be used
	 * for creating and validating signatures.
	 */
	public static String getNormalizedSerialization(ContextNode contextNode) {

		Graph graph;

		graph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, graph, new NoSignaturesCopyStrategy());

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "0");

		XDIJSONWriter writer = new XDIJSONWriter(parameters);
		StringWriter buffer = new StringWriter();
		String string;

		try {

			writer.write(graph, buffer);
			string = buffer.toString();
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot serialize graph: " + ex.getMessage(), ex);
		} finally {

			try { buffer.close(); } catch (Exception ex) { }

			graph.close();
		}

		if (log.isDebugEnabled()) log.debug("Normalized context node " + contextNode.getXDIAddress() + ": " + string);

		return string;
	}

	public static String getDigestAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getDigestAlgorithm(dataTypeXDIAddress);
	}

	public static String getDigestAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc digestAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 0 ? dataTypeXDIAddress.getXDIArc(0) : null;
		if (digestAlgorithmAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestAlgorithmAddress.getCs())) return null;
		if (digestAlgorithmAddress.hasXRef()) return null;
		if (! digestAlgorithmAddress.hasLiteral()) return null;

		return digestAlgorithmAddress.getLiteral();
	}

	public static Integer getDigestLength(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getDigestLength(dataTypeXDIAddress);
	}

	public static Integer getDigestLength(XDIAddress dataTypeXDIAddress) {

		XDIArc digestLengthAddress = dataTypeXDIAddress.getNumXDIArcs() > 1 ? dataTypeXDIAddress.getXDIArc(1) : null;
		if (digestLengthAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestLengthAddress.getCs())) return null;
		if (digestLengthAddress.hasXRef()) return null;
		if (! digestLengthAddress.hasLiteral()) return null;

		return Integer.valueOf(digestLengthAddress.getLiteral());
	}

	public static String getKeyAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getKeyAlgorithm(dataTypeXDIAddress);
	}

	public static String getKeyAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc keyAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 2 ? dataTypeXDIAddress.getXDIArc(2) : null;
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

		XDIArc keyLengthAddress = dataTypeXDIAddress.getNumXDIArcs() > 3 ? dataTypeXDIAddress.getXDIArc(3) : null;
		if (keyLengthAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyLengthAddress.getCs())) return null;
		if (keyLengthAddress.hasXRef()) return null;
		if (! keyLengthAddress.hasLiteral()) return null;

		return Integer.valueOf(keyLengthAddress.getLiteral());
	}

	public static XDIAddress getDataTypeXDIAddress(String digestAlgorithm, int digestLength, String keyAlgorithm, int keyLength) {

		StringBuilder builder = new StringBuilder();

		builder.append(XDIConstants.CS_CLASS_RESERVED + digestAlgorithm.toLowerCase());
		builder.append(XDIConstants.CS_CLASS_RESERVED + Integer.toString(digestLength));
		builder.append(XDIConstants.CS_CLASS_RESERVED + keyAlgorithm.toLowerCase());
		builder.append(XDIConstants.CS_CLASS_RESERVED + Integer.toString(keyLength));

		return XDIAddress.create(builder.toString());
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
