package xdi2.core.syntax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.AddressUtil;

public class CloudNumber {

	private static final Logger log = LoggerFactory.getLogger(CloudNumber.class);

	private XDIAddress address;
	private XDIArc peerRootArc;

	private CloudNumber(XDIAddress address, XDIArc peerRootArc) {

		this.address = address;
		this.peerRootArc = peerRootArc;
	}

	public static boolean isValid(final XDIAddress address) {

		if (address == null) return false;

		Boolean result = null;

		try {

			if (address.getNumArcs() < 2) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< address.getNumArcs(); i+=2) {

				XDIArc arc0 = address.getArc(i);
				XDIArc arc1 = address.getArc(i + 1);

				if (arc0.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! arc0.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (arc0.hasXRef() || arc0.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_AUTHORITY_PERSONAL.equals(arc0.getCs()) && ! XDIConstants.CS_AUTHORITY_LEGAL.equals(arc0.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }

				if (arc1.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (arc1.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (arc1.hasXRef() || ! arc1.hasLiteral()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_MEMBER_UNORDERED.equals(arc1.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
			}

			{ result = Boolean.TRUE; return result.booleanValue(); }
		} finally {

			if (log.isTraceEnabled()) log.trace("isValid(" + address + ") --> " + result);
		}
	}

	public static CloudNumber create(String string) {

		return fromAddress(XDIAddress.create(string));
	}

	public static CloudNumber createRandom(Character cs) {

		XDIArc arc1 = XdiEntityCollection.createarc(XDIArc.fromComponents(cs, false, false, null, null));
		XDIArc arc2 = XdiAbstractMemberUnordered.createRandomUuidarc(false);

		XDIAddress address = AddressUtil.concatAddresses(arc1, arc2);

		XDIArc peerRootArc = XdiPeerRoot.createPeerRootArc(address);

		return new CloudNumber(address, peerRootArc);
	}

	public static CloudNumber fromAddress(XDIAddress address) {

		address = XDIAddress.create(address.toString().toLowerCase());

		if (! isValid(address)) return null;

		XDIArc peerRootAddress = XdiPeerRoot.createPeerRootArc(address);

		return new CloudNumber(address, peerRootAddress);
	}

	public static CloudNumber fromPeerRootArc(XDIArc peerRootArc) {

		XDIAddress address = XdiPeerRoot.getAddressOfPeerRootArc(peerRootArc);

		return fromAddress(address);
	}

	public static CloudNumber fromPeerRootArc(XDIAddress peerRootArc) {

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

		if (! (object instanceof CloudNumber)) return false;
		if (object == this) return true;

		return this.getAddress().equals(((CloudNumber) object).getAddress());
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
