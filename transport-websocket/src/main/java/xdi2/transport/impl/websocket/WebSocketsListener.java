package xdi2.transport.impl.websocket;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import xdi2.transport.impl.websocket.endpoint.WebSocketServerEndpoint;

public class WebSocketsListener implements ServletContextListener, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(WebSocketsListener.class);

	private WebSocketTransport webSocketTransport;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isInfoEnabled()) log.info("Setting application context.");

		// find beans

		this.webSocketTransport = (WebSocketTransport) applicationContext.getBean("WebSocketTransport");
		if (this.webSocketTransport == null) log.info("Bean 'WebSocketTransport' not found, support for WebSockets disabled.");
		if (this.webSocketTransport != null) log.info("WebSocketTransport found and enabled.");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		ServletContext servletContext = sce.getServletContext();
		
		// find application context

		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		if (applicationContext != null) this.setApplicationContext(applicationContext);

		// install WebSocket endpoints

		if (this.webSocketTransport == null) return;
		
		try {

			WebSocketServerEndpoint.install(this.webSocketTransport, servletContext);
		} catch (DeploymentException ex) {

			throw new RuntimeException("Problem while deploying websocket endpoint: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
