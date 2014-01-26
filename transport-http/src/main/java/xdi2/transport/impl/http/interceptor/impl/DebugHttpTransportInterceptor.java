package xdi2.transport.impl.http.interceptor.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.plugins.PluginsLoader;
import xdi2.core.xri3.XDI3ParserRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.transport.Request;
import xdi2.transport.Response;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpRequest;
import xdi2.transport.impl.http.HttpResponse;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.factory.MessagingTargetFactory;
import xdi2.transport.impl.http.interceptor.HttpTransportInterceptor;
import xdi2.transport.impl.http.registry.MessagingTargetFactoryMount;
import xdi2.transport.impl.http.registry.MessagingTargetMount;
import xdi2.transport.interceptor.TransportInterceptor;

/**
 * This interceptor prints out a list of mounted messaging targets.
 * This can be used for debugging purposes with a standard web browser.
 * 
 * @author markus
 */
public class DebugHttpTransportInterceptor extends AbstractInterceptor<Transport<?, ?>> implements TransportInterceptor, HttpTransportInterceptor {

	public static final int DEFAULT_LOG_CAPACITY = 10;

	private int logCapacity;
	private LinkedList<LogEntry> log;

	private VelocityEngine velocityEngine;

	public DebugHttpTransportInterceptor() {

		this.logCapacity = DEFAULT_LOG_CAPACITY;
		this.log = new LinkedList<LogEntry> ();

		this.velocityEngine = new VelocityEngine();
	}

	public void init(Transport<?, ?> transport) {

		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}

	/*
	 * TransportInterceptor
	 */

	@Override
	public boolean before(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException {

		return false;
	}

	@Override
	public boolean after(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException {

		this.getLog().addFirst(new LogEntry(request, response, messagingTarget, messageEnvelope, messageResult, null));
		if (this.getLog().size() > this.getLogCapacity()) this.getLog().removeLast();

		return false;
	}

	@Override
	public void exception(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, ErrorMessageResult errorMessageResult, ExecutionContext executionContext, Exception ex) {

		this.getLog().addFirst(new LogEntry(request, response, messagingTarget, messageEnvelope, errorMessageResult, ex));
		if (this.getLog().size() > this.getLogCapacity()) this.getLog().removeLast();
	}

	/*
	 * HttpTransportInterceptor
	 */

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().equals("/")) return false;

		String cmd = request.getParameter("cmd");
		String cmdMessagingTargetPath = request.getParameter("messagingtargetpath");
		String cmdMessagingTargetFactoryPath = request.getParameter("messagingtargetfactorypath");
		String format = request.getParameter("format");
		String writeImplied = request.getParameter("writeImplied");
		String writeOrdered = request.getParameter("writeOrdered");
		String writeInner = request.getParameter("writeInner");
		String writePretty = request.getParameter("writePretty");
		String graphstring = request.getParameter("graphstring");

		if ("reload".equals(cmd)) {

			httpTransport.getHttpMessagingTargetRegistry().reload();

			return this.processGetRequest(httpTransport, request, response, messagingTargetMount);
		}

		if ("unmount_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpMessagingTargetRegistry().getMessagingTarget(cmdMessagingTargetPath);
			if (cmdMessagingTarget != null) httpTransport.getHttpMessagingTargetRegistry().unmountMessagingTarget(cmdMessagingTarget);

			return this.processGetRequest(httpTransport, request, response, messagingTargetMount);
		}

		if ("unmount_messaging_target_factory".equals(cmd) && cmdMessagingTargetFactoryPath != null) {

			MessagingTargetFactory cmdMessagingTargetFactory = httpTransport.getHttpMessagingTargetRegistry().getMessagingTargetFactory(cmdMessagingTargetFactoryPath);
			if (cmdMessagingTargetFactory != null) httpTransport.getHttpMessagingTargetRegistry().unmountMessagingTargetFactory(cmdMessagingTargetFactory);

			return this.processGetRequest(httpTransport, request, response, messagingTargetMount);
		}

		if ("edit_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpMessagingTargetRegistry().getMessagingTarget(cmdMessagingTargetPath);

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writeImplied = null;
				writeOrdered = null;
				writeInner = "on";
				writePretty = null;
			}

			Properties xdiWriterParameters = new Properties();

			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "on".equals(writeInner) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

			XDIWriter xdiWriter = XDIWriterRegistry.forFormat(format, xdiWriterParameters);

			StringWriter stringWriter = new StringWriter();

			xdiWriter.write(((GraphMessagingTarget) cmdMessagingTarget).getGraph(), stringWriter);

			graphstring = stringWriter.getBuffer().toString();

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", XDI3ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("request", request);
			context.put("messagingtarget", cmdMessagingTarget);
			context.put("messagingtargetpath", cmdMessagingTargetPath);
			context.put("format", format);
			context.put("writeImplied", writeImplied);
			context.put("writeOrdered", writeOrdered);
			context.put("writeInner", writeInner);
			context.put("writePretty", writePretty);
			context.put("graphstring", graphstring);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			PrintWriter writer = new PrintWriter(response.getBodyWriter());

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			this.velocityEngine.evaluate(context, writer, "debug-edit.vm", reader);
			writer.close();

			// done

			return this.processGetRequest(httpTransport, request, response, messagingTargetMount);
		}

