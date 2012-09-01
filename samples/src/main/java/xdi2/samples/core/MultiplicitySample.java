package xdi2.samples.core;

import java.io.StringReader;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MultiplicitySample {

	public static void main(String[] args) throws Exception {

		// create and print a graph with an attribute collection

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		XdiCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getCollection(new XRI3SubSegment("+tel"), true);
		telCollection.createAttributeMember().getContextNode().createLiteral("+1.206.555.1111");
		telCollection.createAttributeMember().getContextNode().createLiteral("+1.206.555.2222");

		System.out.println(graph.toString(new MimeType("application/xdi+json;pretty=1")));

		// write and re-read the graph, then find and print the members of the attribute collection

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph2, new StringReader(graph.toString()));
		ContextNode contextNode2 = graph.findContextNode(new XRI3Segment("=markus"), false);

		XdiCollection telCollection2 = XdiSubGraph.fromContextNode(contextNode2).getCollection(new XRI3SubSegment("+tel"), false);

		for (Iterator<XdiAttributeMember> i = telCollection2.attributes(); i.hasNext(); ) {

			System.out.println(i.next().getContextNode().getLiteral().getLiteralData());
		}
	}
}
