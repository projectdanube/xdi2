package xdi2.core.features.multiplicity;

import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XDI3SubSegment;
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

	public static XDI3SubSegment collectionArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XDI3SubSegment collectionArcXriRandom() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR, "");
	}

	public static XDI3SubSegment entitySingletonArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	public static XDI3SubSegment entityMemberArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XDI3SubSegment entityMemberArcXriRandom() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR, "" + XRI3Constants.LCS_BANG);
	}

	public static XDI3SubSegment attributeSingletonArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XDI3SubSegment attributeMemberArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XDI3SubSegment attributeMemberArcXriRandom() {

		return XRIUtil.randomXRefSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG, "" + XRI3Constants.LCS_BANG);
	}

	public static XDI3SubSegment baseArcXri(XDI3SubSegment arcXri) {

		try {

			if (isCollectionArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isEntitySingletonArcXri(arcXri)) {

				return arcXri;
			}

			if (isAttributeSingletonArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isEntityMemberArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isAttributeMemberArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}
		} catch (ParserException ex) {

			return null;
		}

		throw new IllegalArgumentException("Invalid multiplicity subsegment: " + arcXri);
	}

	/*
	 * Methods for checking arc XRIs.
	 */

	public static boolean isCollectionArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XDI3SubSegment arcXri) {

		if (isCollectionArcXri(arcXri)) return false;
		if (isAttributeSingletonArcXri(arcXri)) return false;
		if (isEntityMemberArcXri(arcXri)) return false;
		if (isAttributeMemberArcXri(arcXri)) return false;

		return true;
	}

	public static boolean isAttributeSingletonArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;

		return true;
	}

	public static boolean isEntityMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;
		if (arcXri.getXRef().getNode().getNumSubSegments() <= 0) return false;
		if (arcXri.getXRef().getNode().getFirstSubSegment().hasGCS()) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getXRef().getNode().getFirstSubSegment().getLCS())) return false;

		return true;
	}

	public static boolean isAttributeMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;
		if (arcXri.getXRef().getNode().getNumSubSegments() <= 0) return false;
		if (arcXri.getXRef().getNode().getFirstSubSegment().hasGCS()) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getXRef().getNode().getFirstSubSegment().getLCS())) return false;

		return true;
	}
}
