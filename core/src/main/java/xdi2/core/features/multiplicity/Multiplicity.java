package xdi2.core.features.multiplicity;

import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.parser.ParserException;

/**
 * Multiplicity supports constructs to express XDI collections, entities and attributes.
 * All these constructs are context nodes whose arc XRI follow certain rules.
 * 
 * Examples:
 * 
 * Collection: $(+tel), $(+passport)
 * Entity Singleton: =!1111, +passport
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

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XRI3SubSegment entitySingletonArcXri(XRI3SubSegment arcXri) {

		return arcXri;
	}

	public static XRI3SubSegment entityMemberArcXri(XRI3SubSegment arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XRI3SubSegment entityMemberArcXriRandom() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR, "" + XRI3Constants.LCS_BANG);
	}

	public static XRI3SubSegment attributeSingletonArcXri(XRI3SubSegment arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XRI3SubSegment attributeMemberArcXri(XRI3SubSegment arcXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XRI3SubSegment attributeMemberArcXriRandom() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG, "" + XRI3Constants.LCS_BANG);
	}

	public static XRI3SubSegment baseArcXri(XRI3SubSegment arcXri) {

		try {

			if (isCollectionArcXri(arcXri)) {

				return new XRI3SubSegment("" + arcXri.getXRef().getXRIReference().toString());
			}

			if (isEntitySingletonArcXri(arcXri)) {

				return arcXri;
			}

			if (isAttributeSingletonArcXri(arcXri)) {

				return new XRI3SubSegment("" + arcXri.getXRef().getXRIReference().toString());
			}

			if (isEntityMemberArcXri(arcXri)) {

				return new XRI3SubSegment("" + arcXri.getXRef().getXRIReference().toString());
			}

			if (isAttributeMemberArcXri(arcXri)) {

				return new XRI3SubSegment("" + arcXri.getXRef().getXRIReference().toString());
			}
		} catch (ParserException ex) {

			return null;
		}

		throw new IllegalArgumentException("Invalid multiplicity subsegment: " + arcXri);
	}

	/*
	 * Methods for checking arc XRIs.
	 */

	public static boolean isCollectionArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasXRIReference()) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XRI3SubSegment arcXri) {

		if (isCollectionArcXri(arcXri)) return false;
		if (isAttributeSingletonArcXri(arcXri)) return false;
		if (isEntityMemberArcXri(arcXri)) return false;
		if (isAttributeMemberArcXri(arcXri)) return false;

		return true;
	}

	public static boolean isAttributeSingletonArcXri(XRI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasXRIReference()) return false;

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
