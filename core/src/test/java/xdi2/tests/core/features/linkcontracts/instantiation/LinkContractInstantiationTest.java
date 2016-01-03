package xdi2.tests.core.features.linkcontracts.instantiation;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class LinkContractInstantiationTest extends TestCase {

	public void testLinkContractInstantiation() throws Exception {

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation();

		linkContractInstantiation.setAuthorizingAuthority(XDIAddress.create("=bob"));
		linkContractInstantiation.setRequestingAuthority(XDIAddress.create("=alice"));
		linkContractInstantiation.setLinkContractTemplate(XDIBootstrap.GET_LINK_CONTRACT_TEMPLATE);

		Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> ();
		variableValues.put(XDIArc.create("{$get}"), XDIAddress.create("=bob$card"));

		linkContractInstantiation.setVariableValues(variableValues);

		GenericLinkContract linkContract = (GenericLinkContract) linkContractInstantiation.execute(true);

		assertEquals(XDIAddress.create("=alice"), linkContract.getRequestingAuthority());
		assertEquals(XDIAddress.create("=bob"), linkContract.getAuthorizingAuthority());
		assertEquals(XDIAddress.create("$get"), linkContract.getTemplateAuthorityAndId());

		assertEquals(XDIAddress.create("=bob$card"), linkContract.getPermissionTargetXDIAddresses(XDIAddress.create("$get")).next());
	}
}
