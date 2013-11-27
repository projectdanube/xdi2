package xdi2.core.xri3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;

public class CloudName {

	private static final Logger log = LoggerFactory.getLogger(CloudName.class);

	private XDI3Segment xri;

	private CloudName(XDI3Segment xri) {

		this.xri = xri;
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

	public static CloudName fromXri(XDI3Segment xri) {

		if (! isValid(xri)) return null;

		return new CloudName(xri);
	}

	public XDI3Segment getXri() {

		return this.xri;
	}

	@Override
	public boolean equals(Object object) {

		return this.getXri().equals(object);
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
