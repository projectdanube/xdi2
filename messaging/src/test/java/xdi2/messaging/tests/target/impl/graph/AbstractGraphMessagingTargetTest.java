package xdi2.messaging.tests.target.impl.graph;

import java.io.IOException;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.RefInterceptor;

public abstract class AbstractGraphMessagingTargetTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphMessagingTargetTest.class);

	private static final XDIReader autoReader = XDIReaderRegistry.getAuto();

	protected abstract Graph openGraph(String identifier) throws IOException;

	public void testMessageEnvelope() throws Exception {

		Graph graph = this.openGraph(this.getClass().getName() + "-graph-me-1"); 

		GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget(); graphMessagingTarget.setGraph(graph);
		graphMessagingTarget.init();

		MessageEnvelope messageEnvelope1 = MessageEnvelope.fromOperationXDIAddressAndTargetXDIStatements(XDIMessagingConstants.XDI_ADD_SET, new SingleItemIterator<XDIStatement> (XDIStatement.create("=markus/+friend/=giovanni")));
		graphMessagingTarget.execute(messageEnvelope1);
		assertEquals(graph.getDeepRelation(XDIAddress.create("=markus"), XDIAddress.create("+friend")).getTargetContextNodeXDIAddress(), XDIAddress.create("=giovanni"));

		MessageEnvelope messageEnvelope2 = MessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(XDIMessagingConstants.XDI_ADD_GET, XDIAddress.create("=markus"));
		Graph resultGraph2 = graphMessagingTarget.execute(messageEnvelope2);
		assertEquals(resultGraph2.getDeepRelation(XDIAddress.create("=markus"), XDIAddress.create("+friend")).getTargetContextNodeXDIAddress(), XDIAddress.create("=giovanni"));

		graphMessagingTarget.shutdown();
	}

	public void testGraphMessagingTarget() throws Exception {

		int i=1, ii;

		while (true) {

			if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null) break;

			log.info("Graph " + i);

			Graph graph = this.openGraph(this.getClass().getName() + "-graph-" + i); 
			autoReader.read(graph, this.getClass().getResourceAsStream("graph" + i + ".xdi")).close();

			GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
			graphMessagingTarget.setGraph(graph);
			graphMessagingTarget.getInterceptors().addInterceptor(new RefInterceptor());

			graphMessagingTarget.init();

			try {

				// execute the messages

				ii = 1;

				while (true) {

					if (this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi") == null) break;

					log.info("Message " + i + "." + ii);

					Graph message = this.openGraph(this.getClass().getName() + "-message-" + i + "-" + ii); 
					autoReader.read(message, this.getClass().getResourceAsStream("message" + i + "." + ii + ".xdi")).close();

					MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(message);

					try {

						graphMessagingTarget.execute(messageEnvelope);
					} finally {

						message.close();
					}

					ii++;
				}

				// check positives

				ii = 1;

				while (true) {

					if (this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi") == null) break;

					log.info("Positive " + i + "." + ii);

					Graph positive = this.openGraph(this.getClass().getName() + "-positive-" + i + "-" + ii); 
					autoReader.read(positive, this.getClass().getResourceAsStream("positive" + i + "." + ii + ".xdi")).close();

					MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(positive);
					Graph resultGraph;

					try {

						resultGraph = graphMessagingTarget.execute(messageEnvelope);
					} finally {

						positive.close();
					}

					assertFalse(resultGraph.isEmpty());

					ii++;
				}

				// check negatives

				ii = 1;

				while (true) {

					if (this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi") == null) break;

					log.info("Negative " + i + "." + ii);

					Graph negative = this.openGraph(this.getClass().getName() + "-negative-" + i + "-" + ii); 
					autoReader.read(negative, this.getClass().getResourceAsStream("negative" + i + "." + ii + ".xdi")).close();

					MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(negative);
					Graph resultGraph;

					try {

						resultGraph = graphMessagingTarget.execute(messageEnvelope);
					} finally {

						negative.close();
					}

					assertTrue(resultGraph.isEmpty());

					ii++;
				}

				// check exceptions

				ii = 1;

				while (true) {

					if (this.getClass().getResourceAsStream("exception" + i + "." + ii + ".xdi") == null) break;

					log.info("Exception " + i + "." + ii);

					Graph exception = this.openGraph(this.getClass().getName() + "-exception-" + i + "-" + ii); 
					autoReader.read(exception, this.getClass().getResourceAsStream("exception" + i + "." + ii + ".xdi")).close();

					MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(exception);

					try {

						graphMessagingTarget.execute(messageEnvelope);

						fail();
					} catch (Xdi2MessagingException ex) {

					} finally {

						exception.close();
					}

					ii++;
				}
			} finally {

				graphMessagingTarget.shutdown();
			}

			// next graph

			i++;
		}

		log.info("Done.");

		assertTrue(i > 1);
	}
}
