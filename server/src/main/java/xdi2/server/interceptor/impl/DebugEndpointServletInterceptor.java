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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;
import xdi2.server.interceptor.AbstractEndpointServletInterceptor;

/**
 * This interceptor prints out a list of mounted messaging targets.
 * This can be used for debugging purposes with a standard web browser.
 * 
 * @author markus
 */
public class DebugEndpointServletInterceptor extends AbstractEndpointServletInterceptor {

	private VelocityEngine velocityEngine;

	@Override
	public void init(EndpointServlet endpointServlet) {

		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}


	@Override
	public boolean processPostRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! requestInfo.getRequestPath().equals("/")) return false;

		String cmd = request.getParameter("cmd");
		String cmdMessagingTargetPath = request.getParameter("messagingtargetpath");
		String cmdMessagingTargetFactoryPath = request.getParameter("messagingtargetfactorypath");
		String format = request.getParameter("format");
		String writecontexts = request.getParameter("writecontexts");
		String writeordered = request.getParameter("writeordered");
		String writepretty = request.getParameter("writepretty");
		String graphstring = request.getParameter("graphstring");

		if ("reload".equals(cmd)) {

			try {

				endpointServlet.getEndpointRegistry().reload();
			} catch (Xdi2ServerException ex) {

				throw new ServletException(ex.getMessage(), ex);
			}
		}

		if ("unmount_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = endpointServlet.getEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);
			if (cmdMessagingTarget != null) endpointServlet.getEndpointRegistry().unmountMessagingTarget(cmdMessagingTarget);
		}

		if ("unmount_messaging_target_factory".equals(cmd) && cmdMessagingTargetFactoryPath != null) {

			MessagingTargetFactory cmdMessagingTargetFactory = endpointServlet.getEndpointRegistry().getMessagingTargetFactory(cmdMessagingTargetFactoryPath);
			if (cmdMessagingTargetFactory != null) endpointServlet.getEndpointRegistry().unmountMessagingTargetFactory(cmdMessagingTargetFactory);
		}

		if ("edit_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = endpointServlet.getEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);

			// prepare format and parameters

			if (format == null) {

				format = XDIDisplayWriter.FORMAT_NAME;
				writecontexts = null;
				writeordered = "on";
				writepretty = null;
			}

			Properties xdiWriterParameters = new Properties();

			if ("on".equals(writecontexts)) xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, "1");
			if ("on".equals(writeordered)) xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
			if ("on".equals(writepretty)) xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "1");

			XDIWriter xdiWriter = XDIWriterRegistry.forFormat(format, xdiWriterParameters);

			StringWriter stringWriter = new StringWriter();

			xdiWriter.write(((GraphMessagingTarget) cmdMessagingTarget).getGraph(), stringWriter);

			graphstring = stringWriter.getBuffer().toString();

			// prepare velocity

			VelocityContext context = new VelocityContext();
			context.put("endpointservlet", endpointServlet);
			context.put("requestinfo", requestInfo);
			context.put("messagingtarget", cmdMessagingTarget);
			context.put("messagingtargetpath", cmdMessagingTargetPath);
			context.put("format", format);
			context.put("writecontexts", writecontexts);
			context.put("writeordered", writeordered);
			context.put("writepretty", writepretty);
			context.put("graphstring", graphstring);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			PrintWriter writer = response.getWriter();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			this.velocityEngine.evaluate(context, writer, "debug-edit.vm", reader);
			writer.close();

			// done

			return true;
		}

		if ("save_messaging_target".equals(cmd) && cmdMessagingTargetPath != null) {

			MessagingTarget cmdMessagingTarget = endpointServlet.getEndpointRegistry().getMessagingTarget(cmdMessagingTargetPath);

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
			context.put("endpointservlet", endpointServlet);
			context.put("requestinfo", requestInfo);
			context.put("messagingtarget", cmdMessagingTarget);
			context.put("messagingtargetpath", cmdMessagingTargetPath);
			context.put("format", format);
			context.put("writecontexts", writecontexts);
			context.put("writeordered", writeordered);
			context.put("writepretty", writepretty);
			context.put("graphstring", graphstring);
			context.put("error", error);

			// send response

			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug-edit.vm"));
			PrintWriter writer = response.getWriter();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			this.velocityEngine.evaluate(context, writer, "debug-edit.vm", reader);
			writer.close();

			// done

			return true;
		}

		return this.processGetRequest(endpointServlet, request, response, requestInfo, messagingTarget);
	}

	@Override
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! requestInfo.getRequestPath().equals("/")) return false;

		// prepare velocity

		File[] pluginFiles = PluginsLoader.getFiles();
		List<MessagingTarget> messagingTargets = endpointServlet.getEndpointRegistry().getMessagingTargets();
		Map<String, MessagingTarget> messagingTargetsByPath = endpointServlet.getEndpointRegistry().getMessagingTargetsByPath();
		List<MessagingTargetFactory> messagingTargetFactorys = endpointServlet.getEndpointRegistry().getMessagingTargetFactorys();
		Map<String, MessagingTargetFactory> messagingTargetFactorysByPath = endpointServlet.getEndpointRegistry().getMessagingTargetFactorysByPath();

		VelocityContext context = new VelocityContext();
		context.put("endpointservlet", endpointServlet);
		context.put("requestinfo", requestInfo);
		context.put("pluginfiles", pluginFiles);
		context.put("messagingtargets", messagingTargets);
		context.put("messagingtargetsbypath", messagingTargetsByPath);
		context.put("messagingtargetfactorys", messagingTargetFactorys);
		context.put("messagingtargetfactorysbypath", messagingTargetFactorysByPath);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug.vm"));
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html");
		this.velocityEngine.evaluate(context, writer, "debug.vm", reader);
		writer.close();

		// done

		return true;
	}
}
