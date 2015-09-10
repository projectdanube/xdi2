package xdi2.messaging.target.tests.impl.graph;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
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

		MessageEnvelope messageEnvelope1 = MessageEnvelope.fromOperationXDIAddressAndTargetXDIStatements(XDIMessagingConstants.XDI_ADD_SET, new SingleItemIterator<XDIStatement> (XDIStatement.create("=markus/#friend/=giovanni")));
		ExecutionContext executionContext1 = ExecutionContext.createExecutionContext();
		ExecutionResult executionResult1 = ExecutionResult.createExecutionResult(messageEnvelope1);
		graphMessagingTarget.execute(messageEnvelope1, executionContext1, executionResult1);

		MessageEnvelope messageEnvelope2 = MessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(XDIMessagingConstants.XDI_ADD_GET, XDIAddress.create("=markus"));
		ExecutionContext executionContext2 = ExecutionContext.createExecutionContext();
		ExecutionResult executionResult2 = ExecutionResult.createExecutionResult(messageEnvelope2);
		graphMessagingTarget.execute(messageEnvelope2, executionContext2, executionResult2);

		assertEquals(executionResult2.makeLightMessagingResponse().getResultGraph().getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend")).getTargetXDIAddress(), XDIAddress.create("=giovanni"));

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
					ExecutionContext executionContext = ExecutionContext.createExecutionContext();
					ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

					try {

						graphMessagingTarget.execute(messageEnvelope, executionContext, executionResult);
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
					ExecutionContext executionContext = ExecutionContext.createExecutionContext();
					ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

					try {

						graphMessagingTarget.execute(messageEnvelope, executionContext, executionResult);
					} finally {

						positive.close();
					}

					assertFalse(executionResult.makeLightMessagingResponse().getResultGraph().isEmpty());

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
					ExecutionContext executionContext = ExecutionContext.createExecutionContext();
					ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

					try {

						graphMessagingTarget.execute(messageEnvelope, executionContext, executionResult);
					} finally {

						negative.close();
					}

					assertTrue(executionResult.makeLightMessagingResponse().getResultGraph().isEmpty());

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
					ExecutionContext executionContext = ExecutionContext.createExecutionContext();
					ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

					try {

						graphMessagingTarget.execute(messageEnvelope, executionContext, executionResult);

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
