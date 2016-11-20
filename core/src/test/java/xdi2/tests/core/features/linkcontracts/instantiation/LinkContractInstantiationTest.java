package xdi2.tests.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.features.linkcontracts.instance.RelationshipLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class LinkContractInstantiationTest extends TestCase {

	public void testLinkContractInstantiation() throws Exception {

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(XDIBootstrap.GET_LINK_CONTRACT_TEMPLATE);

		linkContractInstantiation.setAuthorizingAuthority(XDIAddress.create("=bob"));
		linkContractInstantiation.setRequestingAuthority(XDIAddress.create("=alice"));

		XDIArc instanceXDIArc = XdiEntityInstanceUnordered.createXDIArc();

		Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> ();
		variableValues.put(XDIArc.create("{$get}"), XDIAddress.create("=bob$card"));

		linkContractInstantiation.setVariableValues(variableValues);

		RelationshipLinkContract linkContract = (RelationshipLinkContract) linkContractInstantiation.execute(instanceXDIArc, true);

		assertEquals(XDIAddress.create("=alice"), linkContract.getRequestingAuthority());
		assertEquals(XDIAddress.create("=bob"), linkContract.getAuthorizingAuthority());
		assertEquals(XDIAddress.create("$get"), linkContract.getTemplateAuthorityAndId());

		assertEquals(XDIAddress.create("=bob$card"), linkContract.getPermissionTargetXDIAddresses(XDIAddress.create("$get")).next());
	}
}
