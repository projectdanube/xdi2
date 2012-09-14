package xdi2.samples.core;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class SimpleCoreSample {

	public static void main(String[] args) throws Exception {

		// create a simple graph

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode root = graph.getRootContextNode();
		ContextNode markus = root.createContextNode(new XRI3SubSegment("=markus"));
		ContextNode animesh = root.createContextNode(new XRI3SubSegment("=animesh"));
		ContextNode name = markus.createContextNode(new XRI3SubSegment("+name"));
		name.createLiteral("Markus Sabadello");
		markus.createRelation(new XRI3Segment("+friend"), animesh);

		// write it in different serialization formats

		System.out.println("Serialization in XDI/JSON: \n");
		XDIWriterRegistry.forFormat("XDI/JSON", null).write(graph, System.out);
		System.out.println();

		System.out.println("Serialization in XDI statements:\n");
		XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

		// write the statement associated with a context node

		System.out.println("Statement associated with =markus: " + markus.getStatement());

		// close the graph

		graph.close();
	}
}