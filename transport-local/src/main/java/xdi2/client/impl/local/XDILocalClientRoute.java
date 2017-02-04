package xdi2.client.impl.local;

import java.util.ArrayList;
import java.util.Collection;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.transport.Transport;

public class XDILocalClientRoute extends XDIAbstractClientRoute<XDILocalClient> implements XDIClientRoute<XDILocalClient> {

	private MessagingContainer messagingContainer;
	private Graph graph;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, MessagingContainer messagingContainer, Graph graph) {

		super(toPeerRootXDIArc);

		this.messagingContainer = messagingContainer;
		this.graph = graph;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, MessagingContainer messagingContainer) {

		this(toPeerRootXDIArc, messagingContainer, null);
	}

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, Graph graph) {

		this(toPeerRootXDIArc, null, graph);
	}

	public XDILocalClientRoute(MessagingContainer messagingContainer) {

		this(null, messagingContainer, null);
	}

	public XDILocalClientRoute(Graph graph) {

		this(null, null, graph);
	}

	public XDILocalClientRoute() {

		this(null, null, null);
	}

	@Override
	protected XDILocalClient constructXDIClientInternal() {

		// client construction step

		XDILocalClient xdiClient = new XDILocalClient(this.getMessagingContainer(), this.getGraph());

		// add interceptors if supported

		if (this.getInterceptors() != null) {

			xdiClient.getInterceptors().addAll(this.getInterceptors());
		}

		// done

		return xdiClient;
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
