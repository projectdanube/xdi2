package xdi2.samples.server;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.server.EndpointServer;

public class BasicEndpointServerSample {

	public static void main(String[] args) throws Throwable {

		// create the XDI2 server

		EndpointServer endpointServer = EndpointServer.newServer();

		// set up graph messaging target

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// add interceptor

		BootstrapInterceptor bi = new BootstrapInterceptor();
		bi.setBootstrapOwner(new XRI3Segment("=!1111"));
		bi.setBootstrapSharedSecret("s3cr3t");
		bi.setBootstrapLinkContract(true);

		messagingTarget.getInterceptors().add(bi);

		// mount messaging target

		endpointServer.getEndpointServlet().getEndpointRegistry().mountMessagingTarget("/", messagingTarget);

		// start the server

		endpointServer.start();
	}
}
