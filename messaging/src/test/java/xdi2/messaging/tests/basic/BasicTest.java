package xdi2.messaging.tests.basic;

import java.io.StringReader;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageContainer;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.ModOperation;
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

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(SENDER, true);
		Message message = messageContainer.getMessage(true);

		ContextNode[] contextNodes = new ContextNode[CONTEXTNODEXRIS.length]; 
		for (int i=0; i<CONTEXTNODEXRIS.length; i++) contextNodes[i] = messageEnvelope.getGraph().findContextNode(CONTEXTNODEXRIS[i], true);

		Operation addOperation = message.createAddOperation(contextNodes[0].getXri());
		Operation getOperation = message.createGetOperation(contextNodes[1].getXri());
		Operation modOperation = message.createModOperation(contextNodes[3].getXri());
		Operation delOperation = message.createDelOperation(contextNodes[2].getXri());

		assertTrue(messageContainer.equals(messageEnvelope.getMessageContainer(SENDER, false)));
		assertTrue(message.equals(messageContainer.getMessages().next()));
		assertTrue(addOperation.equals(message.getAddOperations().next()));
		assertTrue(getOperation.equals(message.getGetOperations().next()));
		assertTrue(modOperation.equals(message.getModOperations().next()));
		assertTrue(delOperation.equals(message.getDelOperations().next()));

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
		assertTrue(addOperation instanceof AddOperation);
		assertTrue(getOperation instanceof GetOperation);
		assertTrue(modOperation instanceof ModOperation);
		assertTrue(delOperation instanceof DelOperation);
	}

	public void testMessaging2() throws Exception {

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndTargetXri(XDIMessagingConstants.XRI_S_ADD, TARGET);
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(XDIMessagingConstants.XRI_S_ANONYMOUS, false);
		Message message = messageContainer.getMessages().next();
		Operation operation = message.getAddOperations().next();

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 1);
		assertEquals(messageContainer.getMessageCount(), 1);
		assertEquals(messageContainer.getOperationCount(), 1);
		assertEquals(message.getOperationCount(), 1);
		assertEquals(messageContainer.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(message.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getOperationXri(), XDIMessagingConstants.XRI_S_ADD);
		assertEquals(operation.getTarget(), TARGET);
		assertTrue(operation instanceof AddOperation);
	}

	public void testMessaging3() throws Exception {

		String string = "{\"=markus/()\": [   \"+email\",   \"+friends\"],\"=markus+name/()\": [   \"+last\"],\"=sender$($msg)$(!1e21.d620.fdca.95f4)$do/$mod\" : [ \"=markus+name+last\" ],\"=sender$($msg)$(!1e21.d620.fdca.95f4)$do/$add\" : [ \"=markus+email\" ],\"=sender$($msg)$(!1e21.d620.fdca.95f4)$do/$get\" : [ \"=markus\" ],\"=sender$($msg)$(!1e21.d620.fdca.95f4)$do/$del\" : [ \"=markus+friends\" ]}";
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.forFormat("XDI/JSON").read(graph, new StringReader(string), null);

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(SENDER, false);
		Message message = messageContainer.getMessage(false);
		Operation addOperation = message.getAddOperations().next();
		Operation getOperation = message.getGetOperations().next();
		Operation modOperation = message.getModOperations().next();
		Operation delOperation = message.getDelOperations().next();

		assertTrue(messageContainer.equals(messageEnvelope.getMessageContainer(SENDER, false)));
		assertTrue(message.equals(messageContainer.getMessages().next()));
		assertTrue(addOperation.equals(message.getAddOperations().next()));
		assertTrue(getOperation.equals(message.getGetOperations().next()));
		assertTrue(modOperation.equals(message.getModOperations().next()));
		assertTrue(delOperation.equals(message.getDelOperations().next()));

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
		assertTrue(addOperation instanceof AddOperation);
		assertTrue(getOperation instanceof GetOperation);
		assertTrue(modOperation instanceof ModOperation);
		assertTrue(delOperation instanceof DelOperation);
	}
}
