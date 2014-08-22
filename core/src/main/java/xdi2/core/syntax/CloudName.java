package xdi2.core.syntax;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;

public class CloudName {

	private static final Logger log = LoggerFactory.getLogger(CloudName.class);

	private XDIAddress address;
	private XDIArc peerRootArc;

	private CloudName(XDIAddress address, XDIArc peerRootArc) {

		this.address = address;
		this.peerRootArc = peerRootArc;
	}

	public static boolean isValid(final XDIAddress address) {

		if (address == null) return false;

		Boolean result = null;

		try {

			if (address.getNumArcs() < 1) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< address.getNumArcs(); i++) {

				XDIArc arc = address.getArc(i);

				if (arc.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (arc.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (arc.hasXRef() || ! arc.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_AUTHORITY_PERSONAL.equals(arc.getCs()) && ! XDIConstants.CS_AUTHORITY_LEGAL.equals(arc.getCs()) && ! XDIConstants.CS_AUTHORITY_GENERAL.equals(arc.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + address + ") --> " + result);
		}
	}

	public static CloudName create(String string) {

		return fromAddress(XDIAddress.create(string));
	}

	public static CloudName createRandom(Character cs, String prefix) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(cs);
		if (prefix != null) buffer.append(prefix);
		buffer.append(UUID.randomUUID().toString().toLowerCase().replace('-', '.'));

		XDIAddress address = XDIAddress.create(buffer.toString());

		XDIArc peerRootArc = XdiPeerRoot.createPeerRootArc(address);

		return new CloudName(address, peerRootArc);
	}

	public static CloudName fromAddress(XDIAddress address) {

		address = XDIAddress.create(address.toString().toLowerCase());

		if (! isValid(address)) return null;

		XDIArc peerRootArc = XdiPeerRoot.createPeerRootArc(address);

		return new CloudName(address, peerRootArc);
	}

	public static CloudName fromPeerRootArc(XDIArc peerRootArc) {

		XDIAddress address = XdiPeerRoot.getAddressOfPeerRootArc(peerRootArc);

		return fromAddress(address);
	}

	public static CloudName fromPeerRootArc(XDIAddress peerRootArc) {

		if (peerRootArc.getNumArcs() > 1) return null;
		
		return fromPeerRootArc(peerRootArc.getFirstArc());
	}

	public XDIAddress getAddress() {

		return this.address;
	}

	public XDIArc getPeerRootArc() {

		return this.peerRootArc;
	}

	public Character getCs() {

		return this.getAddress().getFirstArc().getCs();
	}

	@Override
	public boolean equals(Object object) {

		if (! (object instanceof CloudName)) return false;
		if (object == this) return true;

		return this.getAddress().equals(((CloudName) object).getAddress());
	}

	@Override
	public int hashCode() {

		return this.getAddress().hashCode();
	}

	@Override
	public String toString() {

		return this.getAddress().toString();
	}
}
