package xdi2.client.impl.local;

import java.util.ArrayList;
import java.util.Collection;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;

public class XDILocalClientRoute extends XDIAbstractClientRoute<XDILocalClient> implements XDIClientRoute<XDILocalClient> {

	private MessagingTarget messagingTarget;
	private Graph graph;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, MessagingTarget messagingTarget, Graph graph) {

		super(toPeerRootXDIArc);

		this.messagingTarget = messagingTarget;
		this.graph = graph;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, MessagingTarget messagingTarget) {

		this(toPeerRootXDIArc, messagingTarget, null);
	}

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, Graph graph) {

		this(toPeerRootXDIArc, null, graph);
	}

	public XDILocalClientRoute(MessagingTarget messagingTarget) {

		this(null, messagingTarget, null);
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

		XDILocalClient xdiClient = new XDILocalClient(this.getMessagingTarget(), this.getGraph());

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

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
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
