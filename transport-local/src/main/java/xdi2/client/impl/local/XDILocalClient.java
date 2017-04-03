package xdi2.client.impl.local;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.local.LocalTransport;
import xdi2.transport.impl.local.LocalTransportRequest;
import xdi2.transport.impl.local.LocalTransportResponse;

/**
 * An XDI client that can apply XDI messages locally on messaging containers.
 * 
 * @author markus
 */
public class XDILocalClient extends XDIAbstractClient<TransportMessagingResponse> implements XDIClient<TransportMessagingResponse> {

	protected static final Logger log = LoggerFactory.getLogger(XDILocalClient.class);

	private MessagingContainer messagingContainer;
	private Graph graph;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDILocalClient(MessagingContainer messagingContainer, Graph graph) {

		super();

		this.messagingContainer = messagingContainer;
		this.graph = graph;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDILocalClient(MessagingContainer messagingContainer) {

		this(messagingContainer, null);
	}

	public XDILocalClient(Graph graph) {

		this(null, graph);
	}

	public XDILocalClient() {

		this(null, null);
	}

	@Override
	public void close() {

		this.disconnect();
	}

	private MessagingContainer connect() throws Exception {

		if (this.getMessagingContainer() != null) return this.getMessagingContainer();

		if (this.getGraph() == null) throw new Xdi2ClientException("No graph to connect to.");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getGraph().getClass().getSimpleName());

		GraphMessagingContainer messagingContainer = new GraphMessagingContainer();
		messagingContainer.setGraph(this.getGraph());
		messagingContainer.init();

		// done

		if (log.isDebugEnabled()) log.debug("Connected successfully.");

		this.setMessagingContainer(messagingContainer);
		return messagingContainer;
	}

	private void disconnect() {

		try {

			if (this.getMessagingContainer() != null) {

				this.getMessagingContainer().shutdown();
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setMessagingContainer(null);
		}

		if (log.isDebugEnabled()) log.debug("Disconnected successfully.");
	}

	@Override
	protected TransportMessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// connect

		MessagingContainer messagingContainer;

		try {

			messagingContainer = this.connect();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot open messaging container: " + ex.getMessage(), ex);
		}

		// create the transport

		LocalTransport localTransport = new LocalTransport(messagingContainer);

		// add interceptors if supported

		if (this.getInterceptors() != null) {

			localTransport.getInterceptors().addInterceptors(this.getInterceptors());
		}

		// execute the transport

		LocalTransportRequest request = new LocalTransportRequest(messageEnvelope);
		LocalTransportResponse response = new LocalTransportResponse();

		try {

			localTransport.execute(request, response);
		} catch (Xdi2TransportException ex) {

			throw new Xdi2ClientException("Unable to send message envelope to local messaging container: " + ex.getMessage(), ex);
		}

		TransportMessagingResponse messagingResponse = response.getMessagingResponse();

		// done

		return messagingResponse;
	}

	/*
	 * Getters and setters
	 */

	public MessagingContainer getMessagingContainer() {

		return this.messagingContainer;
	}

	public void setMessagingContainer(MessagingContainer messagingContainer) {

		this.messagingContainer = messagingContainer;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}

	public Collection<Interceptor<Transport<?, ?>>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(Collection<Interceptor<Transport<?, ?>>> interceptors) {

		this.interceptors = interceptors;
	}
}
