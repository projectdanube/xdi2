package xdi2.samples.server;

import java.io.File;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.server.EndpointServlet;

public class FileMappingServer extends AbstractJettyServer {

	@Override
	protected void setup(EndpointServlet endpointServlet) throws Exception {

		// set up graph messaging target

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// add contributor

		messagingTarget.addContributor(new XRI3Segment("=!1111+email"), new MyFileContributor(new File("email.txt")));
		messagingTarget.addContributor(new XRI3Segment("=!1111+name"), new MyFileContributor(new File("name.txt")));

		// add interceptor

		BootstrapInterceptor bootstrapInterceptor = new BootstrapInterceptor();
		bootstrapInterceptor.setBootstrapOwner(new XRI3Segment("=!1111"));
		bootstrapInterceptor.setBootstrapSharedSecret("s3cr3t");
		bootstrapInterceptor.setBootstrapLinkContract(true);

		messagingTarget.getInterceptors().add(bootstrapInterceptor);

		// mount messaging target

		endpointServlet.getEndpointRegistry().mountMessagingTarget("/", messagingTarget);
	}

	public static void main(String[] args) throws Throwable {

		new FileMappingServer().run();
	}
}
