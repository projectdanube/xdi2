package xdi2.server.interceptor.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;
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
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! requestInfo.getRequestPath().equals("/")) return false;

		// prepare velocity

		List<MessagingTarget> messagingTargets = endpointServlet.getEndpointRegistry().getMessagingTargets();
		Map<String, MessagingTarget> messagingTargetsByPath = endpointServlet.getEndpointRegistry().getMessagingTargetsByPath();
		List<MessagingTargetFactory> messagingTargetFactorys = endpointServlet.getEndpointRegistry().getMessagingTargetFactorys();
		Map<String, MessagingTargetFactory> messagingTargetFactorysByPath = endpointServlet.getEndpointRegistry().getMessagingTargetFactorysByPath();

		VelocityContext context = new VelocityContext();
		context.put("endpointservlet", endpointServlet);
		context.put("requestinfo", requestInfo);
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
