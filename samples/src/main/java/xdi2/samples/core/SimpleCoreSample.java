package xdi2.samples.core;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
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
		Relation relation = markus.createRelation(new XRI3Segment("+friend"), animesh);
		Literal literal = name.createLiteral("Markus Sabadello");

		// write it in different serialization formats

		System.out.println("Serialization in XDI/JSON: \n");
		XDIWriterRegistry.forFormat("XDI/JSON", null).write(graph, System.out);
		System.out.println();

		System.out.println("Serialization in XDI statements:\n");
		XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

		// write the statement associated with a context node

		System.out.println("Statement associated with a context node: " + markus.getStatement());
		System.out.println("Statement associated with a relation: " + relation.getStatement());
		System.out.println("Statement associated with a literal: " + literal.getStatement());

		// close the graph

		graph.close();
	}
}