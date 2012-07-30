package xdi2.samples.core;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class SimpleCoreSample {

    public static void main(String[] args) throws Exception {

        Graph graph = MemoryGraphFactory.getInstance().openGraph();

        ContextNode root = graph.getRootContextNode();
        ContextNode markus = root.createContextNode(new XRI3SubSegment("=markus"));
        ContextNode name = markus.createContextNode(new XRI3SubSegment("+name"));
        ContextNode email = markus.createContextNode(new XRI3SubSegment("+email"));
        name.createLiteral("Markus Sabadello");
        email.createLiteral("markus.sabadello@gmail.com");

        System.out.println("Serialization in XDI/JSON: \n");
        XDIWriterRegistry.forFormat("XDI/JSON", null).write(graph, System.out);
        System.out.println();

        System.out.println("Serialization in XDI statements:\n");
        XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);
    }
}