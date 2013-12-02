package xdi2.core.xri3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.XDI3Util;

public class CloudNumber {

	private static final Logger log = LoggerFactory.getLogger(CloudNumber.class);

	private XDI3Segment xri;
	private XDI3Segment peerRootXri;

	private CloudNumber(XDI3Segment xri, XDI3Segment peerRootXri) {

		this.xri = xri;
		this.peerRootXri = peerRootXri;
	}

	public static boolean isValid(final XDI3Segment xri) {

		if (xri == null) return false;

		Boolean result = null;

		try {

			if (xri.getNumSubSegments() < 2) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< xri.getNumSubSegments(); i+=2) {

				XDI3SubSegment subSegment0 = xri.getSubSegment(i);
				XDI3SubSegment subSegment1 = xri.getSubSegment(i + 1);

				if (subSegment0.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! subSegment0.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (subSegment0.hasXRef() || subSegment0.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_EQUALS.equals(subSegment0.getCs()) && ! XDIConstants.CS_AT.equals(subSegment0.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }

				if (subSegment1.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (subSegment1.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (subSegment1.hasXRef() || ! subSegment1.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_BANG.equals(subSegment1.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + xri + ") --> " + result);
		}
	}

	public static CloudNumber create(String string) {

		return fromXri(XDI3Segment.create(string));
	}

	public static CloudNumber createRandom(Character cs) {

		XDI3SubSegment subSegment1 = XdiEntityCollection.createArcXri(XDI3SubSegment.fromComponents(cs, false, false, null, null));
		XDI3SubSegment subSegment2 = XdiAbstractMemberUnordered.createRandomUuidArcXri(false);

		XDI3Segment xri = XDI3Util.concatXris(subSegment1, subSegment2);

		XDI3Segment peerRootXri = XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(xri));

		return new CloudNumber(xri, peerRootXri);
	}

	public static CloudNumber fromXri(XDI3Segment xri) {

		xri = XDI3Segment.create(xri.toString().toLowerCase());

		if (! isValid(xri)) return null;

		XDI3Segment peerRootXri = XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(xri));

		return new CloudNumber(xri, peerRootXri);
	}

	public static CloudNumber fromPeerRootXri(XDI3Segment peerRootXri) {

		XDI3Segment xri = XdiPeerRoot.getXriOfPeerRootArcXri(peerRootXri.getFirstSubSegment());

		return fromXri(xri);
	}

	public XDI3Segment getXri() {

		return this.xri;
	}

	public XDI3Segment getPeerRootXri() {

		return this.peerRootXri;
	}

	public Character getCs() {

		return this.getXri().getFirstSubSegment().getCs();
	}

	@Override
	public boolean equals(Object object) {

		if (! (object instanceof CloudNumber)) return false;
		if (object == this) return true;

		return this.getXri().equals(((CloudNumber) object).getXri());
	}

	@Override
	public int hashCode() {

		return this.getXri().hashCode();
	}

	@Override
	public String toString() {

		return this.getXri().toString();
	}
}
