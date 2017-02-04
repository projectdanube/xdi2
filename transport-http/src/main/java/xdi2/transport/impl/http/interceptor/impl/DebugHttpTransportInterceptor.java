package xdi2.transport.impl.http.interceptor.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.plugins.PluginsLoader;
import xdi2.core.properties.XDI2Properties;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.parser.ParserRegistry;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.factory.impl.uri.UriMessagingContainerFactory;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.messaging.container.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.container.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.transport.Transport;
import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.impl.http.interceptor.HttpTransportInterceptor;
import xdi2.transport.interceptor.TransportInterceptor;
import xdi2.transport.registry.impl.uri.UriMessagingContainerFactoryMount;
import xdi2.transport.registry.impl.uri.UriMessagingContainerMount;

/**
 * This interceptor prints out a list of mounted messaging containers.
 * This can be used for debugging purposes with a standard web browser.
 * 
 * @author markus
 */
public class DebugHttpTransportInterceptor extends AbstractInterceptor<Transport<?, ?>> implements ApplicationContextAware, TransportInterceptor, HttpTransportInterceptor {

	public static final String DEFAULT_PATH = "/";
	public static final int DEFAULT_LOG_CAPACITY = 10;

	private String path = DEFAULT_PATH;
	private int logCapacity;

	private ApplicationContext applicationContext;
	private LinkedList<LogEntry> log;

	public DebugHttpTransportInterceptor() {

		this.path = DEFAULT_PATH;
		this.logCapacity = DEFAULT_LOG_CAPACITY;

		this.applicationContext = null;
		this.log = new LinkedList<LogEntry> ();
	}

	/*
	 * TransportInterceptor
	 */

