package xdi2.core.features.digests;

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

public class Digests {

	private Digests() { }

	/**
	 * Given a graph, lists all digests.
	 * @param graph The graph.
	 * @return An iterator over digests.
	 */
	public static Iterator<Digest> getAllDigests(Graph graph) {

		ContextNode root = graph.getRootContextNode(true);
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingXdiAttributeDigestIterator(new MappingContextNodeXdiAttributeIterator(allContextNodes));
	}

	/**
	 * Creates an XDI digest on a context node.
	 * @return The XDI digest.
	 */
	public static Digest createDigest(ContextNode contextNode, String digestAlgorithm, Integer digestVersion, boolean singleton) {

		XdiAttribute digestXdiAttribute;

		if (singleton)
			digestXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDISecurityConstants.XDI_ARC_DIGEST), true);
		else
			digestXdiAttribute = XdiAbstractContext.fromContextNode(contextNode).getXdiAttributeCollection(XdiAttributeCollection.createXDIArc(XDISecurityConstants.XDI_ARC_DIGEST), true).setXdiInstanceUnordered(true, false);

		XDIAddress dataTypeXDIAddress = createDataTypeXDIAddress(digestAlgorithm, digestVersion);
		if (dataTypeXDIAddress != null) DataTypes.setDataType(digestXdiAttribute.getContextNode(), dataTypeXDIAddress);

		return Digest.fromXdiAttribute(digestXdiAttribute);
	}

	/**
	 * Returns the XDI digests on a context node.
	 */
	public static ReadOnlyIterator<Digest> getDigests(ContextNode contextNode) {

		List<Iterator<? extends Digest>> iterators = new ArrayList<Iterator<? extends Digest>> ();

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);

		// add digest that is an XDI attribute singleton

		XdiAttributeSingleton digestAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDISecurityConstants.XDI_ARC_DIGEST), false);
		Digest digestSingleton = digestAttributeSingleton == null ? null : Digest.fromXdiAttribute(digestAttributeSingleton);

		if (digestSingleton != null) iterators.add(new SingleItemIterator<Digest> (digestSingleton));

		// add digests that are XDI attribute instances

		XdiAttributeCollection digestAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createXDIArc(XDISecurityConstants.XDI_ARC_DIGEST), false);

		if (digestAttributeCollection != null) iterators.add(new MappingXdiAttributeDigestIterator(digestAttributeCollection.getXdiInstancesDeref()));

		return new CompositeIterator<Digest> (iterators.iterator());
	}

	/*
	 * Helper methods
	 */

	public static String getDigestAlgorithm(XdiAttribute xdiAttribute) {

		XDIAddress dataTypeXDIAddress = DataTypes.getDataType(xdiAttribute.getContextNode());

		return dataTypeXDIAddress == null ? null : getDigestAlgorithm(dataTypeXDIAddress);
	}

	public static String getDigestAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc digestAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 1 ? dataTypeXDIAddress.getXDIArc(0) : null;
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

		XDIArc digestVersionAddress = dataTypeXDIAddress.getNumXDIArcs() > 1 ? dataTypeXDIAddress.getXDIArc(1) : null;
		if (digestVersionAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(digestVersionAddress.getCs())) return null;
		if (digestVersionAddress.hasXRef()) return null;
		if (! digestVersionAddress.hasLiteral()) return null;

		return Integer.valueOf(digestVersionAddress.getLiteral());
	}

	public static XDIAddress createDataTypeXDIAddress(String digestAlgorithm, Integer digestVersion) {

		StringBuilder builder = new StringBuilder();

		if (digestAlgorithm != null) builder.append(XDIConstants.CS_CLASS_RESERVED + digestAlgorithm.toLowerCase());
		if (digestVersion != null) builder.append(XDIConstants.CS_CLASS_RESERVED + digestVersion.toString());

		if (builder.length() == 0) return null;

		return XDIAddress.create(builder.toString());
	}

	/*
	 * Helper classes
	 */

	public static class NoDigestsCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			if (contextNode == null) return null;
			
			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (xdiAttribute == null) return contextNode;

			Digest digest = Digest.fromXdiAttribute(xdiAttribute);
			if (digest == null) return contextNode;

			return null;
		}
	}

	public static class MappingXdiAttributeDigestIterator extends NotNullIterator<Digest> {

		public MappingXdiAttributeDigestIterator(Iterator<XdiAttribute> iterator) {

			super(new MappingIterator<XdiAttribute, Digest> (iterator) {

				@Override
				public Digest map(XdiAttribute xdiAttribute) {

					return Digest.fromXdiAttribute(xdiAttribute);
				}
			});
		}
	}
}
