package xdi2.tests.core.io;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.Normalization;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

public class NormalizationTest extends TestCase {

	public void testNormalizedSerialization() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		String normalizedSerialization = "{\"/\":[\"=animesh\",\"=markus\"],\"=markus/\":[\"<#email>\"],\"=markus/#friend\":[\"=animesh\"],\"=markus<#email>/&\":\"markus@projectdanube.org\"}";

		assertEquals(Normalization.serialize(contextNode, null), normalizedSerialization);

		graph.close();
	}
}
