package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger log = LoggerFactory.getLogger(LinkContractInstantiation.class);

	private LinkContractTemplate linkContractTemplate;
	private XDIAddress authorizingAuthority;
	private XDIAddress requestingAuthority;

	public GenericLinkContract execute(Graph targetGraph, Map<XDIArc, XDIAddress> variableValues, boolean create) {

		XDIAddress templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		// create generic link contract

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(targetGraph, this.getAuthorizingAuthority(), this.getRequestingAuthority(), templateAuthorityAndId, create);
		if (genericLinkContract == null) return null;
		if (genericLinkContract != null && ! create) return genericLinkContract;

		if (log.isDebugEnabled()) log.debug("Instantiated link contract " + genericLinkContract + " from link contract template " + this.getLinkContractTemplate());

		// set up permissions

		Map<XDIArc, XDIAddress> replacements = new HashMap<XDIArc, XDIAddress> ();
		replacements.putAll(variableValues);
		replacements.put(XDILinkContractConstants.XDI_ARC_V_FROM, this.getRequestingAuthority());
		replacements.put(XDILinkContractConstants.XDI_ARC_V_TO, this.getAuthorizingAuthority());

		// instantiate

		CopyStrategy copyStrategy = new ReplaceXDIAddressCopyStrategy(replacements);

		CopyUtil.copyContextNodeContents(this.getLinkContractTemplate().getContextNode(), genericLinkContract.getContextNode(), copyStrategy);

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
