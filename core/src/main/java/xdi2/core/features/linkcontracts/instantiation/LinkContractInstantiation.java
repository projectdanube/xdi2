package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXriCopyStrategy;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class LinkContractInstantiation {

	private LinkContractTemplate linkContractTemplate;
	private XDI3Segment authorizingAuthority;
	private XDI3Segment requestingAuthority;

	public GenericLinkContract execute(Graph targetGraph, boolean create) {

		XDI3Segment templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		// create generic link contract

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(targetGraph, this.getAuthorizingAuthority(), this.getRequestingAuthority(), templateAuthorityAndId, create);
		if (genericLinkContract == null) return null;
		if (genericLinkContract != null && ! create) return genericLinkContract;

		// set up permissions

		Map<XDI3SubSegment, XDI3Segment> replacements = new HashMap<XDI3SubSegment, XDI3Segment> ();
		replacements.put(XDILinkContractConstants.XRI_SS_V_FROM, this.getRequestingAuthority());
		replacements.put(XDILinkContractConstants.XRI_SS_V_TO, this.getAuthorizingAuthority());

		CopyStrategy copyStrategy = new ReplaceXriCopyStrategy(replacements);

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

	public XDI3Segment getAuthorizingAuthority() {

		return this.authorizingAuthority;
	}

	public void setAuthorizingAuthority(XDI3Segment authorizingAuthority) {

		this.authorizingAuthority = authorizingAuthority;
	}

	public XDI3Segment getRequestingAuthority() {

		return this.requestingAuthority;
	}

	public void setRequestingAuthority(XDI3Segment requestingAuthority) {

		this.requestingAuthority = requestingAuthority;
	}
}
