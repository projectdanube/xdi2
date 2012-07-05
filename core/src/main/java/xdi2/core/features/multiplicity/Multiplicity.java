package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class Multiplicity {

	private Multiplicity() { }

	/*
	 * Methods for arc XRIs.
	 */

	public static XRI3SubSegment attributeSingletonArcXri(String arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XRI3SubSegment attributeCollectionArcXri(String arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_STAR + "(" + arcXri + ")");
	}

	public static XRI3SubSegment entitySingletonArcXri(String arcXri) {

		return new XRI3SubSegment("" + arcXri + "");
	}

	public static XRI3SubSegment entityCollectionArcXri(String arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XRI3SubSegment elementArcXri() {

		return XRIUtil.randomHEXSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG);
	}

	public static boolean isAttributeSingletonArcXri(XRI3SubSegment arcXri) {

		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isAttributeCollectionArcXri(XRI3SubSegment arcXri) {

		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_STAR.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isEntityCollectionArcXri(XRI3SubSegment arcXri) {

		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isElementArcXri(XRI3SubSegment arcXri) {

		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (arcXri.hasXRef()) return false;

		return true;
	}

	/*
	 * Methods for context nodes.
	 */

	public static boolean isAttributeSingleton(ContextNode contextNode) {

		return isAttributeSingletonArcXri(contextNode.getArcXri());
	}

	public static boolean isAttributeCollection(ContextNode contextNode) {

		return isAttributeCollectionArcXri(contextNode.getArcXri());
	}

	public static boolean isEntitySingleton(ContextNode contextNode) {

		return isEntitySingletonArcXri(contextNode.getArcXri());
	}

	public static boolean isEntityCollection(ContextNode contextNode) {

		return isEntityCollectionArcXri(contextNode.getArcXri());
	}

	/*
	 * Methods for elements of a collection.
	 */

	public static ContextNode createCollectionElement(ContextNode contextNode) {

		return contextNode.createContextNode(XRIUtil.randomHEXSubSegment("$!"));
	}
}
