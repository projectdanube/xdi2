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

import xdi2.transport.impl.http.registry.MessagingTargetFactoryMount;
import xdi2.transport.impl.websocket.WebSocketTransport;

public class WebSocketEndpoint extends javax.websocket.Endpoint {

	private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);

	public static void install(ServletContext servletContext, WebSocketTransport webSocketTransport) throws DeploymentException {

		String contextPath = servletContext.getContextPath();
		if (contextPath == null) contextPath = "";
		if (! contextPath.startsWith("/")) contextPath = "/" + contextPath;
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);
		if (log.isDebugEnabled()) log.debug("Context Path: " + contextPath);

		String endpointPath = webSocketTransport.getEndpointPath();
		if (endpointPath == null) endpointPath = "";
		if (! endpointPath.startsWith("/")) endpointPath = "/" + endpointPath;
		if (endpointPath.endsWith("/")) endpointPath = endpointPath.substring(0, endpointPath.length() - 1);
		if (log.isDebugEnabled()) log.debug("Endpoint Path: " + endpointPath);

		// figure out paths

		install(servletContext, webSocketTransport, contextPath, endpointPath, "/{path}");
		install(servletContext, webSocketTransport, contextPath, endpointPath, "/{path}/");

		for (MessagingTargetFactoryMount messagingTargetFactoryMount : webSocketTransport.getHttpMessagingTargetRegistry().getMessagingTargetFactoryMounts()) {

			install(servletContext, webSocketTransport, contextPath, endpointPath, messagingTargetFactoryMount.getMessagingTargetFactoryPath() + "/{path}");
			install(servletContext, webSocketTransport, contextPath, endpointPath, messagingTargetFactoryMount.getMessagingTargetFactoryPath() + "/{path}/");
		}
	}

	public static void install(ServletContext servletContext, WebSocketTransport webSocketTransport, String contextPath, String endpointPath, String requestPath) throws DeploymentException {

		// init websocket endpoints

		ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
		if (serverContainer == null) throw new DeploymentException("Cannot find ServerContainer");

		List<String> subprotocols = Arrays.asList(new String[] { "xdi" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;

		ServerEndpointConfig.Configurator serverEndpointConfigConfigurator = new ServerEndpointConfig.Configurator() {

		};

		String path = contextPath + endpointPath + requestPath;
		
		ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(
				WebSocketEndpoint.class, 
				path);

		serverEndpointConfigBuilder.subprotocols(subprotocols);
		serverEndpointConfigBuilder.extensions(extensions);
		serverEndpointConfigBuilder.encoders(encoders);
		serverEndpointConfigBuilder.decoders(decoders);
		serverEndpointConfigBuilder.configurator(serverEndpointConfigConfigurator);

		ServerEndpointConfig serverEndpointConfig = serverEndpointConfigBuilder.build();
		serverEndpointConfig.getUserProperties().put("webSocketTransport", webSocketTransport);
		serverEndpointConfig.getUserProperties().put("contextPath", contextPath);
		serverEndpointConfig.getUserProperties().put("endpointPath", endpointPath);

		serverContainer.addEndpoint(serverEndpointConfig);

		log.info("Installed WebSocket endpoint at " + path + " with subprotocols " + subprotocols);
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// init message handler

		ServerEndpointConfig serverEndpointConfig = (ServerEndpointConfig) endpointConfig;
		WebSocketTransport webSocketTransport = (WebSocketTransport) serverEndpointConfig.getUserProperties().get("webSocketTransport");
		String contextPath = (String) serverEndpointConfig.getUserProperties().get("contextPath");
		String endpointPath = (String) serverEndpointConfig.getUserProperties().get("endpointPath");

		log.info("WebSocket session " + session.getId() + " opened (" + serverEndpointConfig.getPath() + ")");

		session.addMessageHandler(new WebSocketMessageHandler(session, webSocketTransport, contextPath, endpointPath));
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		log.info("WebSocket session " + session.getId() + " closed.");
	}

	@Override
	public void onError (Session session, Throwable throwable) {

		log.error("WebSocket session " + session.getId() + " problem: " + throwable.getMessage(), throwable);
	}
}