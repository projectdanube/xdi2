package xdi2.core.features.multiplicity;

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

	public static XRI3SubSegment attributeCollectionMemberArcXri() {

		return XRIUtil.randomSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG);
	}

	public static XRI3SubSegment entityCollectionMemberArcXri() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR, "" + XRI3Constants.LCS_BANG);
	}

	public static boolean isAttributeSingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isAttributeCollectionArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_STAR.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return true;
		if ((! arcXri.hasGCS()) && (! arcXri.hasLCS()) && arcXri.hasXRef()) return true;
		if (! arcXri.hasXRef()) return true;

		return false;
	}

	public static boolean isEntityCollectionArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isAttributeCollectionMemberArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isEntityCollectionMemberArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasXRIReference()) return false;
		if (! arcXri.getXRef().getXRIReference().hasPath()) return false;
		if (arcXri.getXRef().getXRIReference().getPath().getNumSegments() <= 0) return false;
		if (arcXri.getXRef().getXRIReference().getPath().getFirstSegment().getNumSubSegments() <= 0) return false;
		if (arcXri.getXRef().getXRIReference().getPath().getFirstSegment().getFirstSubSegment().hasGCS()) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getXRef().getXRIReference().getPath().getFirstSegment().getFirstSubSegment().getLCS())) return false;

		return true;
	}
}