	@Override
	public boolean before(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2TransportException {

		Date start = new Date();
		putStart(executionContext, start);

		return false;
	}

	@Override
	public boolean after(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, ExecutionContext executionContext) throws Xdi2TransportException {

		Date start = getStart(executionContext);
		long stop = System.currentTimeMillis();
		long duration = start == null ? -1 : stop - start.getTime();

		this.getLog().addFirst(new LogEntry(start, duration, transport, request, response, messagingContainer, messageEnvelope, messagingResponse, executionContext, null));
		if (this.getLog().size() > this.getLogCapacity()) this.getLog().removeLast();

		return false;
	}

	@Override
	public void exception(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, Exception ex, ExecutionContext executionContext) {

		Date start = getStart(executionContext);
		long stop = System.currentTimeMillis();
		long duration = start == null ? -1 : stop - start.getTime();

		this.getLog().addFirst(new LogEntry(start, duration, transport, request, response, messagingContainer, messageEnvelope, messagingResponse, executionContext, ex));
		if (this.getLog().size() > this.getLogCapacity()) this.getLog().removeLast();
	}

	/*
	 * HttpTransportInterceptor
	 */

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpTransportRequest httpTransportRequest, HttpTransportResponse httpTransportResponse, UriMessagingContainerMount messagingContainerMount) throws Xdi2TransportException, IOException {

		if (! httpTransportRequest.getRequestPath().equals(this.getPath())) return false;

		String cmd = httpTransportRequest.getParameter("cmd");
		String cmdMessagingContainerPath = httpTransportRequest.getParameter("messagingcontainerpath");
		String cmdMessagingContainerFactoryPath = httpTransportRequest.getParameter("messagingcontainerfactorypath");
		String format = httpTransportRequest.getParameter("format");
		String writeImplied = httpTransportRequest.getParameter("writeImplied");
		String writeOrdered = httpTransportRequest.getParameter("writeOrdered");
		String writePretty = httpTransportRequest.getParameter("writePretty");
		String graphstring = httpTransportRequest.getParameter("graphstring");

		if ("unmount_messaging_container".equals(cmd) && cmdMessagingContainerPath != null) {

			MessagingContainer cmdMessagingContainer = httpTransport.getUriMessagingContainerRegistry().getMessagingContainer(cmdMessagingContainerPath);
			if (cmdMessagingContainer != null) httpTransport.getUriMessagingContainerRegistry().unmountMessagingContainer(cmdMessagingContainer);

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		if ("unmount_messaging_container_factory".equals(cmd) && cmdMessagingContainerFactoryPath != null) {

			UriMessagingContainerFactory cmdMessagingContainerFactory = httpTransport.getUriMessagingContainerRegistry().getMessagingContainerFactory(cmdMessagingContainerFactoryPath);
			if (cmdMessagingContainerFactory != null) httpTransport.getUriMessagingContainerRegistry().unmountMessagingContainerFactory(cmdMessagingContainerFactory);

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		if ("edit_messaging_container".equals(cmd) && cmdMessagingContainerPath != null) {

			MessagingContainer cmdMessagingContainer = httpTransport.getUriMessagingContainerRegistry().getMessagingContainer(cmdMessagingContainerPath);

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writeImplied = null;
				writeOrdered = null;
				writePretty = null;
			}

			Properties xdiWriterParameters = new Properties();

			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

			// write graph

			Graph graph = ((GraphMessagingContainer) cmdMessagingContainer).getGraph();

			XDIWriter xdiWriter = XDIWriterRegistry.forFormat(format, xdiWriterParameters);
			StringWriter xdiStringWriter = new StringWriter();
			xdiWriter.write(graph, xdiStringWriter);
			graphstring = xdiStringWriter.getBuffer().toString();

			String statementcount = Long.toString(graph.getRootContextNode().getAllStatementCount());

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("httprequest", httpTransportRequest);
			context.put("messagingcontainer", cmdMessagingContainer);
			context.put("messagingcontainerpath", cmdMessagingContainerPath);
			context.put("format", format);
			context.put("writeImplied", writeImplied);
			context.put("writeOrdered", writeOrdered);
			context.put("writePretty", writePretty);
			context.put("graphstring", graphstring);
			context.put("statementcount", statementcount);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			StringWriter stringWriter = new StringWriter();
			makeVelocityEngine().evaluate(context, stringWriter, "debug-edit.vm", reader);

			httpTransportResponse.setStatus(HttpServletResponse.SC_OK);
			httpTransportResponse.setContentType("text/html");
			httpTransportResponse.writeBody(stringWriter.getBuffer().toString(), true);

			// done

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		if ("msg_messaging_container".equals(cmd) && cmdMessagingContainerPath != null) {

			MessagingContainer cmdMessagingContainer = httpTransport.getUriMessagingContainerRegistry().getMessagingContainer(cmdMessagingContainerPath);

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writeImplied = null;
				writeOrdered = null;
				writePretty = null;
			}

			Properties xdiWriterParameters = new Properties();

			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

			// write message envelope

			XDIArc ownerPeerRootXDIArc = cmdMessagingContainer.getOwnerPeerRootXDIArc();

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.createMessage(XDIMessagingConstants.XDI_ADD_ANONYMOUS);
			if (ownerPeerRootXDIArc != null) message.setToPeerRootXDIArc(ownerPeerRootXDIArc);
			message.createGetOperation(XDIConstants.XDI_ADD_ROOT);

			Graph graph = messageEnvelope.getGraph();

			XDIWriter xdiWriter = XDIWriterRegistry.forFormat(format, xdiWriterParameters);
			StringWriter xdiStringWriter = new StringWriter();
			xdiWriter.write(graph, xdiStringWriter);
			graphstring = xdiStringWriter.getBuffer().toString();

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("httprequest", httpTransportRequest);
			context.put("messagingcontainer", cmdMessagingContainer);
			context.put("messagingcontainerpath", cmdMessagingContainerPath);
			context.put("graphstring", graphstring);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-msg.vm"));
			StringWriter stringWriter = new StringWriter();
			makeVelocityEngine().evaluate(context, stringWriter, "debug-msg.vm", reader);

			httpTransportResponse.setStatus(HttpServletResponse.SC_OK);
			httpTransportResponse.setContentType("text/html");
			httpTransportResponse.writeBody(stringWriter.getBuffer().toString(), true);

			// done

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		if ("save_messaging_container".equals(cmd) && cmdMessagingContainerPath != null) {

			MessagingContainer cmdMessagingContainer = httpTransport.getUriMessagingContainerRegistry().getMessagingContainer(cmdMessagingContainerPath);

			// parse graph

			Graph graph = ((GraphMessagingContainer) cmdMessagingContainer).getGraph();

			XDIReader xdiReader = XDIReaderRegistry.getAuto();

			String error = null;

			try {

				graph.clear();
				xdiReader.read(graph, new StringReader(graphstring));
			} catch (Xdi2ParseException ex) {

				error = ex.getMessage();
			}

			String statementcount = Long.toString(graph.getRootContextNode().getAllStatementCount());

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("httprequest", httpTransportRequest);
			context.put("messagingcontainer", cmdMessagingContainer);
			context.put("messagingcontainerpath", cmdMessagingContainerPath);
			context.put("format", format);
			context.put("writeImplied", writeImplied);
			context.put("writeOrdered", writeOrdered);
			context.put("writePretty", writePretty);
			context.put("graphstring", graphstring);
			context.put("statementcount", statementcount);
			context.put("error", error);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			StringWriter stringWriter = new StringWriter();
			makeVelocityEngine().evaluate(context, stringWriter, "debug-edit.vm", reader);

			httpTransportResponse.setStatus(HttpServletResponse.SC_OK);
			httpTransportResponse.setContentType("text/html");
			httpTransportResponse.writeBody(stringWriter.getBuffer().toString(), true);

			// done

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		if ("exec_messaging_container".equals(cmd) && cmdMessagingContainerPath != null) {

			MessagingContainer cmdMessagingContainer = httpTransport.getUriMessagingContainerRegistry().getMessagingContainer(cmdMessagingContainerPath);

			// parse and execute message envelope

			MessageEnvelope messageEnvelope = new MessageEnvelope();

			XDIReader xdiReader = XDIReaderRegistry.getAuto();

			ExecutionContext executionContext = null;
			ExecutionResult executionResult = null;

			String error = null;
			String resultstring = null;

			try {

				xdiReader.read(messageEnvelope.getGraph(), new StringReader(graphstring));

				if (cmdMessagingContainer instanceof AbstractMessagingContainer) {

					LinkContractInterceptor linkContractInterceptor = ((AbstractMessagingContainer) cmdMessagingContainer).getInterceptors().getInterceptor(LinkContractInterceptor.class);
					if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessageEnvelope(messageEnvelope);
				}

				executionContext = ExecutionContext.createExecutionContext();
				executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

				cmdMessagingContainer.execute(messageEnvelope, executionContext, executionResult);
			} catch (Xdi2Exception ex) {

				error = ex.getMessage();
			}

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writeImplied = null;
				writeOrdered = null;
				writePretty = null;
			}

			Properties xdiWriterParameters = new Properties();

			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
			xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

			// write result graph

			if (executionResult != null) {

				Graph resultGraph = executionResult.makeLightMessagingResponse().getResultGraph();

				XDIWriter xdiWriter = XDIWriterRegistry.forFormat(format, xdiWriterParameters);
				StringWriter stringWriter = new StringWriter();
				xdiWriter.write(resultGraph, stringWriter);
				resultstring = stringWriter.getBuffer().toString();
			} else {

				resultstring = "No execution result.";
			}

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("parser", ParserRegistry.getInstance().getParser());
			context.put("httptransport", httpTransport);
			context.put("httprequest", httpTransportRequest);
			context.put("messagingcontainer", cmdMessagingContainer);
			context.put("messagingcontainerpath", cmdMessagingContainerPath);
			context.put("graphstring", graphstring);
			context.put("resultstring", resultstring);
			context.put("error", error);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-msg.vm"));
			StringWriter stringWriter = new StringWriter();
			makeVelocityEngine().evaluate(context, stringWriter, "debug-msg.vm", reader);

			httpTransportResponse.setStatus(HttpServletResponse.SC_OK);
			httpTransportResponse.setContentType("text/html");
			httpTransportResponse.writeBody(stringWriter.getBuffer().toString(), true);

			// done

			return this.processGetRequest(httpTransport, httpTransportRequest, httpTransportResponse, messagingContainerMount);
		}

		// done

		return false;
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, UriMessagingContainerMount messagingContainerMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().equals(this.getPath())) return false;

		// prepare velocity

		File[] pluginFiles = PluginsLoader.getFiles();
		Properties xdi2Properties = XDI2Properties.properties;
		Properties systemProperties = System.getProperties();
		List<UriMessagingContainerMount> messagingContainerMounts = httpTransport.getUriMessagingContainerRegistry().getMessagingContainerMounts();
		List<UriMessagingContainerFactoryMount> messagingContainerFactoryMounts = httpTransport.getUriMessagingContainerRegistry().getMessagingContainerFactoryMounts();
		Collection<?> transports = this.getApplicationContext().getBeansOfType(Transport.class).values();

		VelocityContext context = new VelocityContext();
		context.put("parser", ParserRegistry.getInstance().getParser());
		context.put("httptransport", httpTransport);
		context.put("httprequest", request);
		context.put("pluginfiles", pluginFiles);
		context.put("xdi2properties", xdi2Properties);
		context.put("systemproperties", systemProperties);
		context.put("messagingcontainermounts", messagingContainerMounts);
		context.put("messagingcontainerfactorymounts", messagingContainerFactoryMounts);
		context.put("transports", transports);
		context.put("log", this.getLog());

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug.vm"));
		StringWriter stringWriter = new StringWriter();
		makeVelocityEngine().evaluate(context, stringWriter, "debug.vm", reader);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		response.writeBody(stringWriter.getBuffer().toString(), true);

		// done

		return true;
	}

	@Override
	public boolean processPutRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, UriMessagingContainerMount messagingContainerMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, UriMessagingContainerMount messagingContainerMount) throws Xdi2TransportException, IOException {

		return false;
	}

