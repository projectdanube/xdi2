package xdi2.messaging.tests.messagingtarget;

import java.io.IOException;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public abstract class AbstractGraphMessagingTargetTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphMessagingTargetTest.class);

	private static final XDIReader autoReader = XDIReaderRegistry.getAuto();

	protected abstract Graph openNewGraph(String id) throws IOException;

	public void testGraphMessagingTarget() throws Exception {

		int i=1, ii;

		while (true) {

			if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null) break;

			Graph graph = this.openNewGraph(this.getClass().getName() + "-graph-" + i); 
			autoReader.read(graph, this.getClass().getResourceAsStream("graph" + i + ".xdi"), null).close();

			log.info("Graph " + i);

			// execute the messages

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi") == null) break;

				Graph message = this.openNewGraph(this.getClass().getName() + "-message-" + i + "-" + ii); 
				autoReader.read(message, this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi"), null).close();

				log.info("Message " + i + "." + ii);

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(message);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				ii++;
			}

			assertTrue(ii > 1);

			// check positives

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi") == null) break;

				Graph positive = this.openNewGraph(this.getClass().getName() + "-positive-" + i + "-" + ii); 
				autoReader.read(positive, this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi"), null).close();

				log.info("Positive " + i + "." + ii);

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(positive);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				assertFalse(messageResult.isEmpty());

				ii++;
			}

			assertTrue(ii > 1);

			// check negatives

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi") == null) break;

				Graph negative = this.openNewGraph(this.getClass().getName() + "-negative-" + i + "-" + ii); 
				autoReader.read(negative, this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi"), null).close();

				log.info("Negative " + i + "." + ii);

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(negative);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				assertTrue(messageResult.isEmpty());

				ii++;
			}

			assertTrue(ii > 1);

			i++;
		}

		log.info("Done.");

		assertTrue(i > 1);
	}
}
