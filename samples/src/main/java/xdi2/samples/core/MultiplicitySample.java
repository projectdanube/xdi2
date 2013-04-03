package xdi2.samples.core;

import java.io.StringReader;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.contextfunctions.XdiAttributeInstance;
import xdi2.core.features.contextfunctions.XdiCollection;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class MultiplicitySample {

	public static void main(String[] args) throws Exception {

		// create and print a graph with a collection

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		XdiCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getXdiCollection(XDI3SubSegment.create("+tel"), true);
		telCollection.getXdiAttributeMember().getContextNode().createLiteral("+1.206.555.1111");
		telCollection.getXdiAttributeMember().getContextNode().createLiteral("+1.206.555.2222");

		System.out.println(graph.toString(new MimeType("application/xdi+json;pretty=1")));

		// write and re-read the graph, then find and print the members of the attribute collection

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph2, new StringReader(graph.toString()));
		ContextNode contextNode2 = graph.findContextNode(XDI3Segment.create("=markus"), false);

		XdiCollection telCollection2 = XdiSubGraph.fromContextNode(contextNode2).getXdiCollection(XDI3SubSegment.create("+tel"), false);

		for (Iterator<XdiAttributeInstance> i = telCollection2.attributes(); i.hasNext(); ) {

			System.out.println(i.next().getContextNode().getLiteral().getLiteralData());
		}
	}
}
