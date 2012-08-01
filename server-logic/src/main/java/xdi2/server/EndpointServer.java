package xdi2.server;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class EndpointServer extends Server implements ApplicationContextAware {

	private String webAppDir = null;
	private String contextPath = null;
	private ServletHandler servletHandler = null;
	private ApplicationContext applicationContext = null;

	public String getContextPath() {

		return this.contextPath;
	}

	public ServletHandler getServletHandler() {

		return this.servletHandler;
	}

	public String getWebAppDir() {

		return this.webAppDir;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		this.applicationContext = applicationContext;
	}

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
	}

	public void setServletHandler(ServletHandler servletHandler) {

		this.servletHandler = servletHandler;
	}

	public void setWebAppDir(String webAppDir) {

		this.webAppDir = webAppDir;
	}

	@Override
	protected void doStart() throws Exception {

		EndpointServlet endpointServlet = new EndpointServlet();

		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.addServlet(new ServletHolder(endpointServlet), "/*");

		//		final WebAppContext webAppContext = new WebAppContext(getServer(), this.webAppDir, this.contextPath);
		GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();

		webApplicationContext.setServletContext(servletContextHandler.getServletContext());
		webApplicationContext.setParent(this.applicationContext);
		servletContextHandler.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
		webApplicationContext.refresh();
		//servletContextHandler.setServletHandler(this.servletHandler);

		this.setHandler(servletContextHandler);

		super.doStart();
	}
	
	public static void main(String... args) throws Exception {
		
		new EndpointServer().start();
	}
}
