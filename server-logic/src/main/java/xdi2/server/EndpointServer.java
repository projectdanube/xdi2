package xdi2.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

@SuppressWarnings("unchecked")
public class EndpointServer extends Server implements ApplicationContextAware {

	private ApplicationContext applicationContext = null;
	private String contextPath = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {

		return this.applicationContext;
	}

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
	}

	public String getContextPath() {

		return this.contextPath;
	}

	public static EndpointServer newServer(ApplicationContext applicationContext) throws Exception {

		EndpointServer server = (EndpointServer) applicationContext.getBean("Server");

		return server;
	}

	public static EndpointServer newServer() throws Exception {

		GenericXmlApplicationContext applicationContext = new GenericXmlApplicationContext();
		applicationContext.setClassLoader(ClassLoader.getSystemClassLoader());
		applicationContext.load(new FileSystemResource("jetty-applicationContext.xml"));
		applicationContext.refresh();

		return newServer(applicationContext);
	}

	public void setup() throws Exception {

		EndpointServlet endpointServlet = (EndpointServlet) this.getApplicationContext().getBean("EndpointServlet");
		EndpointFilter endpointFilter = (EndpointFilter) this.getApplicationContext().getBean("EndpointFilter");

		ServletMapping servletMapping = new ServletMapping();
		servletMapping.setServletName("EndpointServlet");
		servletMapping.setPathSpec("/xdi/*");

		FilterMapping filterMapping = new FilterMapping();
		filterMapping.setFilterName("EndpointFilter");
		filterMapping.setServletName("EndpointServlet");

		ServletHandler servletHandler = new ServletHandler();
		servletHandler.setServlets(new ServletHolder[] { new ServletHolder(endpointServlet) });
		servletHandler.setFilters(new FilterHolder[] { new FilterHolder(endpointFilter) });
		servletHandler.setServletMappings(new ServletMapping[] { servletMapping });
		servletHandler.setFilterMappings(new FilterMapping[] { filterMapping });

		ServletContextHandler servletContextHandler = new ServletContextHandler(null, this.getContextPath());
		servletContextHandler.setServletHandler(servletHandler);
		servletContextHandler.setContextPath("/");

		this.setHandler(servletContextHandler);

		super.doStart();
	}

	public static void main(String... args) throws Exception {

		EndpointServer.newServer().start();
	}
}
