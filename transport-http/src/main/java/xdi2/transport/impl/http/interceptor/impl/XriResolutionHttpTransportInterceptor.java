package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
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
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.MappingContextNodeXDIAddressIterator;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;

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
	public void init(Transport<?, ?> transport) {

		this.velocityEngine = new VelocityEngine();
		this.velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "");
		this.velocityEngine.init();
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().startsWith(this.getResolvePath())) return false;

		// prepare resolution information

		XDIArc query = parseQuery(request);
		XDIAddress providerid = getProviderId(this.getRegistryGraph());
		XDIAddress[] provideridSynonyms = getProviderIdSynonyms(this.getRegistryGraph(), providerid);

		// look into registry

		XdiPeerRoot peerRoot = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoot(XDIAddress.create("" + providerid + query), false);

		if (peerRoot == null) {

			for (XDIAddress provideridSynonym : provideridSynonyms) {

				peerRoot = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoot(XDIAddress.create("" + provideridSynonym + query), false);
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
		XdiPeerRoot referencePeerRoot = (referencePeerRootContextNode == null) ? null : XdiPeerRoot.fromContextNode(referencePeerRootContextNode);
		if (referencePeerRoot == null) referencePeerRoot = peerRoot;

		XDIAddress canonicalid = referencePeerRoot.getXDIAddressOfPeerRoot();
		XDIArc localid = query;
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
		StringWriter stringWriter = new StringWriter();
		this.velocityEngine.evaluate(context, stringWriter, "xrds-ok.vm", reader);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/xrd+xml");
		response.writeBody(stringWriter.getBuffer().toString(), true);

		// done

		return true;
	}

	/*
	 * Helper methods
	 */

	private static XDIArc parseQuery(HttpTransportRequest request) {

		String query = request.getRequestPath();
		if (query.endsWith("/")) query = query.substring(0, query.length() - 1);
		query = query.substring(query.lastIndexOf('/') + 1);

		if (query.isEmpty()) return null;

		return XDIArc.create(query);
	}

	private static String constructUri(HttpTransportRequest request, String targetPath, XDIAddress canonicalid) {

		String uri = request.getBaseUri() + targetPath + "/" + canonicalid.toString();

		return uri;
	}

	private static XDIAddress getProviderId(Graph graph) {

		return GraphUtil.getOwnerXDIAddress(graph);
	}

	private static XDIAddress[] getProviderIdSynonyms(Graph graph, XDIAddress providerid) {

		XdiPeerRoot selfPeerRoot = XdiCommonRoot.findCommonRoot(graph).getSelfPeerRoot();
		if (selfPeerRoot == null) return new XDIAddress[0];

		Iterator<ContextNode> selfPeerRootIncomingReferenceContextNodes = Equivalence.getIncomingReferenceContextNodes(selfPeerRoot.getContextNode());

		XDIAddress[] selfSynonyms = new IteratorArrayMaker<XDIAddress> (new MappingContextNodeXDIAddressIterator(selfPeerRootIncomingReferenceContextNodes)).array(XDIAddress.class);
		for (int i=0; i<selfSynonyms.length; i++) selfSynonyms[i] = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(selfSynonyms[i].getLastXDIArc());

		return selfSynonyms;
	}

	private void sendNotFoundXrd(HttpTransport httpTransport, HttpTransportRequest request, XDIArc query, HttpTransportResponse response) throws IOException {

		// prepare velocity

		VelocityContext context = new VelocityContext();
		context.put("httptransport", httpTransport);
		context.put("request", request);
		context.put("query", query);

		// send response

		Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("xrds-notfound.vm"));
		StringWriter stringWriter = new StringWriter();
		this.velocityEngine.evaluate(context, stringWriter, "xrd.vm", reader);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/xrd+xml");
		response.writeBody(stringWriter.getBuffer().toString(), true);
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
