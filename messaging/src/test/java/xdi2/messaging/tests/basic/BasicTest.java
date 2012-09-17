package xdi2.messaging.tests.basic;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;

public class BasicTest extends TestCase {

	private static final XRI3Segment SENDER = new XRI3Segment("=sender");

	private static final XRI3Segment TARGET = new XRI3Segment("=markus");

	private static final XRI3Segment CONTEXTNODEXRIS[] = new XRI3Segment[] {
		new XRI3Segment("=markus+email"),
		new XRI3Segment("=markus"),
		new XRI3Segment("=markus+friends"),
		new XRI3Segment("=markus+name+last")
	};

	public void testMessagingOverview() throws Exception {

		// create a message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		assertTrue(MessageEnvelope.isValid(messageEnvelope.getGraph()));

		assertFalse(messageEnvelope.getMessageCollections().hasNext());
		assertNull(messageEnvelope.getMessageCollection(SENDER, false));
		assertEquals(messageEnvelope.getMessageCollectionCount(), 0);
		assertFalse(messageEnvelope.getMessages().hasNext());
		assertFalse(messageEnvelope.getMessages(SENDER).hasNext());
		assertNull(messageEnvelope.getMessage(SENDER, false));
		assertEquals(messageEnvelope.getMessageCount(), 0);

		// create a message collection

		MessageCollection messageCollection = messageEnvelope.getMessageCollection(SENDER, true);

		assertTrue(MessageCollection.isValid(messageCollection.getEntityCollection()));

		assertTrue(messageEnvelope.getMessageCollections().hasNext());
		assertNotNull(messageEnvelope.getMessageCollection(SENDER, false));
		assertEquals(messageEnvelope.getMessageCollectionCount(), 1);
		assertFalse(messageEnvelope.getMessages().hasNext());
		assertFalse(messageEnvelope.getMessages(SENDER).hasNext());
		assertNull(messageEnvelope.getMessage(SENDER, false));
		assertEquals(messageEnvelope.getMessageCount(), 0);

		assertFalse(messageCollection.getMessages().hasNext());
		assertNull(messageCollection.getMessage(false));
		assertEquals(messageCollection.getMessageCount(), 0);

		// create a message

		Message message = messageCollection.getMessage(true);

		assertTrue(Message.isValid(message.getEntityMember()));

		assertTrue(messageEnvelope.getMessageCollections().hasNext());
		assertNotNull(messageEnvelope.getMessageCollection(SENDER, false));
		assertEquals(messageEnvelope.getMessageCollectionCount(), 1);
		assertTrue(messageEnvelope.getMessages().hasNext());
		assertTrue(messageEnvelope.getMessages(SENDER).hasNext());
		assertNotNull(messageEnvelope.getMessage(SENDER, false));
		assertEquals(messageEnvelope.getMessageCount(), 1);

		assertTrue(messageCollection.getMessages().hasNext());
		assertNotNull(messageCollection.getMessage(false));
		assertEquals(messageCollection.getMessageCount(), 1);

		assertFalse(message.getOperations().hasNext());
		assertEquals(message.getOperationCount(), 0);

		// create some operations

		ContextNode[] contextNodes = new ContextNode[CONTEXTNODEXRIS.length]; 
		for (int i=0; i<CONTEXTNODEXRIS.length; i++) contextNodes[i] = messageEnvelope.getGraph().findContextNode(CONTEXTNODEXRIS[i], true);

		Operation addOperation = message.createAddOperation(contextNodes[0].getXri());
		Operation getOperation = message.createGetOperation(contextNodes[1].getXri());
		Operation modOperation = message.createModOperation(contextNodes[3].getXri());
		Operation delOperation = message.createDelOperation(contextNodes[2].getXri());

		assertTrue(messageCollection.equals(messageEnvelope.getMessageCollection(SENDER, false)));
		assertTrue(message.equals(messageCollection.getMessages().next()));
		assertTrue(addOperation.equals(message.getAddOperations().next()));
		assertTrue(getOperation.equals(message.getGetOperations().next()));
		assertTrue(modOperation.equals(message.getModOperations().next()));
		assertTrue(delOperation.equals(message.getDelOperations().next()));

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 4);
		assertEquals(messageCollection.getMessageCount(), 1);
		assertEquals(messageCollection.getOperationCount(), 4);
		assertEquals(message.getOperationCount(), 4);
		assertEquals(messageCollection.getSender(), SENDER);
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

