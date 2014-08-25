package xdi2.core.features.linkcontracts.community;

import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiEntity;

/**
 * An XDI requester link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class CommunityLinkContract extends GenericLinkContract {

	private static final long serialVersionUID = -4713483572204567144L;

	protected CommunityLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}
}
