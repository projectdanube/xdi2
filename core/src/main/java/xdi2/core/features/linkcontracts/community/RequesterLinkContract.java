package xdi2.core.features.linkcontracts.community;

import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiEntity;

/**
 * An XDI requester link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class RequesterLinkContract extends LinkContract {

	private static final long serialVersionUID = -4713483572204567144L;

	protected RequesterLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}
}
