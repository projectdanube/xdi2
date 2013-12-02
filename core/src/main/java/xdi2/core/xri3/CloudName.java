package xdi2.core.xri3;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;

public class CloudName {

	private static final Logger log = LoggerFactory.getLogger(CloudName.class);

	private XDI3Segment xri;
	private XDI3Segment peerRootXri;

	private CloudName(XDI3Segment xri, XDI3Segment peerRootXri) {

		this.xri = xri;
		this.peerRootXri = peerRootXri;
	}

	public static boolean isValid(final XDI3Segment xri) {

		if (xri == null) return false;

		Boolean result = null;

		try {

			if (xri.getNumSubSegments() < 1) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< xri.getNumSubSegments(); i++) {

				XDI3SubSegment subSegment = xri.getSubSegment(i);

				if (subSegment.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (subSegment.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (subSegment.hasXRef() || ! subSegment.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_EQUALS.equals(subSegment.getCs()) && ! XDIConstants.CS_AT.equals(subSegment.getCs()) && ! XDIConstants.CS_STAR.equals(subSegment.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + xri + ") --> " + result);
		}
	}

	public static CloudName create(String string) {

		return fromXri(XDI3Segment.create(string));
	}

	public static CloudName createRandom(Character cs, String prefix) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(cs);
		if (prefix != null) buffer.append(prefix);
		buffer.append(UUID.randomUUID().toString().toLowerCase().replace('-', '.'));

		XDI3Segment xri = XDI3Segment.create(buffer.toString());

		XDI3Segment peerRootXri = XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(xri));

		return new CloudName(xri, peerRootXri);
	}

	public static CloudName fromXri(XDI3Segment xri) {

		xri = XDI3Segment.create(xri.toString().toLowerCase());

		if (! isValid(xri)) return null;

		XDI3Segment peerRootXri = XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(xri));

		return new CloudName(xri, peerRootXri);
	}

	public static CloudName fromPeerRootXri(XDI3Segment peerRootXri) {

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

		if (! (object instanceof CloudName)) return false;
		if (object == this) return true;

		return this.getXri().equals(((CloudName) object).getXri());
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
