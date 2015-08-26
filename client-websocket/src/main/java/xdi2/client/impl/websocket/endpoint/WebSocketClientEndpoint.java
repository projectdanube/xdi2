package xdi2.client.impl.websocket.endpoint;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
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

	public static WebSocketClientEndpoint connect(XDIWebSocketClient xdiWebSocketClient, URI xdiWebSocketEndpointUri) throws Exception {

		// create client container

		ClientContainer clientContainer = new ClientContainer();

		// set default timeout

		long oldDefaultMaxSessionIdleTimeout = clientContainer.getDefaultMaxSessionIdleTimeout();
		long newDefaultMaxSessionIdleTimeout = 0;
		clientContainer.setDefaultMaxSessionIdleTimeout(newDefaultMaxSessionIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed default max session idle timeout from " + oldDefaultMaxSessionIdleTimeout + " to " + newDefaultMaxSessionIdleTimeout);

		// connect

		return connect(clientContainer, xdiWebSocketClient, xdiWebSocketEndpointUri);
	}

	private static WebSocketClientEndpoint connect(ClientContainer clientContainer, XDIWebSocketClient xdiWebSocketClient, URI xdiWebSocketEndpointUri) throws Exception {

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
		clientEndpointConfig.getUserProperties().put("xdiWebSocketEndpointUri", xdiWebSocketEndpointUri);

		// connect websocket endpoint

		WebSocketClientEndpoint webSocketEndpoint = new WebSocketClientEndpoint();

		clientContainer.start();
		Session session = clientContainer.connectToServer(webSocketEndpoint, clientEndpointConfig, URI.create(xdiWebSocketEndpointUri.toString()));
		webSocketEndpoint.setSession(session);

		// done

		log.info("Connected WebSocket endpoint for " + xdiWebSocketEndpointUri + " with preferred subprotocols " + preferredSubprotocols);
		return webSocketEndpoint;
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {

		// set timeout

		long oldMaxIdleTimeout = session.getMaxIdleTimeout();
		long newMaxIdleTimeout = 0;
		session.setMaxIdleTimeout(newMaxIdleTimeout);

		if (log.isDebugEnabled()) log.debug("Changed max idle timeout of session " + session.getId() + " from " + oldMaxIdleTimeout + " to " + newMaxIdleTimeout);

		// read properties

		ClientEndpointConfig clientEndpointConfig = (ClientEndpointConfig) endpointConfig;

		URI xdiWebSocketEndpointUri = (URI) clientEndpointConfig.getUserProperties().get("xdiWebSocketEndpointUri");

		// init message handler

		WebSocketClientMessageHandler webSocketMessageHandler = new WebSocketClientMessageHandler(session);

		// init session

		log.info("WebSocket session " + session.getId() + " opened (" + xdiWebSocketEndpointUri + ").");

		session.addMessageHandler(webSocketMessageHandler);
		session.getUserProperties().putAll(clientEndpointConfig.getUserProperties());
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		log.info("WebSocket session " + session.getId() + " closed.");

		XDIWebSocketClient xdiWebSocketClient = (XDIWebSocketClient) session.getUserProperties().get("xdiWebSocketClient");
		xdiWebSocketClient.close();
	}

	@Override
	public void onError(Session session, Throwable throwable) {

		log.error("WebSocket session " + (session != null ? session.getId() : session) + " problem: " + throwable.getMessage(), throwable);

		if (session != null) {

			XDIWebSocketClient xdiWebSocketClient = (XDIWebSocketClient) session.getUserProperties().get("xdiWebSocketClient");
			xdiWebSocketClient.close();
		}
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