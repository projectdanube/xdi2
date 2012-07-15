package xdi2.samples;

import java.io.StringReader;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.AttributeCollection;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MultiplicitySample {

	public static void main(String[] args) throws Exception {

		// create and print a graph with an attribute collection

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		AttributeCollection telAttributeCollection = Multiplicity.getAttributeCollection(contextNode, "+tel", true);
		telAttributeCollection.createMember().createLiteral("+1.206.555.1111");
		telAttributeCollection.createMember().createLiteral("+1.206.555.2222");

		System.out.println(graph);

		// write and re-read the graph, then find and print the members of the attribute collection

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph2, new StringReader(graph.toString()));
		ContextNode contextNode2 = graph.findContextNode(new XRI3Segment("=markus"), false);

		AttributeCollection telAttributeCollection2 = Multiplicity.getAttributeCollection(contextNode2, "+tel", false);

		for (Iterator<ContextNode> i = telAttributeCollection2.getMembers(); i.hasNext(); ) {

			System.out.println(i.next().getLiteral().getLiteralData());
		}
	}
}
