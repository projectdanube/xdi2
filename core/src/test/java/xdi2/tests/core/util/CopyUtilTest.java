package xdi2.tests.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.ConcatXDIAddressCopyStrategy;
import xdi2.core.util.CopyUtil.ExtractXDIAddressCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;
import xdi2.tests.AbstractTestCase;

public class CopyUtilTest extends AbstractTestCase {

	public void testCopyUtil() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=neustar=animesh"));
		graph.setStatement(XDIStatement.create("=neustar=animesh<#email>/&/\"animesh@gmail.com\""));
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

	public void testMultipleReplacements() throws Exception {

		Graph graph1 = MemoryGraphFactory.getInstance().openGraph();
		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();

		graph1.setStatement(XDIStatement.create("=a=b=c/+e/=f"));

		XDIArc from1 = XDIArc.create("=b");
		List<XDIAddress> to1 = new ArrayList<XDIAddress> ();
		to1.add(XDIAddress.create("=x"));
		to1.add(XDIAddress.create("=y"));

		XDIArc from2 = XDIArc.create("=f");
		List<XDIAddress> to2 = new ArrayList<XDIAddress> ();
		to2.add(XDIAddress.create("=m"));
		to2.add(XDIAddress.create("=n"));

		Map<XDIArc, Object> replacements = new HashMap<XDIArc, Object> ();
		replacements.put(from1, to1);
		replacements.put(from2, to2);

		CopyUtil.copyGraph(graph1, graph2, new ReplaceXDIAddressCopyStrategy(replacements));

		assertTrue(graph2.containsStatement(XDIStatement.create("=a=x=c/+e/=m")));
		assertTrue(graph2.containsStatement(XDIStatement.create("=a=x=c/+e/=n")));
		assertTrue(graph2.containsStatement(XDIStatement.create("=a=y=c/+e/=m")));
		assertTrue(graph2.containsStatement(XDIStatement.create("=a=y=c/+e/=n")));

		assertEquals(graph2.getAllStatementCount(), 11);

		graph1.close();
		graph2.close();
	}

	public void testConcatXDIAddressCopyStrategyLight() throws Exception {

		String graphString = "" +
				"//=a";

		String copiedGraphString = "" +
				"=x=y//=a";

		Graph graph1 = MemoryGraphFactory.getInstance().parseGraph(graphString);
		Graph graph2 = MemoryGraphFactory.getInstance().parseGraph(copiedGraphString);
		Graph graph3 = MemoryGraphFactory.getInstance().openGraph();

		CopyUtil.copyGraph(graph1, graph3, new ConcatXDIAddressCopyStrategy(XDIAddress.create("=x=y")));
		assertNotEquals(graph1, graph2);
		assertNotEquals(graph1, graph3);
		assertEquals(graph2, graph3);

		graph1.close();
		graph2.close();
		graph3.close();
	}

	public void testConcatXDIAddressCopyStrategy() throws Exception {

		String graphString = "" +
				"=a=b<#c>/&/123" + "\n" +
				"=a/#e/=f" + "\n" +
				"=f/#g/=a=b";

		String copiedGraphString = "" +
				"=x=y=a=b<#c>/&/123" + "\n" +
				"=x=y=a/#e/=x=y=f" + "\n" +
				"=x=y=f/#g/=x=y=a=b";

		Graph graph1 = MemoryGraphFactory.getInstance().parseGraph(graphString);
		Graph graph2 = MemoryGraphFactory.getInstance().parseGraph(copiedGraphString);
		Graph graph3 = MemoryGraphFactory.getInstance().openGraph();

		CopyUtil.copyGraph(graph1, graph3, new ConcatXDIAddressCopyStrategy(XDIAddress.create("=x=y")));
		assertNotEquals(graph1, graph2);
		assertNotEquals(graph1, graph3);
		assertEquals(graph2, graph3);

		graph1.close();
		graph2.close();
		graph3.close();
	}

	public void testExtractXDIAddressCopyStrategyLight() throws Exception {

		String graphString = "" +
				"(=z)//=a";

		String copiedGraphString = "" +
				"//=a";

		Graph graph1 = MemoryGraphFactory.getInstance().parseGraph(graphString);
		Graph graph2 = MemoryGraphFactory.getInstance().parseGraph(copiedGraphString);
		Graph graph3 = MemoryGraphFactory.getInstance().openGraph();

		CopyUtil.copyGraph(graph1, graph3, new ExtractXDIAddressCopyStrategy(XdiPeerRoot.class, false, false, false, false, true));
		assertNotEquals(graph1, graph2);
		assertNotEquals(graph1, graph3);
		assertEquals(graph2, graph3);

		graph1.close();
		graph2.close();
		graph3.close();
	}

	public void testExtractXDIAddressCopyStrategy() throws Exception {

		String graphString = "" +
				"(=z)=a=b<#c>/&/123" + "\n" +
				"(=z)=a/#e/(=z)=f" + "\n" +
				"(=z)=f/#g/(=z)=a=b";

		String copiedGraphString = "" +
				"=a=b<#c>/&/123" + "\n" +
				"=a/#e/=f" + "\n" +
				"=f/#g/=a=b";

		Graph graph1 = MemoryGraphFactory.getInstance().parseGraph(graphString);
		Graph graph2 = MemoryGraphFactory.getInstance().parseGraph(copiedGraphString);
		Graph graph3 = MemoryGraphFactory.getInstance().openGraph();

		CopyUtil.copyGraph(graph1, graph3, new ExtractXDIAddressCopyStrategy(XdiPeerRoot.class, false, false, false, false, true));
		assertNotEquals(graph1, graph2);
		assertNotEquals(graph1, graph3);
		assertEquals(graph2, graph3);

		graph1.close();
		graph2.close();
		graph3.close();
	}
}
