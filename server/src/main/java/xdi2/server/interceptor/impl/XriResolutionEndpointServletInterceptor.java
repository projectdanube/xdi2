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
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.roots.RemoteRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
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

		XDI3SubSegment query = parseQuery(requestInfo);
		XDI3Segment providerid = getProviderId(this.getRegistryGraph());
		XDI3Segment[] provideridSynonyms = getProviderIdSynonyms(this.getRegistryGraph(), providerid);

		// look into registry

		RemoteRoot remoteRoot = Roots.findLocalRoot(this.getRegistryGraph()).findRemoteRoot(XDI3Segment.create("" + providerid + query), false);

		if (remoteRoot == null) {

			for (XDI3Segment provideridSynonym : provideridSynonyms) {

				remoteRoot = Roots.findLocalRoot(this.getRegistryGraph()).findRemoteRoot(XDI3Segment.create("" + provideridSynonym + query), false);
				if (remoteRoot != null) break;
			}
		}

		if (remoteRoot == null) {

			log.warn("Remote root for " + query + " not found in the registry graph. Ignoring.");
			this.sendNotFoundXrd(endpointServlet, requestInfo, query, response);
			return true;
		}

		if (remoteRoot.isSelfRemoteRoot()) {

			log.warn("Remote root for " + query + " is the owner of the registry graph. Ignoring.");
			this.sendNotFoundXrd(endpointServlet, requestInfo, query, response);
			return true;
		}

		ContextNode referenceRemoteRootContextNode = Equivalence.getReferenceContextNode(remoteRoot.getContextNode());
		RemoteRoot referenceRemoteRoot = (referenceRemoteRootContextNode == null) ? null : RemoteRoot.fromContextNode(referenceRemoteRootContextNode);
		if (referenceRemoteRoot == null) referenceRemoteRoot = remoteRoot;

		XDI3Segment canonicalid = referenceRemoteRoot.getXriOfRemoteRoot();
		XDI3SubSegment localid = query;
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

	private static XDI3SubSegment parseQuery(RequestInfo requestInfo) {

		String query = requestInfo.getRequestPath();
		if (query.endsWith("/")) query = query.substring(0, query.length() - 1);
		query = query.substring(query.lastIndexOf('/') + 1);

		if (query.isEmpty()) return null;

		return XDI3SubSegment.create(query);
	}

	private static String constructUri(RequestInfo requestInfo, String targetPath, XDI3Segment canonicalid) {

		String uri = requestInfo.getUri().substring(0, requestInfo.getUri().length() - requestInfo.getRequestPath().length());
		uri += targetPath + "/" + canonicalid.toString();

		return uri;
	}

	private static XDI3Segment getProviderId(Graph graph) {

		RemoteRoot selfRemoteRoot = Roots.findLocalRoot(graph).getSelfRemoteRoot();
		if (selfRemoteRoot == null) return null;

		return selfRemoteRoot.getXriOfRemoteRoot();
	}

	private static XDI3Segment[] getProviderIdSynonyms(Graph graph, XDI3Segment providerid) {

		RemoteRoot selfRemoteRoot = Roots.findLocalRoot(graph).getSelfRemoteRoot();
		if (selfRemoteRoot == null) return new XDI3Segment[0];

		Iterator<ContextNode> selfRemoteRootIncomingReferenceContextNodes = Equivalence.getIncomingReferenceContextNodes(selfRemoteRoot.getContextNode());

		XDI3Segment[] selfSynonyms = new IteratorArrayMaker<XDI3Segment> (new MappingContextNodeXriIterator(selfRemoteRootIncomingReferenceContextNodes)).array(XDI3Segment.class);
		for (int i=0; i<selfSynonyms.length; i++) selfSynonyms[i] = RemoteRoot.getXriOfRemoteRootXri(selfSynonyms[i].getLastSubSegment());

		return selfSynonyms;
	}

	/*	private static XDI3Segment getCanonicalId(Graph graph, XDI3Segment providerid, XDI3SubSegment localid) {

		XDI3Segment canonicalid = XDI3Segment.create("" + providerid + localid);

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

	private void sendNotFoundXrd(EndpointServlet endpointServlet, RequestInfo requestInfo, XDI3SubSegment query, HttpServletResponse response) throws IOException {

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
