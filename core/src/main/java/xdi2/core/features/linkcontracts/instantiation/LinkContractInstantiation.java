package xdi2.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.RelationshipLinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiAbstractVariable;
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

	public static final XDIArc XDI_ARC_V_AUTHORIZING_AUTHORITY = XDILinkContractConstants.XDI_ARC_V_TO;
	public static final XDIArc XDI_ARC_V_REQUESTING_AUTHORITY = XDILinkContractConstants.XDI_ARC_V_FROM;
	public static final XDIArc XDI_ARC_V_INSTANCE = XDIArc.create("{*!:uuid:0}");

	private LinkContractTemplate linkContractTemplate;
	private Map<XDIArc, Object> variableValues;

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate, Map<XDIArc, Object> variableValues) {

		this.linkContractTemplate = linkContractTemplate;
		this.variableValues = variableValues;
	}

	public LinkContractInstantiation(LinkContractTemplate linkContractTemplate) {

		this(linkContractTemplate, new HashMap<XDIArc, Object> ());
	}

	public LinkContractInstantiation() {

		this(null, new HashMap<XDIArc, Object> ());
	}

	public LinkContract execute() {

		// set up variable values including peer roots

		Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> (this.getVariableValues());

		for (Map.Entry<XDIArc, Object> entry : this.getVariableValues().entrySet()) {

			XDIArc key = entry.getKey();
			Object value = entry.getValue();

			if ((! XdiPeerRoot.isValidXDIArc(key)) && value instanceof XDIAddress && ((XDIAddress) value).getNumXDIArcs() == 1) {

				XDIArc peerKey = XDIArc.create(key.toString().replace("{", "{(").replace("}", ")}"));	// TODO: do this better
				if (variableValues.containsKey(peerKey)) continue;

				XDIArc peerValue = XdiPeerRoot.createPeerRootXDIArc((XDIAddress) value);

				variableValues.put(peerKey, peerValue);
			}
		}

		if (log.isDebugEnabled()) log.debug("Variable values: " + variableValues);

		// use variables

		Object instanceLiteralDataValue = XdiAbstractVariable.getVariableLiteralDataValue(variableValues, XDI_ARC_V_INSTANCE);
		XDIArc instanceXDIArcValue = XdiAbstractVariable.getVariableXDIArcValue(variableValues, XDI_ARC_V_INSTANCE);
		XDIAddress requestingAuthorityValue = XdiAbstractVariable.getVariableXDIAddressValue(variableValues, XDI_ARC_V_REQUESTING_AUTHORITY);
		XDIAddress authorizingAuthorityValue = XdiAbstractVariable.getVariableXDIAddressValue(variableValues, XDI_ARC_V_AUTHORIZING_AUTHORITY);

		if (requestingAuthorityValue == null) throw new NullPointerException("No requesting authority " + XDI_ARC_V_REQUESTING_AUTHORITY + "  value.");
		if (authorizingAuthorityValue == null) throw new NullPointerException("No authorizing authority " + XDI_ARC_V_AUTHORIZING_AUTHORITY + "  value.");

		// create relationship link contract

		XDIAddress templateAuthorityAndId = this.getLinkContractTemplate().getTemplateAuthorityAndId();

		Graph linkContractGraph = MemoryGraphFactory.getInstance().openGraph();
		LinkContract linkContract;

		if (instanceLiteralDataValue instanceof Double) {	// TODO here use the instanceXDIArcValue

			linkContract = RelationshipLinkContract.findRelationshipLinkContract(linkContractGraph, authorizingAuthorityValue, requestingAuthorityValue, templateAuthorityAndId, null, true);
		} if (instanceXDIArcValue != null) {

			linkContract = RelationshipLinkContract.findRelationshipLinkContract(linkContractGraph, authorizingAuthorityValue, requestingAuthorityValue, templateAuthorityAndId, instanceXDIArcValue, true);
		} else {

			linkContract = RelationshipLinkContract.findRelationshipLinkContract(linkContractGraph, authorizingAuthorityValue, requestingAuthorityValue, templateAuthorityAndId, null, true);
		}

		// TODO: make sure all variables in the link contract template have assigned values

		// instantiate

		CopyStrategy copyStrategy = new CompoundCopyStrategy(
				new ReplaceXDIAddressCopyStrategy(variableValues),
				new ReplaceLiteralVariablesCopyStrategy(variableValues),
				new ReplaceEscapedVariablesCopyStrategy());
		CopyUtil.copyContextNodeContents(this.getLinkContractTemplate().getContextNode(), linkContract.getContextNode(), copyStrategy);

		if (log.isDebugEnabled()) log.debug("Instantiated link contract " + linkContract + " from link contract template " + this.getLinkContractTemplate());

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

	public Map<XDIArc, Object> getVariableValues() {

		return this.variableValues;
	}

	public void setVariableValues(Map<XDIArc, Object> variableValues) {

		this.variableValues = variableValues;
	}

	public void setVariableValue(XDIArc key, Object value) {

		if (this.variableValues == null) this.variableValues = new HashMap<XDIArc, Object> ();
		this.variableValues.put(key, value);
	}
}