		if ("save_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpMessagingTargetRegistry().getMessagingTarget(cmdMessagingTargetPath);

			// parse graph

			XDIReader xdiReader = XDIReaderRegistry.getAuto();

			Graph graph = ((GraphMessagingTarget) cmdMessagingTarget).getGraph();

			String error = null;

			try {

				graph.clear();
				xdiReader.read(graph, new StringReader(graphstring));
			} catch (Xdi2ParseException ex) {

				error = ex.getMessage();
			}

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", XDI3ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("request", request);
			context.put("messagingtarget", cmdMessagingTarget);
			context.put("messagingtargetpath", cmdMessagingTargetPath);
			context.put("format", format);
			context.put("writeImplied", writeImplied);
			context.put("writeOrdered", writeOrdered);
			context.put("writeInner", writeInner);
			context.put("writePretty", writePretty);
			context.put("graphstring", graphstring);
			context.put("error", error);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			PrintWriter writer = new PrintWriter(response.getBodyWriter());

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			this.velocityEngine.evaluate(context, writer, "debug-edit.vm", reader);
			writer.close();

			// done

			return this.processGetRequest(httpTransport, request, response, messagingTargetMount);
		}

		// done

		return false;
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().equals("/")) return false;

		// prepare velocity

		File[] pluginFiles = PluginsLoader.getFiles();
		Properties systemProperties = System.getProperties();
		List<MessagingTargetMount> messagingTargetMounts = httpTransport.getHttpMessagingTargetRegistry().getMessagingTargetMounts();
		List<MessagingTargetFactoryMount> messagingTargetFactoryMounts = httpTransport.getHttpMessagingTargetRegistry().getMessagingTargetFactoryMounts();

		VelocityContext context = new VelocityContext();
		context.put("httptransport", httpTransport);
		context.put("request", request);
		context.put("parser", XDI3ParserRegistry.getInstance().getParser());
		context.put("pluginfiles", pluginFiles);
		context.put("systemproperties", systemProperties);
		context.put("messagingtargetmounts", messagingTargetMounts);
		context.put("messagingtargetfactorymounts", messagingTargetFactoryMounts);
		context.put("log", this.getLog());

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug.vm"));
		PrintWriter bodyWriter = new PrintWriter(response.getBodyWriter());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		this.velocityEngine.evaluate(context, bodyWriter, "debug.vm", reader);
		bodyWriter.close();

		// done

		return true;
	}

	@Override
	public boolean processPutRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	/*
	 * Getters and setters
	 */

	public LinkedList<LogEntry> getLog() {

		return this.log;
	}

	public void setLog(LinkedList<LogEntry> log) {

		this.log = log;
	}

	public int getLogCapacity() {

		return this.logCapacity;
	}

	public void setLogCapacity(int logCapacity) {

		this.logCapacity = logCapacity;
	}

	/*
	 * Helper classes
	 */

	public static class LogEntry {

		private Date time;
		private Request request;
		private Response response;
		private MessagingTarget messagingTarget;
		private MessageEnvelope messageEnvelope;
		private MessageResult messageResult;
		private Exception ex;

		public LogEntry(Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, Exception ex) {

			this.time = new Date();
			this.request = request;
			this.response = response;
			this.messagingTarget = messagingTarget;
			this.messageEnvelope = messageEnvelope;
			this.messageResult = messageResult;
			this.ex = ex;
		}

		public Date getTime() {

			return this.time;
		}

		public void setTime(Date time) {

			this.time = time;
		}

		public Request getRequest() {

			return this.request;
		}

		public void setRequest(Request request) {

			this.request = request;
		}

		public Response getResponse() {

			return this.response;
		}

		public void setResponse(Response response) {

			this.response = response;
		}

		public MessagingTarget getMessagingTarget() {

			return this.messagingTarget;
		}

		public void setMessagingTarget(MessagingTarget messagingTarget) {

			this.messagingTarget = messagingTarget;
		}

		public MessageEnvelope getMessageEnvelope() {

			return this.messageEnvelope;
		}

		public void setMessageEnvelope(MessageEnvelope messageEnvelope) {

			this.messageEnvelope = messageEnvelope;
		}

		public MessageResult getMessageResult() {

			return this.messageResult;
		}

		public void setMessageResult(MessageResult messageResult) {

			this.messageResult = messageResult;
		}

		public Exception getEx() {

			return this.ex;
		}

		public void setEx(Exception ex) {

			this.ex = ex;
		}
	}
}
