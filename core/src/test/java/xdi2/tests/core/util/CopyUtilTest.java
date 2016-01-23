package xdi2.tests.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;

public class CopyUtilTest extends TestCase {

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
}
