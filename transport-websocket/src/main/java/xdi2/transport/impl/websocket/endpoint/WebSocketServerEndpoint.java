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

import xdi2.transport.impl.websocket.WebSocketTransport;
import xdi2.transport.registry.impl.uri.UriMessagingTargetFactoryMount;

public class WebSocketServerEndpoint extends javax.websocket.Endpoint {

	private static final Logger log = LoggerFactory.getLogger(WebSocketServerEndpoint.class);

	public static void install(WebSocketTransport webSocketTransport, ServletContext servletContext) throws DeploymentException {

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

		// find server container

		ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
		if (serverContainer == null) throw new DeploymentException("Cannot find ServerContainer");

		// set default timeout

		long oldDefaultMaxSessionIdleTimeout = serverContainer.getDefaultMaxSessionIdleTimeout();
		long newDefaultMaxSessionIdleTimeout = 0;
		serverContainer.setDefaultMaxSessionIdleTimeout(newDefaultMaxSessionIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed default max session idle timeout from " + oldDefaultMaxSessionIdleTimeout + " to " + newDefaultMaxSessionIdleTimeout);

		// install

		install(serverContainer, webSocketTransport, contextPath, endpointPath, "/{path}");

		for (UriMessagingTargetFactoryMount messagingTargetFactoryMount : webSocketTransport.getMessagingTargetRegistry().getMessagingTargetFactoryMounts()) {

			install(serverContainer, webSocketTransport, contextPath, endpointPath, messagingTargetFactoryMount.getMessagingTargetFactoryPath() + "/{path}");
		}
	}

	private static void install(ServerContainer serverContainer, WebSocketTransport webSocketTransport, String contextPath, String endpointPath, String requestPath) throws DeploymentException {

		String path = endpointPath + requestPath;

		// init websocket endpoint

		List<String> subprotocols = Arrays.asList(new String[] { "xdi" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;

		ServerEndpointConfig.Configurator serverEndpointConfigConfigurator = new ServerEndpointConfig.Configurator() {

		};

		ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(
				WebSocketServerEndpoint.class, 
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

		// install websocket endpoint

		serverContainer.addEndpoint(serverEndpointConfig);

		// done

		log.info("Installed WebSocket endpoint at " + path + " with subprotocols " + subprotocols);
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// set timeout

		long oldMaxIdleTimeout = session.getMaxIdleTimeout();
		long newMaxIdleTimeout = 0;
		session.setMaxIdleTimeout(newMaxIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed max idle timeout of session " + session.getId() + " from " + oldMaxIdleTimeout + " to " + newMaxIdleTimeout);

		// read properties

		ServerEndpointConfig serverEndpointConfig = (ServerEndpointConfig) endpointConfig;

		WebSocketTransport webSocketTransport = (WebSocketTransport) serverEndpointConfig.getUserProperties().get("webSocketTransport");

		// init message handler

		WebSocketServerMessageHandler webSocketMessageHandler = new WebSocketServerMessageHandler(session);

		// init session

		log.info("WebSocket session " + session.getId() + " opened (" + serverEndpointConfig.getPath() + ").");

		session.addMessageHandler(webSocketMessageHandler);
		session.getUserProperties().putAll(serverEndpointConfig.getUserProperties());

		// register session

		webSocketTransport.registerSession(session, null);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		log.info("WebSocket session " + session.getId() + " closed.");

		// read properties

		WebSocketTransport webSocketTransport = (WebSocketTransport) session.getUserProperties().get("webSocketTransport");

		// unregister session

		webSocketTransport.unregisterSession(session);
	}

	@Override
	public void onError(Session session, Throwable throwable) {

		log.error("WebSocket session " + session.getId() + " problem: " + throwable.getMessage(), throwable);
	}
}