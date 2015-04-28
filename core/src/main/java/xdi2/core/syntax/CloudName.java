package xdi2.core.syntax;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.XDIAddressUtil;

public class CloudName {

	private static final Logger log = LoggerFactory.getLogger(CloudName.class);

	private XDIAddress XDIaddress;
	private XDIArc peerRootXDIArc;

	private CloudName(XDIAddress XDIaddress, XDIArc peerRootXDIArc) {

		this.XDIaddress = XDIaddress;
		this.peerRootXDIArc = peerRootXDIArc;
	}

	public static boolean isValid(final XDIAddress XDIaddress) {

		if (XDIaddress == null) return false;

		Boolean result = null;

		try {

			if (XDIaddress.getNumXDIArcs() < 1) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< XDIaddress.getNumXDIArcs(); i++) {

				XDIArc XDIarc = XDIaddress.getXDIArc(i);

				if (XDIarc.isAttribute()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc.isCollection()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc.hasXRef() || ! XDIarc.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_AUTHORITY_PERSONAL.equals(XDIarc.getCs()) && ! XDIConstants.CS_AUTHORITY_LEGAL.equals(XDIarc.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + XDIaddress + ") --> " + result);
		}
	}

	public static CloudName create(String string) {

		return fromXDIAddress(XDIAddress.create(string));
	}

	public static CloudName createRandom(Character cs, String prefix) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(cs);
		if (prefix != null) buffer.append(prefix);
		buffer.append(UUID.randomUUID().toString().toLowerCase().replace('-', '.'));

		XDIAddress XDIaddress = XDIAddress.create(buffer.toString());

		XDIArc peerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudName(XDIaddress, peerRootXDIArc);
	}

	public static CloudName fromXDIAddress(XDIAddress XDIaddress) {

		if (XDIaddress.getNumXDIArcs() < 2) throw new Xdi2RuntimeException("Invalid cloud name length: " + XDIaddress);

		XDIaddress = XDIAddressUtil.parentXDIAddress(XDIaddress, 1);
		XDIaddress = XDIAddress.create(XDIaddress.toString().toLowerCase());

		if (! isValid(XDIaddress)) throw new Xdi2RuntimeException("Invalid cloud name: " + XDIaddress);

		XDIArc peerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudName(XDIaddress, peerRootXDIArc);
	}

	public static CloudName fromPeerRootXDIArc(XDIArc peerRootXDIArc) {

		XDIAddress XDIaddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(peerRootXDIArc);

		return fromXDIAddress(XDIaddress);
	}

	public static CloudName fromPeerRootXDIArc(XDIAddress peerRootXDIArc) {

		if (peerRootXDIArc.getNumXDIArcs() > 1) return null;

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

		if (! (object instanceof CloudName)) return false;
		if (object == this) return true;

		return this.getXDIAddress().equals(((CloudName) object).getXDIAddress());
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
