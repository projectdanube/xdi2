package xdi2.tests.core.features.linkcontracts.instantiation;

import junit.framework.TestCase;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.instance.RelationshipLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class LinkContractInstantiationTest extends TestCase {

	public void testLinkContractInstantiation() throws Exception {

		XDIArc instanceXDIArc = XdiEntityInstanceUnordered.createXDIArc();

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(LinkContractTemplate.fromXdiEntitySingletonVariable(XDIBootstrap.GET_LINK_CONTRACT_TEMPLATE));

		linkContractInstantiation.setVariableValue(LinkContractInstantiation.XDI_ARC_V_REQUESTING_AUTHORITY, XDIAddress.create("=alice"));
		linkContractInstantiation.setVariableValue(LinkContractInstantiation.XDI_ARC_V_AUTHORIZING_AUTHORITY, XDIAddress.create("=bob"));
		linkContractInstantiation.setVariableValue(LinkContractInstantiation.XDI_ARC_V_INSTANCE, instanceXDIArc);

		linkContractInstantiation.setVariableValue(XDIArc.create("{$get}"), XDIAddress.create("=bob$card"));

		RelationshipLinkContract linkContract = (RelationshipLinkContract) linkContractInstantiation.execute();

		assertEquals(XDIAddress.create("=alice"), linkContract.getRequestingAuthority());
		assertEquals(XDIAddress.create("=bob"), linkContract.getAuthorizingAuthority());
		assertEquals(XDIAddress.create("$get"), linkContract.getTemplateAuthorityAndId());

		assertEquals(XDIAddress.create("=bob$card"), linkContract.getPermissionTargetXDIAddresses(XDIAddress.create("$get")).next());
	}
}
