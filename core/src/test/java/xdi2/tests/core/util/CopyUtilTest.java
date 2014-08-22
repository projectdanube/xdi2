package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;

public class CopyUtilTest extends TestCase {

	public void testCopyUtil() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>&/&/\"markus.sabadello@gmail.com\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=neustar=animesh"));
		graph.setStatement(XDIStatement.create("=neustar=animesh<#email>&/&/\"animesh@gmail.com\""));
		graph.setStatement(XDIStatement.create("=neustar=animesh/#friend/=markus"));

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyGraph(graph, graph2, null);

		Graph graph3 = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyGraph(graph, graph3, null);
		CopyUtil.copyGraph(graph2, graph3, null);

		assertEquals(graph, graph2);
		assertEquals(graph2, graph3);
		assertEquals(graph3, graph);
		
		graph.close();
		graph2.close();
		graph3.close();
	}
}
