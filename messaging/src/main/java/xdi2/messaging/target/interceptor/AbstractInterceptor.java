package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public abstract class AbstractInterceptor implements Interceptor {

	private Graph graph;
	private boolean enabled;

	public AbstractInterceptor() {

		this.graph = null;
		this.enabled = true;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		if (this.getGraph() == null && messagingTarget instanceof GraphMessagingTarget) {

			this.setGraph(((GraphMessagingTarget) messagingTarget).getGraph());
		}
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}
	
	/*
	 * Enabled?
	 */

	@Override
	public boolean isEnabled() {

		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	/*
	 * Getters and setters
	 */

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
