package xdi2.core.syntax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiPeerRoot;

public class CloudNumber {

	private static final Logger log = LoggerFactory.getLogger(CloudNumber.class);

	private XDIAddress XDIaddress;
	private XDIArc peerRootXDIArc;

	private CloudNumber(XDIAddress XDIaddress, XDIArc peerRootXDIArc) {

		this.XDIaddress = XDIaddress;
		this.peerRootXDIArc = peerRootXDIArc;
	}

	public static boolean isValid(final XDIAddress XDIaddress) {

		if (XDIaddress == null) return false;

		Boolean result = null;

		try {

			if (XDIaddress.getNumXDIArcs() != 1) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i<XDIaddress.getNumXDIArcs(); i+=2) {

				XDIArc XDIarc = XDIaddress.getXDIArc(i);

				if (XDIarc.isAttribute()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc.isCollection()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc.hasXRef() || ! XDIarc.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIarc.isImmutable()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc.isRelative()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_AUTHORITY_PERSONAL.equals(XDIarc.getCs()) && ! XDIConstants.CS_AUTHORITY_LEGAL.equals(XDIarc.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + XDIaddress + ") --> " + result);
		}
	}

	public static CloudNumber create(String string) {

		return fromXDIAddress(XDIAddress.create(string));
	}

	public static CloudNumber createRandom(Character cs) {

		XDIArc XDIarc = XdiEntitySingleton.createXDIArc(cs, true, false, XDIArc.literalFromRandomUuid(), null);

		XDIAddress XDIaddress = XDIAddress.fromComponent(XDIarc);

		XDIArc peerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudNumber(XDIaddress, peerRootXDIArc);
	}

	public static CloudNumber fromXDIAddress(XDIAddress XDIaddress) {

		if (XDIaddress.getNumXDIArcs() != 1) throw new Xdi2RuntimeException("Invalid cloud number length: " + XDIaddress);

		XDIaddress = XDIAddress.create(XDIaddress.toString().toLowerCase());

		if (! isValid(XDIaddress)) throw new Xdi2RuntimeException("Invalid cloud number: " + XDIaddress);

		XDIArc peerRootAddress = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudNumber(XDIaddress, peerRootAddress);
	}

	public static CloudNumber fromPeerRootXDIArc(XDIArc peerRootXDIArc) {

		XDIAddress XDIaddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(peerRootXDIArc);

		return fromXDIAddress(XDIaddress);
	}

	public static CloudNumber fromPeerRootXDIArc(XDIAddress peerRootXDIArc) {

		if (peerRootXDIArc.getNumXDIArcs() != 1) return null;

		return fromPeerRootXDIArc(peerRootXDIArc.getFirstXDIArc());
	}

	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	public XDIArc getPeerRootXDIArc() {

		return this.peerRootXDIArc;
	}

	public Character getCs() {

		return this.getXDIAddress().getFirstXDIArc().getCs();
	}

	@Override
	public boolean equals(Object object) {

		if (! (object instanceof CloudNumber)) return false;
		if (object == this) return true;

		return this.getXDIAddress().equals(((CloudNumber) object).getXDIAddress());
	}

	@Override
	public int hashCode() {

		return this.getXDIAddress().hashCode();
	}

	@Override
	public String toString() {

		return this.getXDIAddress().toString();
	}
}