	public void testMessagingFromOperationXriAndTargetXri() throws Exception {

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndTargetXri(XDIMessagingConstants.XRI_S_ADD, TARGET);
		MessageCollection messageCollection = messageEnvelope.getMessageCollection(XDIMessagingConstants.XRI_S_ANONYMOUS, false);
		Message message = messageCollection.getMessages().next();
		Operation operation = message.getAddOperations().next();

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 1);
		assertEquals(messageCollection.getMessageCount(), 1);
		assertEquals(messageCollection.getOperationCount(), 1);
		assertEquals(message.getOperationCount(), 1);
		assertEquals(messageCollection.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(message.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getSender(), XDIMessagingConstants.XRI_S_ANONYMOUS);
		assertEquals(operation.getOperationXri(), XDIMessagingConstants.XRI_S_ADD);
		assertEquals(operation.getTarget(), TARGET);
		assertTrue(operation instanceof AddOperation);
	}

	public void testMessagingSimple1() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, this.getClass().getResourceAsStream("simple.1.xdi")).close();

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);
		MessageCollection messageCollection = messageEnvelope.getMessageCollection(SENDER, false);
		Message message = messageCollection.getMessage(false);
		Operation addOperation = message.getAddOperations().next();
		Operation getOperation = message.getGetOperations().next();
		Operation modOperation = message.getModOperations().next();
		Operation delOperation = message.getDelOperations().next();

		assertTrue(messageCollection.equals(messageEnvelope.getMessageCollection(SENDER, false)));
		assertTrue(message.equals(messageCollection.getMessages().next()));
		assertTrue(addOperation.equals(message.getAddOperations().next()));
		assertTrue(getOperation.equals(message.getGetOperations().next()));
		assertTrue(modOperation.equals(message.getModOperations().next()));
		assertTrue(delOperation.equals(message.getDelOperations().next()));

		assertEquals(messageEnvelope.getMessageCount(), 1);
		assertEquals(messageEnvelope.getOperationCount(), 4);
		assertEquals(messageCollection.getMessageCount(), 1);
		assertEquals(messageCollection.getOperationCount(), 4);
		assertEquals(message.getOperationCount(), 4);
		assertEquals(messageCollection.getSender(), SENDER);
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

	public void testMessagingSimple2() throws Exception {

		XRI3Segment sender = new XRI3Segment("=!1111");

		GregorianCalendar calendar = new GregorianCalendar(2010, 11, 22, 22, 22, 22);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, this.getClass().getResourceAsStream("simple.2.xdi")).close();

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);
		MessageCollection messageCollection = messageEnvelope.getMessageCollection(sender, false);
		Message message = messageCollection.getMessage(false);

		assertEquals(message.getLinkContractXri(), new XRI3Segment("$(=!2222)$(!1)$do"));
		LinkContract linkContract = LinkContracts.findLinkContractByAddress(messageEnvelope.getGraph(), message.getLinkContractXri());
		assertNotNull(linkContract);
		assertEquals(linkContract.getAssignees().next().getXri(), sender);

		assertEquals(message.getSenderAuthority(), new XRI3Segment("(=!1111)(!3)"));
		assertEquals(message.getRecipientAuthority(), new XRI3Segment("(=!2222)"));
		assertEquals(message.getTimestamp(), calendar.getTime());
	}

	public void testSenderAndRecipientAuthority() throws Exception {

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(new XRI3Segment("=sender"), true);
		message.setSenderAuthority(new XRI3Segment("(=!1111)"));
		message.setRecipientAuthority(new XRI3Segment("(=!2222)"));
		assertEquals(message.getSenderAuthority(), new XRI3Segment("(=!1111)"));
		assertEquals(message.getRecipientAuthority(), new XRI3Segment("(=!2222)"));
	}
}
