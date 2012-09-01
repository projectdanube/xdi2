package xdi2.core.features.multiplicity;

import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * Multiplicity supports constructs to express XDI collections, entities and attributes.
 * All these constructs are context nodes whose arc XRI follow certain rules.
 * 
 * Examples:
 * 
 * Collection: +tel, +passport
 * Entity Singleton: $(+passport)
 * Attribute Singleton: $!(+tel)
 * Entity Member: $(!1)
 * Attribute Member: $!(!1)
 * 
 * @author markus
 */
public class Multiplicity {

	private Multiplicity() { }

	/*
	 * Methods for building arc XRIs.
	 */
	
	public static XRI3SubSegment collectionArcXri(XRI3SubSegment arcXri) {

		return arcXri;
	}
	
	public static XRI3SubSegment entitySingletonArcXri(XRI3SubSegment arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XRI3SubSegment attributeSingletonArcXri(XRI3SubSegment arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XRI3SubSegment entityMemberArcXri() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR, "" + XRI3Constants.LCS_BANG);
	}

	public static XRI3SubSegment attributeMemberArcXri() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG, "" + XRI3Constants.LCS_BANG);
	}

	/*
	 * Methods for checking arc XRIs.
	 */
	
	public static boolean isCollectionArcXri(XRI3SubSegment arcXri) {

		if (isEntitySingletonArcXri(arcXri)) return false;
		if (isAttributeSingletonArcXri(arcXri)) return false;
		if (isEntityMemberArcXri(arcXri)) return false;
		if (isAttributeMemberArcXri(arcXri)) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isAttributeSingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isEntityMemberArcXri(XRI3SubSegment arcXri) {

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

	public static boolean isAttributeMemberArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
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
