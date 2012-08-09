package xdi2.server.factory.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.messaging.target.interceptor.impl.CheckOwnerInterceptor;
import xdi2.messaging.target.interceptor.impl.LinkContractsInterceptor;
import xdi2.messaging.target.interceptor.impl.VariablesInterceptor;
import xdi2.server.factory.AbstractMessagingTargetFactory;
import xdi2.server.registry.EndpointRegistry;

public abstract class StandardGraphMessagingTargetFactory extends AbstractMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(StandardGraphMessagingTargetFactory.class);

	private GraphFactory graphFactory;

	public void mountStandardMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetPath, XRI3Segment owner, XRI3Segment[] ownerSynonyms, String sharedSecret) throws Xdi2MessagingException {

		// get graph

		Graph graph;

		try {

			log.debug("Instantiating new graph for " + owner);

			graph = this.graphFactory.openGraph(owner.toString());
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Cannot open graph: " + ex.getMessage(), ex, null);
		}

		// create GraphMessagingTarget

		GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
		graphMessagingTarget.setGraph(graph);

		try {

			graphMessagingTarget.init();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot initialize messaging target: " + ex.getMessage(), ex, null);
		}

		// add interceptors

		BootstrapInterceptor bootstrapInterceptor = new BootstrapInterceptor();
		bootstrapInterceptor.setBootstrapOwner(owner);
		bootstrapInterceptor.setBootstrapOwnerSynonyms(ownerSynonyms);
		bootstrapInterceptor.setBootstrapSharedSecret(sharedSecret);
		bootstrapInterceptor.setBootstrapLinkContract(true);
		graphMessagingTarget.addInterceptor(bootstrapInterceptor);

		VariablesInterceptor variablesInterceptor = new VariablesInterceptor();
		graphMessagingTarget.addInterceptor(variablesInterceptor);

		if (owner != null) {

			CheckOwnerInterceptor checkOwnerInterceptor = new CheckOwnerInterceptor();
			graphMessagingTarget.addInterceptor(checkOwnerInterceptor);
		}

		if (owner != null && sharedSecret != null) {

			LinkContractsInterceptor linkContractsInterceptor = new LinkContractsInterceptor();
			linkContractsInterceptor.setLinkContractsGraph(graph);
			graphMessagingTarget.addInterceptor(linkContractsInterceptor);
		}

		// mount the new messaging target

		endpointRegistry.mountMessagingTarget(messagingTargetPath, graphMessagingTarget);
	}

	public GraphFactory getGraphFactory() {

		return this.graphFactory;
	}

	public void setGraphFactory(GraphFactory graphFactory) {

		this.graphFactory = graphFactory;
	}
}
