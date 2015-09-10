package xdi2.messaging.target.tests.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.Node;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public class ContributorTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(ContributorTest.class);

	static String referenceGraphStatements = 
			"" +
					"(#con)=a<#b>/&/\"val\"" + "\n" + 
					"(#con)=x*y/#c/(#con)=d*e" + "\n" + 
					"(#con)<#email>/&/\"val\"" + "\n" + 
					"(#test)=markus/#friend/(#test)=animesh" + "\n";

	static String[] targetStrings = new String[] {
		"(#con)=a",
		"(#con)=a<#b>",
		"(#con)=a<#b>&",
		"(#con)=x",
		"(#con)=d",
		"(#con)<#email>",
		"(#con)<#email>&",
		"",
		"(#con)",
		"(#test)"
	};

	public void testContributor() throws Exception {

		// init reference graph

		Graph referenceGraph = MemoryGraphFactory.getInstance().parseGraph(referenceGraphStatements, "XDI DISPLAY", null);

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

			XDIAddress target = XDIAddress.create(targetString);

			// execute against messaging target with contributors

			log.info("Doing $get: " + targetString);

			MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(XDIMessagingConstants.XDI_ADD_GET, target);
			ExecutionContext executionContext = ExecutionContext.createExecutionContext();
			ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

			messagingTarget.execute(messageEnvelope, executionContext, executionResult);

 			log.info("Result: " + executionResult.makeLightMessagingResponse().getResultGraph().toString());

			// validate result

			Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
			Node referenceNode = referenceGraph.getDeepNode(target);
			CopyUtil.copyNode(referenceNode, tempGraph, null);

			assertEquals(executionResult.makeLightMessagingResponse().getResultGraph(), tempGraph);

			tempGraph.close();
		}

		// done

		messagingTarget.shutdown();
	}
}
