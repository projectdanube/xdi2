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
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;
import xdi2.server.interceptor.AbstractEndpointServletInterceptor;

/**
 * This interceptor can act as a lightweight XRI resolution server based on a "registry graph".
 * 
 * @author markus
 */
public class XriResolutionEndpointServletInterceptor extends AbstractEndpointServletInterceptor {

	private static final Logger log = LoggerFactory.getLogger(XriResolutionEndpointServletInterceptor.class);

	private String resolvePath;
	private String targetPath;
	private Graph registryGraph;

	private VelocityEngine velocityEngine;

	@Override
	public void init(EndpointServlet endpointServlet) {

		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}

	@Override
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! requestInfo.getRequestPath().startsWith(this.getResolvePath())) return false;

		// prepare velocity values

		String query = parseQuery(requestInfo);
		String providerid = getProviderId(this.getRegistryGraph());
		String localid = query;
		String canonicalid = providerid + localid;
		String uri = constructUri(requestInfo, this.getTargetPath(), query);

		// look into registry

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), new XRI3Segment(query), false);
		if (remoteRootContextNode == null) {

			log.warn("Remote root context node for " + query + " not found in the registry graph. Ignoring.");
			return false;
		}

		ContextNode selfRemoteContextNode = RemoteRoots.getSelfRemoteRootContextNode(this.getRegistryGraph());
		if (remoteRootContextNode.equals(selfRemoteContextNode)) {

			log.warn("Remote root context node for " + query + " is the owner of the registry graph. Ignoring.");
			return false;
		}

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("endpointservlet", endpointServlet);
		context.put("requestinfo", requestInfo);
		context.put("query", query);
		context.put("providerid", providerid);
		context.put("localid", localid);
		context.put("canonicalid", canonicalid);
		context.put("uri", uri);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrd.vm"));
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/xrd+xml");
		this.velocityEngine.evaluate(context, writer, "xrd.vm", reader);
		writer.close();

		// done

		return true;
	}

	/*
	 * Helper methods
	 */

	private static String parseQuery(RequestInfo requestInfo) {

		String query = requestInfo.getRequestPath();
		if (query.endsWith("/")) query = query.substring(0, query.length() - 1);
		query = query.substring(query.lastIndexOf('/') + 1);

		if (query.isEmpty()) return null;

		return query;
	}

	private static String constructUri(RequestInfo requestInfo, String targetPath, String query) {

		String uri = requestInfo.getUri().substring(0, requestInfo.getUri().length() - requestInfo.getRequestPath().length());
		uri += targetPath + "/" + query;

		return uri;
	}

	private static String getProviderId(Graph graph) {

		ContextNode contextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);
		if (contextNode == null) return null;

		return RemoteRoots.xriOfRemoteRootXri(contextNode.getXri()).toString();
	}

	/*
	 * Getters and setters
	 */

	public String getResolvePath() {

		return this.resolvePath;
	}

	public void setResolvePath(String resolvePath) {

		this.resolvePath = resolvePath;
	}

	public String getTargetPath() {

		return this.targetPath;
	}

	public void setTargetPath(String targetPath) {

		this.targetPath = targetPath;
	}

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
