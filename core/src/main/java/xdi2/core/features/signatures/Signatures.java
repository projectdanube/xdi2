package xdi2.core.features.signatures;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

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

		XDI3Segment dataTypeXri = getDataTypeXri(digestAlgorithm, digestLength, keyAlgorithm, keyLength);
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
		parameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "0");
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

		return string;
	}

	public static String getDigestAlgorithm(XdiAttribute xdiAttribute) {

		XDI3Segment dataTypeXri = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXri == null ? null : getDigestAlgorithm(dataTypeXri);
	}

	public static String getDigestAlgorithm(XDI3Segment dataTypeXri) {

		XDI3SubSegment digestAlgorithmXri = dataTypeXri.getNumSubSegments() > 0 ? dataTypeXri.getSubSegment(0) : null;
		if (digestAlgorithmXri == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestAlgorithmXri.getCs())) return null;
		if (digestAlgorithmXri.hasXRef()) return null;
		if (! digestAlgorithmXri.hasLiteral()) return null;

		return digestAlgorithmXri.getLiteral();
	}

	public static Integer getDigestLength(XdiAttribute xdiAttribute) {

		XDI3Segment dataTypeXri = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXri == null ? null : getDigestLength(dataTypeXri);
	}

	public static Integer getDigestLength(XDI3Segment dataTypeXri) {

		XDI3SubSegment digestLengthXri = dataTypeXri.getNumSubSegments() > 1 ? dataTypeXri.getSubSegment(1) : null;
		if (digestLengthXri == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestLengthXri.getCs())) return null;
		if (digestLengthXri.hasXRef()) return null;
		if (! digestLengthXri.hasLiteral()) return null;

		return Integer.valueOf(digestLengthXri.getLiteral());
	}

	public static String getKeyAlgorithm(XdiAttribute xdiAttribute) {

		XDI3Segment dataTypeXri = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXri == null ? null : getKeyAlgorithm(dataTypeXri);
	}

	public static String getKeyAlgorithm(XDI3Segment dataTypeXri) {

		XDI3SubSegment keyAlgorithmXri = dataTypeXri.getNumSubSegments() > 2 ? dataTypeXri.getSubSegment(2) : null;
		if (keyAlgorithmXri == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyAlgorithmXri.getCs())) return null;
		if (keyAlgorithmXri.hasXRef()) return null;
		if (! keyAlgorithmXri.hasLiteral()) return null;

		return keyAlgorithmXri.getLiteral();
	}

	public static Integer getKeyLength(XdiAttribute xdiAttribute) {

		XDI3Segment dataTypeXri = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXri == null ? null : getKeyLength(dataTypeXri);
	}

	public static Integer getKeyLength(XDI3Segment dataTypeXri) {

		XDI3SubSegment keyLengthXri = dataTypeXri.getNumSubSegments() > 3 ? dataTypeXri.getSubSegment(3) : null;
		if (keyLengthXri == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyLengthXri.getCs())) return null;
		if (keyLengthXri.hasXRef()) return null;
		if (! keyLengthXri.hasLiteral()) return null;

		return Integer.valueOf(keyLengthXri.getLiteral());
	}

	public static XDI3Segment getDataTypeXri(String digestAlgorithm, int digestLength, String keyAlgorithm, int keyLength) {

		StringBuilder builder = new StringBuilder();

		builder.append(XDIConstants.CS_CLASS_RESERVED + digestAlgorithm.toLowerCase());
		builder.append(XDIConstants.CS_CLASS_RESERVED + Integer.toString(digestLength));
		builder.append(XDIConstants.CS_CLASS_RESERVED + keyAlgorithm.toLowerCase());
		builder.append(XDIConstants.CS_CLASS_RESERVED + Integer.toString(keyLength));

		return XDI3Segment.create(builder.toString());
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
