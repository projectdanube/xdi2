package xdi2.messaging.tests.messagingtarget;

import java.io.IOException;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.DollarIsInterceptor;

public abstract class AbstractGraphMessagingTargetTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphMessagingTargetTest.class);

	private static final XDIReader autoReader = XDIReaderRegistry.getAuto();

	protected abstract Graph openNewGraph(String id) throws IOException;

	public void testMessageEnvelope() throws Exception {

		Graph graph = this.openNewGraph(this.getClass().getName() + "-graph-me-1"); 

		GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget(); graphMessagingTarget.setGraph(graph);

		MessageEnvelope messageEnvelope1 = MessageEnvelope.fromOperationXriAndStatement(XDIMessagingConstants.XRI_S_ADD, "=markus/+friend/=giovanni");
		MessageResult messageResult1 = new MessageResult();
		graphMessagingTarget.execute(messageEnvelope1, messageResult1, null);
		assertEquals(graph.findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")).getTargetContextNodeXri(), new XRI3Segment("=giovanni"));

		MessageEnvelope messageEnvelope2 = MessageEnvelope.fromOperationXriAndTargetXri(XDIMessagingConstants.XRI_S_GET, new XRI3Segment("=markus"));
		MessageResult messageResult2 = new MessageResult();
		graphMessagingTarget.execute(messageEnvelope2, messageResult2, null);
		assertEquals(messageResult2.getGraph().findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")).getTargetContextNodeXri(), new XRI3Segment("=giovanni"));
	}

	public void testGraphMessagingTarget() throws Exception {

		int i=1, ii;

		while (true) {

			if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null) break;

			Graph graph = this.openNewGraph(this.getClass().getName() + "-graph-" + i); 
			autoReader.read(graph, this.getClass().getResourceAsStream("graph" + i + ".xdi")).close();

			GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
			graphMessagingTarget.setGraph(graph);
			graphMessagingTarget.getInterceptors().addInterceptor(new DollarIsInterceptor());

			log.info("Graph " + i);

			// execute the messages

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi") == null) break;

				Graph message = this.openNewGraph(this.getClass().getName() + "-message-" + i + "-" + ii); 
				autoReader.read(message, this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi")).close();

				log.info("Message " + i + "." + ii);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(message);
				MessageResult messageResult = new MessageResult();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				ii++;
			}

			// check positives

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi") == null) break;

				Graph positive = this.openNewGraph(this.getClass().getName() + "-positive-" + i + "-" + ii); 
				autoReader.read(positive, this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi")).close();

				log.info("Positive " + i + "." + ii);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(positive);
				MessageResult messageResult = new MessageResult();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				assertFalse(messageResult.isEmpty());

				ii++;
			}

			// check exceptions

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("exception" + i + "." + ii + ".xdi") == null) break;

				Graph negative = this.openNewGraph(this.getClass().getName() + "-exception-" + i + "-" + ii); 
				autoReader.read(negative, this.getClass().getResourceAsStream("exception" + i + "." + ii + ".xdi")).close();

				log.info("Exception " + i + "." + ii);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(negative);
				MessageResult messageResult = new MessageResult();

				try {

					graphMessagingTarget.execute(messageEnvelope, messageResult, null);

					fail();
				} catch (Xdi2MessagingException ex) {

				}

				ii++;
			}

			// next graph

			i++;
		}

		log.info("Done.");

		assertTrue(i > 1);
	}
}
