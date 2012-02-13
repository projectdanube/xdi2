package xdi2.server.tests;

import junit.framework.TestCase;
import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public class GraphMessagingTargetTest extends TestCase {

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();
	private static XDIReader autoReader = XDIReaderRegistry.getAuto();

	public void testGraphMessagingTarget() throws Exception {

		int i=1, ii;

		while (true) {

			if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null) break;

			Graph graph = graphFactory.openGraph(); autoReader.read(graph, this.getClass().getResourceAsStream("graph" + i + ".xdi"), null).close();

			// execute the messages

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi") == null) break;

				Graph message = graphFactory.openGraph(); autoReader.read(message, this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi"), null).close();

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(message);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);
				
				ii++;
			}

			// check positives

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi") == null) break;

				Graph positive = graphFactory.openGraph(); autoReader.read(positive, this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi"), null).close();

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(positive);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				assertFalse(messageResult.isEmpty());

				ii++;
			}

			// check negatives

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi") == null) break;

				Graph negative = graphFactory.openGraph(); autoReader.read(negative, this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi"), null).close();

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(negative);
				MessageResult messageResult = MessageResult.newInstance();

				graphMessagingTarget.execute(messageEnvelope, messageResult, null);

				assertTrue(messageResult.isEmpty());

				ii++;
			}

			i++;
		}
	}
}
