package xdi2.messaging.tests.basic;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageContainer;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.util.XDIMessagingConstants;

public class BasicTest extends TestCase {

	private static final XRI3Segment SENDER = new XRI3Segment("=sender");
	
	private static final XRI3Segment TARGET = new XRI3Segment("=markus");

	private static final XRI3Segment CONTEXTNODEXRIS[] = new XRI3Segment[] {
		new XRI3Segment("=markus+email"),
		new XRI3Segment("=markus"),
		new XRI3Segment("=markus+friends"),
		new XRI3Segment("=markus+name+last")
	};

	public void testMessaging() throws Exception {

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(SENDER, true);
		Message message = messageContainer.createMessage();

		ContextNode[] contextNodes = new ContextNode[CONTEXTNODEXRIS.length]; 
		for (int i=0; i<CONTEXTNODEXRIS.length; i++) contextNodes[i] = messageEnvelope.getGraph().findContextNode(CONTEXTNODEXRIS[i], true);

		Operation addOperation = message.createAddOperation(contextNodes[0]);
		Operation getOperation = message.createGetOperation(contextNodes[1]);
		Operation delOperation = message.createDelOperation(contextNodes[2]);
		Operation modOperation = message.createModOperation(contextNodes[3]);

		assertTrue(messageContainer.equals(messageEnvelope.getMessageContainer(SENDER, false)));
		assertTrue(message.equals(messageContainer.getMessages().next()));
		assertTrue(addOperation.equals(message.getAddOperation()));
		assertTrue(getOperation.equals(message.getGetOperation()));
		assertTrue(delOperation.equals(message.getDelOperation()));
		assertTrue(modOperation.equals(message.getModOperation()));

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 4);
		assertEquals(messageContainer.getMessageCount(), 1);
		assertEquals(messageContainer.getOperationCount(), 4);
		assertEquals(message.getOperationCount(), 4);
		assertEquals(messageContainer.getSender(), SENDER);
		assertEquals(message.getSender(), SENDER);
		assertEquals(addOperation.getSender(), SENDER);
		assertEquals(getOperation.getSender(), SENDER);
		assertEquals(delOperation.getSender(), SENDER);
		assertEquals(modOperation.getSender(), SENDER);
	}

	public void testMessaging2() throws Exception {
		
		MessageEnvelope messageEnvelope = MessageEnvelope.fromXriAndOperationXri(TARGET, XDIMessagingConstants.XRI_S_ADD);
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(XDIMessagingConstants.XRI_S_ANONYMOUS, false);
		Message message = messageContainer.getMessages().next();
		Operation operation = message.getAddOperation();

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 1);
		assertEquals(messageContainer.getMessageCount(), 1);
		assertEquals(messageContainer.getOperationCount(), 1);
		assertEquals(message.getOperationCount(), 1);
		assertEquals(messageContainer.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(message.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getOperationXri(), XDIMessagingConstants.XRI_S_ADD);
		assertEquals(operation.getOperationTargetXri(), TARGET);
	}
}
