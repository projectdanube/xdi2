package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CompoundCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceEscapedVariablesCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceLiteralVariablesCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;

public class LinkContractInstantiation {

	private static final Logger log = LoggerFactory.getLogger(LinkContractInstantiation.class);

	public static final XDIArc XDI_ARC_INSTANCE_VARIABLE = XDIArc.create("{*!:uuid:0}");

	public static final XDIArc[] XDI_ARC_RESERVED_VARIABLES = new XDIArc[] {

			XDILinkContractConstants.XDI_ARC_V_FROM,
			XDILinkContractConstants.XDI_ARC_V_TO,
			XDILinkContractConstants.XDI_ARC_V_FROM_ROOT,
			XDILinkContractConstants.XDI_ARC_V_TO_ROOT,
			XDILinkContractConstants.XDI_ARC_CONTRACT,
			XDI_ARC_INSTANCE_VARIABLE
	};

	private LinkContractTemplate linkContractTemplate;
	private XDIAddress authorizingAuthority;
	private XDIAddress requestingAuthority;
	private Map<XDIArc, Object> variableValues;

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate, XDIAddress authorizingAuthority, XDIAddress requestingAuthority, Map<XDIArc, Object> variableValues) {

		this.linkContractTemplate = linkContractTemplate;
		this.authorizingAuthority = authorizingAuthority;
		this.requestingAuthority = requestingAuthority;
		this.variableValues = variableValues;
	}

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate) {

		this(linkContractTemplate, null, null, null);
	}

	public LinkContractInstantiation() {

		this(null, null, null, null);
	}

	public LinkContract execute(XDIArc instanceXDIArc, boolean create) {

		XDIAddress templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		// create generic link contract

		if (this.getAuthorizingAuthority() == null) throw new NullPointerException("No authorizing authority.");
		if (this.getRequestingAuthority() == null) throw new NullPointerException("No requesting authority.");

		Graph linkContractGraph = MemoryGraphFactory.getInstance().openGraph();

		LinkContract linkContract = GenericLinkContract.findGenericLinkContract(linkContractGraph, this.getAuthorizingAuthority(), this.getRequestingAuthority(), templateAuthorityAndId, instanceXDIArc, create);
		if (linkContract == null) return null;
		if (linkContract != null && ! create) return linkContract;

		if (log.isDebugEnabled()) log.debug("Instantiated link contract " + linkContract + " from link contract template " + this.getLinkContractTemplate());

		// check for reserved variables

		for (XDIArc reservedVariable : XDI_ARC_RESERVED_VARIABLES) {

			if (this.getVariableValues().containsKey(reservedVariable)) throw new Xdi2RuntimeException("Cannot set reserved variable " + reservedVariable + " during link contract instantiation.");
		}

		// TODO: make sure all variables in the link contract template have assigned values

		// set up variable values

		Map<XDIArc, Object> allVariableValues = new HashMap<XDIArc, Object> ();
		if (this.getVariableValues() != null) allVariableValues.putAll(this.getVariableValues());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM, this.getRequestingAuthority());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_TO, this.getAuthorizingAuthority());
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM_ROOT, XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(this.getRequestingAuthority())));
		allVariableValues.put(XDILinkContractConstants.XDI_ARC_V_TO_ROOT, XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(this.getAuthorizingAuthority())));

		if (log.isDebugEnabled()) log.debug("Variable values: " + allVariableValues);

		// instantiate

		CopyStrategy copyStrategy = new CompoundCopyStrategy(
				new ReplaceXDIAddressCopyStrategy(allVariableValues),
				new ReplaceLiteralVariablesCopyStrategy(allVariableValues),
				new ReplaceEscapedVariablesCopyStrategy());
		CopyUtil.copyContextNodeContents(this.getLinkContractTemplate().getContextNode(), linkContract.getContextNode(), copyStrategy);

		// add push permission inverse relations

		linkContract.setupPushPermissionInverseRelations();

		// add type statement

		Dictionary.setContextNodeType(linkContract.getContextNode(), this.getLinkContractTemplate().getContextNode().getXDIAddress());

		// done

		return linkContract;
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

	public Map<XDIArc, Object> getVariableValues() {

		return this.variableValues;
	}

	public void setVariableValues(Map<XDIArc, Object> variableValues) {

		this.variableValues = variableValues;
	}
}
