package xdi2.messaging.tests.target.interceptor.impl.linkcontract;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;

public class LinkContractInterceptorTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(LinkContractInterceptorTest.class);

	private static final XDIReader autoReader = XDIReaderRegistry.getAuto();

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	public void testDummy() {

	}

	public void testLinkContracts() throws Exception {

		int i=1, ii;

		while (true) {

			if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null) break;

			log.info("Graph " + i);

			Graph graph = graphFactory.openGraph(); 
			autoReader.read(graph, this.getClass().getResourceAsStream("graph" + i + ".xdi")).close();

			// check authorized

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("authorized" + i + "." + ii + ".xdi") == null) break;

				log.info("Authorized " + i + "." + ii);

				Graph authorized = graphFactory.openGraph(); 
				autoReader.read(authorized, this.getClass().getResourceAsStream("authorized" + i + "." + ii + ".xdi")).close();

				LinkContractInterceptor linkContractsInterceptor = new LinkContractInterceptor();
				linkContractsInterceptor.setLinkContractsGraph(graph);

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);
				graphMessagingTarget.getInterceptors().add(linkContractsInterceptor);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(authorized);
				MessageResult messageResult = new MessageResult();

				try {

					graphMessagingTarget.execute(messageEnvelope, messageResult, null);
					continue;
				} catch (Xdi2NotAuthorizedException ex) {

					fail();
				} finally {
					ii++;
				}
			}

			// check not authorized

			ii = 1;

			while (true) {

				if (this.getClass().getResourceAsStream("notauthorized" + i + "." + ii + ".xdi") == null) break;

				log.info("Not Authorized " + i + "." + ii);

				Graph notauthorized = graphFactory.openGraph(); 
				autoReader.read(notauthorized, this.getClass().getResourceAsStream("notauthorized" + i + "." + ii + ".xdi")).close();

				LinkContractInterceptor linkContractsInterceptor = new LinkContractInterceptor();
				linkContractsInterceptor.setLinkContractsGraph(graph);

				GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
				graphMessagingTarget.setGraph(graph);
				graphMessagingTarget.getInterceptors().add(linkContractsInterceptor);

				MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(notauthorized);
				MessageResult messageResult = new MessageResult();

				try {

					graphMessagingTarget.execute(messageEnvelope, messageResult, null);
					fail();
				} catch (Xdi2NotAuthorizedException ex) {

					continue;
				} finally {

					ii++;
				}
			}

			assertTrue(ii > 1);

			i++;
		}

		log.info("Done.");

		assertTrue(i > 1);
	}
}
