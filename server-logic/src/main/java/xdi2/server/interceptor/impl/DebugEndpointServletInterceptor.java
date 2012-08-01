package xdi2.server.interceptor.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointRegistry;
import xdi2.server.EndpointServlet;
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
		this.velocityEngine.init();
	}

	@Override
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! path.isEmpty()) return false;

		EndpointRegistry endpointRegistry = endpointServlet.getEndpointRegistry();

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("path", path);
		context.put("messagingtargets", endpointRegistry.getMessagingTargets());
		context.put("messagingtargetsbypath", endpointRegistry.getMessagingTargetsByPath().entrySet());

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("debug.vm"));
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);
		this.velocityEngine.evaluate(context, writer, "debug.vm", reader);
		writer.close();

		// done

		return true;
	}
}
