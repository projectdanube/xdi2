package xdi2.transport.impl.websocket.endpoint;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

public class WebSocketEndpoint extends javax.websocket.Endpoint implements ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isInfoEnabled()) log.info("Setting application context.");

		// find bean

		this.httpTransport = (HttpTransport) applicationContext.getBean("HttpTransport");
		if (this.httpTransport == null) throw new NoSuchBeanDefinitionException("Required bean 'HttpTransport' not found.");
	}

	public static void install(ServletContext servletContext, String webSocketPath) throws DeploymentException {

		// init websocket endpoint

		ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
		if (serverContainer == null) throw new DeploymentException("Cannot find ServerContainer");

		List<String> subprotocols = Arrays.asList(new String[] { "xdi" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;
		ServerEndpointConfig.Configurator serverEndpointConfigConfigurator = null;

		ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(
				WebSocketEndpoint.class, 
				webSocketPath);

		serverEndpointConfigBuilder.subprotocols(subprotocols);
		serverEndpointConfigBuilder.extensions(extensions);
		serverEndpointConfigBuilder.encoders(encoders);
		serverEndpointConfigBuilder.decoders(decoders);
		serverEndpointConfigBuilder.configurator(serverEndpointConfigConfigurator);

		serverContainer.addEndpoint(serverEndpointConfigBuilder.build());
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// find application context

		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		if (applicationContext != null) this.setApplicationContext(applicationContext);

		// init message handler
		
		ServerEndpointConfig serverEndpointConfig = (ServerEndpointConfig) endpointConfig;
		
		log.info("WebSocket session " + session.getId() + " opened (" + serverEndpointConfig.getPath() + ")");

		MessagingTargetMount messagingTargetMount;

		try {

			messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(serverEndpointConfig.getPath());
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		session.addMessageHandler(new WebSocketMessageHandler(serverEndpointConfig, session, messagingTargetMount));
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		log.info("WebSocket session " + session.getId() + " closed.");
	}

	@Override
	public void onError (Session session, Throwable throwable) {

		log.error("WebSocket session " + session.getId() + " problem: " + throwable.getMessage(), throwable);
	}

	/*
	 * Getters and setters
	 */

	public HttpMessagingTargetRegistry getHttpMessagingTargetRegistry() {

		return this.httpMessagingTargetRegistry;
	}

	public void setHttpMessagingTargetRegistry(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
	}
}