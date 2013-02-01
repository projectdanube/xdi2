package xdi2.samples.core;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public class SimpleCoreSample {

	public static void main(String[] args) throws Exception {

		// create a simple graph with context nodes, a relation, and a literal

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode root = graph.getRootContextNode();
		ContextNode markus = root.createContextNode(XDI3SubSegment.create("=markus"));
		ContextNode animesh = root.createContextNode(XDI3SubSegment.create("=animesh"));
		ContextNode name = markus.createContextNode(XDI3SubSegment.create("+name"));
		Relation relation = markus.createRelation(XDI3Segment.create("+friend"), animesh);
		Literal literal = name.createLiteral("Markus Sabadello");

		// write some statements from our graph

		System.out.println("Statement associated with a context node: " + markus.getStatement());
		System.out.println("Statement associated with a relation: " + relation.getStatement());
		System.out.println("Statement associated with a literal: " + literal.getStatement());

		// we can also add a whole new statement to the graph

		graph.createStatement(XDI3Statement.create("=alice/+friend/=bob"));

		// write the whole graph in different serialization formats

		System.out.println("Serialization in XDI/JSON: \n");
		XDIWriterRegistry.forFormat("XDI/JSON", null).write(graph, System.out);
		System.out.println();

		System.out.println("Serialization in XDI statements:\n");
		XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

		// close the graph

		graph.close();
	}
}