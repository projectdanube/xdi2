package xdi2.server.interceptor.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.roots.PeerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.interceptor.AbstractHttpTransportInterceptor;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

/**
 * This interceptor can act as a lightweight XRI resolution server based on a "registry graph".
 * 
 * @author markus
 */
public class XriResolutionHttpTransportInterceptor extends AbstractHttpTransportInterceptor {

	private static final Logger log = LoggerFactory.getLogger(XriResolutionHttpTransportInterceptor.class);

	private String resolvePath;
	private String targetPath;
	private Graph registryGraph;

	private VelocityEngine velocityEngine;

	@Override
	public void init(HttpTransport httpTransport) {

		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		if (! request.getRequestPath().startsWith(this.getResolvePath())) return false;

		// prepare resolution information

		XDI3SubSegment query = parseQuery(request);
		XDI3Segment providerid = getProviderId(this.getRegistryGraph());
		XDI3Segment[] provideridSynonyms = getProviderIdSynonyms(this.getRegistryGraph(), providerid);

		// look into registry

		PeerRoot peerRoot = Roots.findLocalRoot(this.getRegistryGraph()).findPeerRoot(XDI3Segment.create("" + providerid + query), false);

		if (peerRoot == null) {

			for (XDI3Segment provideridSynonym : provideridSynonyms) {

				peerRoot = Roots.findLocalRoot(this.getRegistryGraph()).findPeerRoot(XDI3Segment.create("" + provideridSynonym + query), false);
				if (peerRoot != null) break;
			}
		}

		if (peerRoot == null) {

			log.warn("Peer root for " + query + " not found in the registry graph. Ignoring.");
			this.sendNotFoundXrd(httpTransport, request, query, response);
			return true;
		}

		if (peerRoot.isSelfPeerRoot()) {

			log.warn("Peer root for " + query + " is the owner of the registry graph. Ignoring.");
			this.sendNotFoundXrd(httpTransport, request, query, response);
			return true;
		}

		ContextNode referencePeerRootContextNode = Equivalence.getReferenceContextNode(peerRoot.getContextNode());
		PeerRoot referencePeerRoot = (referencePeerRootContextNode == null) ? null : PeerRoot.fromContextNode(referencePeerRootContextNode);
		if (referencePeerRoot == null) referencePeerRoot = peerRoot;

		XDI3Segment canonicalid = referencePeerRoot.getXriOfPeerRoot();
		XDI3SubSegment localid = query;
		String uri = constructUri(request, this.getTargetPath(), canonicalid);

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("httptransport", httpTransport);
		context.put("request", request);
		context.put("query", query);
		context.put("providerid", providerid);
		context.put("localid", localid);
		context.put("canonicalid", canonicalid);
		context.put("uri", uri);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrds-ok.vm"));
		PrintWriter writer = new PrintWriter(response.getBodyWriter());

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

	private static XDI3SubSegment parseQuery(HttpRequest request) {

		String query = request.getRequestPath();
		if (query.endsWith("/")) query = query.substring(0, query.length() - 1);
		query = query.substring(query.lastIndexOf('/') + 1);

		if (query.isEmpty()) return null;

		return XDI3SubSegment.create(query);
	}

	private static String constructUri(HttpRequest request, String targetPath, XDI3Segment canonicalid) {

		String uri = request.getUri().substring(0, request.getUri().length() - request.getRequestPath().length());
		uri += targetPath + "/" + canonicalid.toString();

		return uri;
	}

	private static XDI3Segment getProviderId(Graph graph) {

		PeerRoot selfPeerRoot = Roots.findLocalRoot(graph).getSelfPeerRoot();
		if (selfPeerRoot == null) return null;

		return selfPeerRoot.getXriOfPeerRoot();
	}

	private static XDI3Segment[] getProviderIdSynonyms(Graph graph, XDI3Segment providerid) {

		PeerRoot selfPeerRoot = Roots.findLocalRoot(graph).getSelfPeerRoot();
		if (selfPeerRoot == null) return new XDI3Segment[0];

		Iterator<ContextNode> selfPeerRootIncomingReferenceContextNodes = Equivalence.getIncomingReferenceContextNodes(selfPeerRoot.getContextNode());

		XDI3Segment[] selfSynonyms = new IteratorArrayMaker<XDI3Segment> (new MappingContextNodeXriIterator(selfPeerRootIncomingReferenceContextNodes)).array(XDI3Segment.class);
		for (int i=0; i<selfSynonyms.length; i++) selfSynonyms[i] = PeerRoot.getXriOfPeerRootXri(selfSynonyms[i].getLastSubSegment());

		return selfSynonyms;
	}

	private void sendNotFoundXrd(HttpTransport httpTransport, HttpRequest request, XDI3SubSegment query, HttpResponse response) throws IOException {

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("httptransport", httpTransport);
		context.put("request", request);
		context.put("query", query);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrds-notfound.vm"));
		PrintWriter writer = new PrintWriter(response.getBodyWriter());

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
