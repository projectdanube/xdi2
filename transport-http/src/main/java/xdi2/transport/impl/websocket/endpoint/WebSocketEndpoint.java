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

public class WebSocketEndpoint extends javax.websocket.Endpoint {

	private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);

	public static void install(ServletContext servletContext, WebSocketTransport webSocketTransport) throws DeploymentException {

		// init websocket endpoint

		ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
		if (serverContainer == null) throw new DeploymentException("Cannot find ServerContainer");

		List<String> subprotocols = Arrays.asList(new String[] { "xdi" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;

		ServerEndpointConfig.Configurator serverEndpointConfigConfigurator = new ServerEndpointConfig.Configurator() {

		};

		ServerEndpointConfig.Builder serverEndpointConfigBuilder = ServerEndpointConfig.Builder.create(WebSocketEndpoint.class, "/");

		serverEndpointConfigBuilder.subprotocols(subprotocols);
		serverEndpointConfigBuilder.extensions(extensions);
		serverEndpointConfigBuilder.encoders(encoders);
		serverEndpointConfigBuilder.decoders(decoders);
		serverEndpointConfigBuilder.configurator(serverEndpointConfigConfigurator);

		ServerEndpointConfig serverEndpointConfig = serverEndpointConfigBuilder.build();
		serverEndpointConfig.getUserProperties().put("webSocketTransport", webSocketTransport);

		serverContainer.addEndpoint(serverEndpointConfig);
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// init message handler

		ServerEndpointConfig serverEndpointConfig = (ServerEndpointConfig) endpointConfig;
		WebSocketTransport webSocketTransport = (WebSocketTransport) serverEndpointConfig.getUserProperties().get("webSocketTransport");

		log.info("WebSocket session " + session.getId() + " opened (" + serverEndpointConfig.getPath() + ")");

		session.addMessageHandler(new WebSocketMessageHandler(session, webSocketTransport));
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