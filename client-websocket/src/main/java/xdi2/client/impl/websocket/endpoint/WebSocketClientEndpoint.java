package xdi2.client.impl.websocket.endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;

import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.impl.websocket.XDIWebSocketClient;

public class WebSocketClientEndpoint extends javax.websocket.Endpoint {

	private static final Logger log = LoggerFactory.getLogger(WebSocketClientEndpoint.class);

	private Session session;

	public static WebSocketClientEndpoint connect(XDIWebSocketClient xdiWebSocketClient, URL xdiWebSocketEndpointUrl) throws DeploymentException, IOException {

		// create client container

		ClientContainer clientContainer = new ClientContainer();

		// set default timeout

		long oldDefaultMaxSessionIdleTimeout = clientContainer.getDefaultMaxSessionIdleTimeout();
		long newDefaultMaxSessionIdleTimeout = 0;
		clientContainer.setDefaultMaxSessionIdleTimeout(newDefaultMaxSessionIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed default max session idle timeout from " + oldDefaultMaxSessionIdleTimeout + " to " + newDefaultMaxSessionIdleTimeout);

		// connect

		return connect(clientContainer, xdiWebSocketClient, xdiWebSocketEndpointUrl);
	}

	private static WebSocketClientEndpoint connect(ClientContainer clientContainer, XDIWebSocketClient xdiWebSocketClient, URL xdiWebSocketEndpointUrl) throws DeploymentException, IOException {

		// init websocket endpoint

		List<String> preferredSubprotocols = Arrays.asList(new String[] { "xdi" });
		List<Extension> extensions = null;
		List<Class<? extends Encoder>> encoders = null;
		List<Class<? extends Decoder>> decoders = null;

		ClientEndpointConfig.Configurator clientEndpointConfigConfigurator = new ClientEndpointConfig.Configurator() {

		};

		ClientEndpointConfig.Builder clientEndpointConfigBuilder = ClientEndpointConfig.Builder.create();

		clientEndpointConfigBuilder.preferredSubprotocols(preferredSubprotocols);
		clientEndpointConfigBuilder.extensions(extensions);
		clientEndpointConfigBuilder.encoders(encoders);
		clientEndpointConfigBuilder.decoders(decoders);
		clientEndpointConfigBuilder.configurator(clientEndpointConfigConfigurator);

		ClientEndpointConfig clientEndpointConfig = clientEndpointConfigBuilder.build();
		clientEndpointConfig.getUserProperties().put("xdiWebSocketClient", xdiWebSocketClient);
		clientEndpointConfig.getUserProperties().put("xdiWebSocketEndpointUrl", xdiWebSocketEndpointUrl);

		// connect websocket endpoint

		WebSocketClientEndpoint webSocketEndpoint = new WebSocketClientEndpoint();

		Session session = clientContainer.connectToServer(webSocketEndpoint, clientEndpointConfig, URI.create(xdiWebSocketEndpointUrl.toString()));
		webSocketEndpoint.setSession(session);

		// done

		log.info("Connected WebSocket endpoint for " + xdiWebSocketEndpointUrl + " with preferred subprotocols " + preferredSubprotocols);
		return webSocketEndpoint;
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// set timeout

		long oldMaxIdleTimeout = session.getMaxIdleTimeout();
		long newMaxIdleTimeout = 0;
		session.setMaxIdleTimeout(newMaxIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed max idle timeout of session " + session.getId() + " from " + oldMaxIdleTimeout + " to " + newMaxIdleTimeout);

		// init message handler

		ClientEndpointConfig clientEndpointConfig = (ClientEndpointConfig) endpointConfig;
		XDIWebSocketClient xdiWebSocketClient = (XDIWebSocketClient) clientEndpointConfig.getUserProperties().get("xdiWebSocketClient");
		URL xdiWebSocketEndpointUrl = (URL) clientEndpointConfig.getUserProperties().get("xdiWebSocketEndpointUrl");

		WebSocketClientMessageHandler webSocketMessageHandler = new WebSocketClientMessageHandler(session, xdiWebSocketClient, xdiWebSocketEndpointUrl);

		log.info("WebSocket session " + session.getId() + " opened (" + xdiWebSocketEndpointUrl + ")");

		session.addMessageHandler(webSocketMessageHandler);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		log.info("WebSocket session " + session.getId() + " closed.");
	}

	@Override
	public void onError(Session session, Throwable throwable) {

		log.error("WebSocket session " + session.getId() + " problem: " + throwable.getMessage(), throwable);
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public void setSession(Session session) {

		this.session = session;
	}
}