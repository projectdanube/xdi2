package xdi2.samples.client;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class SimpleClientSample {

    static XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON", null);

    static XDIClient client = new XDIHttpClient("http://localhost:8080/xdi/mem-graph");

    static void doAdd() throws Exception {

        MessageEnvelope messageEnvelope = new MessageEnvelope();
        Message message = messageEnvelope.getMessage(XDI3Segment.create("=sender"), true);
        message.createAddOperation(XDI3Segment.create("(=markus+name/!/(data:,Markus))"));

        client.send(messageEnvelope, null);
    }

    static void doGet() throws Exception {

        MessageEnvelope messageEnvelope = new MessageEnvelope();
        Message message = messageEnvelope.getMessage(XDI3Segment.create("=sender"), true);
        message.createGetOperation(XDI3Segment.create("()"));

        MessageResult messageResult = new MessageResult();
        client.send(messageEnvelope, messageResult);
        writer.write(messageResult.getGraph(), System.out);
    }

    static void doDel() throws Exception {

        MessageEnvelope messageEnvelope = new MessageEnvelope();
        Message message = messageEnvelope.getMessage(XDI3Segment.create("=sender"), true);
        message.createDelOperation(XDI3Segment.create("()"));

        client.send(messageEnvelope, null);
    }

    public static void main(String[] args) throws Exception {

        LogManager.getLogger("xdi2").setLevel(Level.OFF);

        // run a $add message

        System.out.println("Running $add");
        doAdd();

        // run a $get message

        System.out.println("Running $get");
        doGet();

        // run a $del message

        System.out.println("Running $del");
        doDel();

        // run a $get message

        System.out.println("Running $get");
        doGet();
    }
}