	/*
	 * Getters and setters
	 */

	public String getPath() {

		return this.path;
	}

	public void setPath(String path) {

		this.path = path;
	}

	public int getLogCapacity() {

		return this.logCapacity;
	}

	public void setLogCapacity(int logCapacity) {

		this.logCapacity = logCapacity;
	}

	public ApplicationContext getApplicationContext() {

		return this.applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
	}

	public LinkedList<LogEntry> getLog() {

		return this.log;
	}

	public void setLog(LinkedList<LogEntry> log) {

		this.log = log;
	}

	/*
	 * Helper methods
	 */

	private static VelocityEngine makeVelocityEngine() {

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		velocityEngine.setProperty(RuntimeConstants.PARSER_POOL_SIZE, Integer.valueOf(1));
		velocityEngine.init();

		return velocityEngine;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_START_PER_EXECUTIONCONTEXT = DebugHttpTransportInterceptor.class.getCanonicalName() + "#startperexecutioncontext";

	public static Date getStart(ExecutionContext executionContext) {

		return (Date) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_START_PER_EXECUTIONCONTEXT);
	}

	public static void putStart(ExecutionContext executionContext, Date start) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_START_PER_EXECUTIONCONTEXT, start);
	}

	/*
	 * Helper classes
	 */

	public static class LogEntry {

		private Date start;
		private long duration;
		private Transport<?, ?> transport;
		private TransportRequest request;
		private TransportResponse response;
		private MessagingContainer messagingContainer;
		private MessageEnvelope messageEnvelope;
		private MessagingResponse messagingResponse;
		private ExecutionContext executionContext;
		private Exception ex;

		public LogEntry(Date start, long duration, Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, ExecutionContext executionContext, Exception ex) {

			this.start = start;
			this.duration = duration;
			this.transport = transport;
			this.request = request;
			this.response = response;
			this.messagingContainer = messagingContainer;
			this.messageEnvelope = messageEnvelope;
			this.messagingResponse = messagingResponse;
			this.executionContext = executionContext;
			this.ex = ex;
		}

		public Date getStart() {

			return this.start;
		}

		public void setStart(Date start) {

			this.start = start;
		}

		public long getDuration() {

			return this.duration;
		}

		public void setDuration(long duration) {

			this.duration = duration;
		}

		public Transport<?, ?> getTransport() {

			return this.transport;
		}

		public void setTransport(Transport<?, ?> transport) {

			this.transport = transport;
		}

		public TransportRequest getRequest() {

			return this.request;
		}

		public void setRequest(TransportRequest request) {

			this.request = request;
		}

		public TransportResponse getResponse() {

			return this.response;
		}

		public void setResponse(TransportResponse response) {

			this.response = response;
		}

		public MessagingContainer getMessagingContainer() {

			return this.messagingContainer;
		}

		public void setMessagingContainer(MessagingContainer messagingContainer) {

			this.messagingContainer = messagingContainer;
		}

		public MessageEnvelope getMessageEnvelope() {

			return this.messageEnvelope;
		}

		public void setMessageEnvelope(MessageEnvelope messageEnvelope) {

			this.messageEnvelope = messageEnvelope;
		}

		public MessagingResponse getMessagingResponse() {

			return this.messagingResponse;
		}

		public void setMessagingResponse(MessagingResponse messagingResponse) {

			this.messagingResponse = messagingResponse;
		}

		public ExecutionContext getExecutionContext() {

			return this.executionContext;
		}

		public void setExecutionContext(ExecutionContext executionContext) {

			this.executionContext = executionContext;
		}

		public Exception getEx() {

			return this.ex;
		}

		public void setEx(Exception ex) {

			this.ex = ex;
		}
	}
}
