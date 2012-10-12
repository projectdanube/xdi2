package xdi2.server.interceptor.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;

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
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXrisIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
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

		// prepare resolution information

		XRI3SubSegment query = parseQuery(requestInfo);
		XRI3Segment providerid = getProviderId(this.getRegistryGraph());
		XRI3Segment[] provideridSynonyms = getProviderIdSynonyms(this.getRegistryGraph(), providerid);

		// look into registry

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), new XRI3Segment("" + providerid + query), false);

		if (remoteRootContextNode == null) {

			for (XRI3Segment provideridSynonym : provideridSynonyms) {

				remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), new XRI3Segment("" + provideridSynonym + query), false);
				if (remoteRootContextNode != null) break;
			}
		}

		if (remoteRootContextNode == null) {

			log.warn("Remote root context node for " + query + " not found in the registry graph. Ignoring.");
			this.sendNotFoundXrd(endpointServlet, requestInfo, query, response);
			return true;
		}

		if (RemoteRoots.isSelfRemoteRootContextNode(remoteRootContextNode)) {

			log.warn("Remote root context node for " + query + " is the owner of the registry graph. Ignoring.");
			this.sendNotFoundXrd(endpointServlet, requestInfo, query, response);
			return true;
		}

		ContextNode canonicalRemoteRootContextNode = Dictionary.getCanonicalContextNode(remoteRootContextNode);
		if (canonicalRemoteRootContextNode == null) canonicalRemoteRootContextNode = remoteRootContextNode;

		XRI3Segment canonicalid = RemoteRoots.xriOfRemoteRootXri(canonicalRemoteRootContextNode.getXri());
		XRI3SubSegment localid = query;
		String uri = constructUri(requestInfo, this.getTargetPath(), canonicalid);

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

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrds-ok.vm"));
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/xrd+xml");
		this.velocityEngine.evaluate(context, writer, "xrds-ok.vm", reader);
		writer.close();

		// done

		return true;
	}

	/*
	 * Helper methods
	 */

	private static XRI3SubSegment parseQuery(RequestInfo requestInfo) {

		String query = requestInfo.getRequestPath();
		if (query.endsWith("/")) query = query.substring(0, query.length() - 1);
		query = query.substring(query.lastIndexOf('/') + 1);

		if (query.isEmpty()) return null;

		return new XRI3SubSegment(query);
	}

	private static String constructUri(RequestInfo requestInfo, String targetPath, XRI3Segment canonicalid) {

		String uri = requestInfo.getUri().substring(0, requestInfo.getUri().length() - requestInfo.getRequestPath().length());
		uri += targetPath + "/" + canonicalid.toString();

		return uri;
	}

	private static XRI3Segment getProviderId(Graph graph) {

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);
		if (selfRemoteRootContextNode == null) return null;

		return RemoteRoots.xriOfRemoteRootXri(selfRemoteRootContextNode.getXri());
	}

	private static XRI3Segment[] getProviderIdSynonyms(Graph graph, XRI3Segment providerid) {

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);
		if (selfRemoteRootContextNode == null) return new XRI3Segment[0];

		Iterator<ContextNode> selfSynonymRemoteRootContextNodes = Dictionary.getSynonymContextNodes(selfRemoteRootContextNode);

		XRI3Segment[] selfSynonyms = new IteratorArrayMaker<XRI3Segment> (new MappingContextNodeXrisIterator(selfSynonymRemoteRootContextNodes)).array(XRI3Segment.class);
		for (int i=0; i<selfSynonyms.length; i++) selfSynonyms[i] = RemoteRoots.xriOfRemoteRootXri(selfSynonyms[i]);

		return selfSynonyms;
	}

	/*	private static XRI3Segment getCanonicalId(Graph graph, XRI3Segment providerid, XRI3SubSegment localid) {

		XRI3Segment canonicalid = new XRI3Segment("" + providerid + localid);

		ContextNode canonicalidRemoteRootContextNode = RemoteRoots.findRemoteRootContextNode(graph, canonicalid, false);

		if (canonicalidRemoteRootContextNode != null) {

			while (true) {

				Relation canonicalidRelation = canonicalidRemoteRootContextNode.getRelation(XDIDictionaryConstants.XRI_S_IS);
				if (canonicalidRelation == null) break;

				canonicalidRemoteRootContextNode = canonicalidRelation.follow();
			}

			canonicalid = RemoteRoots.xriOfRemoteRootXri(canonicalidRemoteRootContextNode.getXri());
		}

		return canonicalid;
	}*/

	private void sendNotFoundXrd(EndpointServlet endpointServlet, RequestInfo requestInfo, XRI3SubSegment query, HttpServletResponse response) throws IOException {

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("endpointservlet", endpointServlet);
		context.put("requestinfo", requestInfo);
		context.put("query", query);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrds-notfound.vm"));
		PrintWriter writer = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/xrd+xml");
		this.velocityEngine.evaluate(context, writer, "xrd.vm", reader);
		writer.close();
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
