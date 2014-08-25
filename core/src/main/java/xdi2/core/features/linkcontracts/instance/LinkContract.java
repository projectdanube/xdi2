package xdi2.core.features.linkcontracts.instance;

import xdi2.core.features.linkcontracts.LinkContractBase;
import xdi2.core.features.linkcontracts.requester.RequesterLinkContract;
import xdi2.core.features.nodetypes.XdiEntity;

public abstract class LinkContract extends LinkContractBase {

	private static final long serialVersionUID = 7780858453875071410L;

	private XdiEntity xdiEntity;
	
	protected LinkContract(XdiEntity xdiEntity) {

		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity == null) return false;

		return
				RequesterLinkContract.isValid(xdiEntity) ||
				RootLinkContract.isValid(xdiEntity) ||
				PublicLinkContract.isValid(xdiEntity) ||
				GenericLinkContract.isValid(xdiEntity);
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromXdiEntity(XdiEntity xdiEntity) {

		LinkContract linkContract = null;

		if ((linkContract = RootLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = PublicLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = RequesterLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = CommunityLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = GenericLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;

		return null;
	}
	
	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity to which this XDI link contract is bound.
	 * @return An XDI entity that represents the XDI link contract.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	@Override
	public XdiEntity getXdiSubGraph() {

		return this.xdiEntity;
	}
}
