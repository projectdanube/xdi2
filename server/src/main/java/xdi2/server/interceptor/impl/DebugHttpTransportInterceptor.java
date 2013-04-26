package xdi2.server.interceptor.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
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
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;
import xdi2.server.interceptor.AbstractHttpTransportInterceptor;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

/**
 * This interceptor prints out a list of mounted messaging targets.
 * This can be used for debugging purposes with a standard web browser.
 * 
 * @author markus
 */
public class DebugHttpTransportInterceptor extends AbstractHttpTransportInterceptor {

	private VelocityEngine velocityEngine;

	@Override
	public void init(HttpTransport httpTransport) {

		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

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

			httpTransport.getHttpEndpointRegistry().reload();

			return this.processGetRequest(httpTransport, request, response, messagingTarget);
		}

		if ("unmount_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);
			if (cmdMessagingTarget != null) httpTransport.getHttpEndpointRegistry().unmountMessagingTarget(cmdMessagingTarget);

			return this.processGetRequest(httpTransport, request, response, messagingTarget);
		}

		if ("unmount_messaging_target_factory".equals(cmd) && cmdMessagingTargetFactoryPath != null) {

			MessagingTargetFactory cmdMessagingTargetFactory = httpTransport.getHttpEndpointRegistry().getMessagingTargetFactory(cmdMessagingTargetFactoryPath);
			if (cmdMessagingTargetFactory != null) httpTransport.getHttpEndpointRegistry().unmountMessagingTargetFactory(cmdMessagingTargetFactory);

			return this.processGetRequest(httpTransport, request, response, messagingTarget);
		}

		if ("edit_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writeImplied = null;
				writeOrdered = "on";
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

			return this.processGetRequest(httpTransport, request, response, messagingTarget);
		}

		if ("save_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = httpTransport.getHttpEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);

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

			return this.processGetRequest(httpTransport, request, response, messagingTarget);
		}

		// done

		return false;
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		if (! request.getRequestPath().equals("/")) return false;

		// prepare velocity

		File[] pluginFiles = PluginsLoader.getFiles();
		List<MessagingTarget> messagingTargets = httpTransport.getHttpEndpointRegistry().getMessagingTargets();
		Map<String, MessagingTarget> messagingTargetsByPath = httpTransport.getHttpEndpointRegistry().getMessagingTargetsByPath();
		List<MessagingTargetFactory> messagingTargetFactorys = httpTransport.getHttpEndpointRegistry().getMessagingTargetFactorys();
		Map<String, MessagingTargetFactory> messagingTargetFactorysByPath = httpTransport.getHttpEndpointRegistry().getMessagingTargetFactorysByPath();

		VelocityContext context = new VelocityContext();
		context.put("parser", XDI3ParserRegistry.getInstance().getParser());
		context.put("httptransport", httpTransport);
		context.put("request", request);
		context.put("pluginfiles", pluginFiles);
		context.put("messagingtargets", messagingTargets);
		context.put("messagingtargetsbypath", messagingTargetsByPath);
		context.put("messagingtargetfactorys", messagingTargetFactorys);
		context.put("messagingtargetfactorysbypath", messagingTargetFactorysByPath);

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
}
