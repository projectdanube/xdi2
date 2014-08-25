package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;

public class LinkContractInstantiation {

	private LinkContractTemplate linkContractTemplate;
	private XDIAddress authorizingAuthority;
	private XDIAddress requestingAuthority;

	public GenericLinkContract execute(Graph targetGraph, boolean create) {

		XDIAddress templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		// create generic link contract

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(targetGraph, this.getAuthorizingAuthority(), this.getRequestingAuthority(), templateAuthorityAndId, create);
		if (genericLinkContract == null) return null;
		if (genericLinkContract != null && ! create) return genericLinkContract;

		// set up permissions

		Map<XDIArc, XDIAddress> replacements = new HashMap<XDIArc, XDIAddress> ();
		replacements.put(XDILinkContractConstants.XDI_ARC_V_FROM, this.getRequestingAuthority());
		replacements.put(XDILinkContractConstants.XDI_ARC_V_TO, this.getAuthorizingAuthority());

		CopyStrategy copyStrategy = new ReplaceXDIAddressCopyStrategy(replacements);

		CopyUtil.copyRelations(this.getLinkContractTemplate().getContextNode(), genericLinkContract.getContextNode(), copyStrategy);

		// set up policy

		// done

		return genericLinkContract;
	}

	/*
	 * Getters and setters
	 */

	public LinkContractTemplate getLinkContractTemplate() {

		return this.linkContractTemplate;
	}

	public void setLinkContractTemplate(LinkContractTemplate linkContractTemplate) {

		this.linkContractTemplate = linkContractTemplate;
	}

	public XDIAddress getAuthorizingAuthority() {

		return this.authorizingAuthority;
	}

	public void setAuthorizingAuthority(XDIAddress authorizingAuthority) {

		this.authorizingAuthority = authorizingAuthority;
	}

	public XDIAddress getRequestingAuthority() {

		return this.requestingAuthority;
	}

	public void setRequestingAuthority(XDIAddress requestingAuthority) {

		this.requestingAuthority = requestingAuthority;
	}
}
