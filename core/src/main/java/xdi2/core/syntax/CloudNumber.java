package xdi2.core.syntax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.XDIAddressUtil;

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

			if (XDIaddress.getNumXDIArcs() < 2) { result = Boolean.FALSE; return result.booleanValue(); }

			for (int i=0; i< XDIaddress.getNumXDIArcs(); i+=2) {

				XDIArc XDIarc0 = XDIaddress.getXDIArc(i);
				XDIArc XDIarc1 = XDIaddress.getXDIArc(i + 1);

				if (XDIarc0.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIarc0.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc0.hasXRef() || XDIarc0.hasLiteralNode()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_AUTHORITY_PERSONAL.equals(XDIarc0.getCs()) && ! XDIConstants.CS_AUTHORITY_LEGAL.equals(XDIarc0.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }

				if (XDIarc1.isAttributeXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc1.isClassXs()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (XDIarc1.hasXRef() || ! XDIarc1.hasLiteralNode()) { result = Boolean.FALSE; return result.booleanValue(); }
				if (! XDIConstants.CS_MEMBER_UNORDERED.equals(XDIarc1.getCs())) { result = Boolean.FALSE; return result.booleanValue(); }
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

		XDIArc XDIarc1 = XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.fromComponents(cs, false, false, null, null));
		XDIArc XDIarc2 = XdiAbstractMemberUnordered.createRandomUuidXDIArc(XdiEntityCollection.class);

		XDIAddress XDIaddress = XDIAddressUtil.concatXDIAddresses(XDIarc1, XDIarc2);

		XDIArc peerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudNumber(XDIaddress, peerRootXDIArc);
	}

	public static CloudNumber fromXDIAddress(XDIAddress XDIaddress) {

		XDIaddress = XDIAddressUtil.parentXDIAddress(XDIaddress, 2);
		XDIaddress = XDIAddress.create(XDIaddress.toString().toLowerCase());

		if (! isValid(XDIaddress)) return null;

		XDIArc peerRootAddress = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		return new CloudNumber(XDIaddress, peerRootAddress);
	}

	public static CloudNumber fromPeerRootXDIArc(XDIArc peerRootXDIArc) {

		XDIAddress XDIaddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(peerRootXDIArc);

		return fromXDIAddress(XDIaddress);
	}

	public static CloudNumber fromPeerRootXDIArc(XDIAddress peerRootXDIArc) {

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
