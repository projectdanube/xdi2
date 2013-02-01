package xdi2.samples.messaging;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public class SimpleMessagingSample {

    public static void main(String[] args) throws Exception {

        XDIReader reader = XDIReaderRegistry.forFormat("XDI DISPLAY", null);
        XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON", null);

        // load an XDI graph and create a messaging target

        Graph graph = MemoryGraphFactory.getInstance().openGraph();
        reader.read(graph, SimpleMessagingSample.class.getResourceAsStream("simple.xdi"));
        GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
        graphMessagingTarget.setGraph(graph);

        // create a message

        MessageEnvelope messageEnvelope = new MessageEnvelope();
        MessageCollection messageCollection = messageEnvelope.getMessageCollection(XDI3Segment.create("=sender"), true);
        Message message = messageCollection.getMessage(true);
        message.createGetOperation(messageEnvelope.getGraph().getRootContextNode().getXri());

        // execute the message

        MessageResult messageResult = new MessageResult();
        graphMessagingTarget.execute(messageEnvelope, messageResult, null);

        // serialize the result

        writer.write(messageResult.getGraph(), System.out);
    }
}