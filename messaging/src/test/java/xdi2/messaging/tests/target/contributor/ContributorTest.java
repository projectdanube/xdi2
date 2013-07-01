package xdi2.messaging.tests.target.contributor;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public class ContributorTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(ContributorTest.class);

	static String referenceGraphStatements = 
			"" +
					"(+con)=a<+b>&/&/\"val\"" + "\n" + 
					"(+con)=x*y/+c/(+con)=d*e" + "\n" + 
					"(+con)<+email>&/&/\"val\"" + "\n" + 
					"(+test)=markus/+friend/(+test)=animesh" + "\n";

	static String[] targetStrings = new String[] {
		"(+con)=a",
		"(+con)=a<+b>",
		"(+con)=a<+b>&",
		"(+con)=x",
		"(+con)=d",
		"(+con)<+email>",
		"(+con)<+email>&",
		"()",
		"(+con)",
		"(+test)"
	};

	public void testContributor() throws Exception {

		// init reference graph

		Graph referenceGraph = MemoryGraphFactory.getInstance().parseGraph(referenceGraphStatements);

		log.info("Reference graph: " + referenceGraph);

		// init messaging target with contributors

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();

		messagingTarget.setGraph(graph);

		messagingTarget.init();

		messagingTarget.getContributors().addContributor(new TestContributor1());
		messagingTarget.getContributors().addContributor(new TestContributor3());

		// go

		for (String targetString : targetStrings) {

			XDI3Segment target = XDI3Segment.create(targetString);

			// execute against messaging target with contributors

			log.info("Doing $get: " + targetString);

			MessageEnvelope envelope = MessageEnvelope.fromOperationXriAndTargetAddress(XDIMessagingConstants.XRI_S_GET, target);
			MessageResult result = new MessageResult();

			messagingTarget.execute(envelope, result, null);

			log.info("Result: " + result.getGraph().toString());

			// validate result

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
			ContextNode referenceContextNode = referenceGraph.getDeepContextNode(target);
			CopyUtil.copyContextNode(referenceContextNode, tempGraph, null);

			assertEquals(result.getGraph(), tempGraph);
		}

		// done

		messagingTarget.shutdown();
	}
}
