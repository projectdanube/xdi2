package xdi2.samples.server;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.server.EndpointServlet;

public class BasicGraphServer extends AbstractJettyServer {

	@Override
	protected void setup(EndpointServlet endpointServlet) throws Exception {

		// add messaging targets

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		BootstrapInterceptor bi = new BootstrapInterceptor();
		bi.setBootstrapOwner(new XRI3Segment("=!1111"));
		bi.setBootstrapSharedSecret("s3cr3t");
		bi.setBootstrapLinkContract(true);

		messagingTarget.getInterceptors().add(bi);

		endpointServlet.getEndpointRegistry().mountMessagingTarget("/", messagingTarget);
	}

	public static void main(String[] args) throws Throwable {

		new BasicGraphServer().run();
	}
}
