package xdi2.messaging.tests.instantiation;

import junit.framework.TestCase;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageTemplate;
import xdi2.messaging.instantiation.MessageInstantiation;

public class MessageInstantiationTest extends TestCase {

	public void testMessageInstantiation() throws Exception {

		MessageInstantiation messageInstantiation = new MessageInstantiation(MessageTemplate.fromXdiEntitySingletonVariable(XDIBootstrap.GET_MESSAGE_TEMPLATE));

		messageInstantiation.setVariableValue(MessageInstantiation.XDI_ARC_V_SENDER, XDIAddress.create("=!:uuid:1111"));
		messageInstantiation.setVariableValue(MessageInstantiation.XDI_ARC_V_TO_PEER_ROOT, XDIAddress.create("(=!:uuid:2222)"));

		messageInstantiation.setVariableValue(XDIArc.create("{$get}"), XDIAddress.create("=!:uuid:2222<#email>"));

		Message message = messageInstantiation.execute();

		assertEquals(XDIAddress.create("=!:uuid:1111"), message.getSenderXDIAddress());
		assertEquals(XDIAddress.create("(=!:uuid:1111)"), message.getFromPeerRootXDIArc());
		assertEquals(XDIAddress.create("(=!:uuid:2222)"), message.getToPeerRootXDIArc());
		assertEquals(XDIAddress.create("=!:uuid:2222<#email>"), message.getGetOperations().next().getTargetXDIAddress());
		assertEquals(1, message.getOperationCount());
	}
}
