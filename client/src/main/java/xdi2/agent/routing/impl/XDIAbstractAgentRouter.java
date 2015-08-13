package xdi2.agent.routing.impl;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.ManipulatorList;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public abstract class XDIAbstractAgentRouter <ROUTE extends XDIClientRoute<CLIENT>, CLIENT extends XDIClient> implements XDIAgentRouter<ROUTE, CLIENT> {

	private XDIArc overrideToPeerRootXDIArc;
	private ManipulatorList manipulators;

	protected XDIAbstractAgentRouter() {

		this.overrideToPeerRootXDIArc = null;
		this.manipulators = new ManipulatorList();
	}

	@Override
	public final ROUTE route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		ROUTE route = this.routeInternal(this.overrideToPeerRootXDIArc(toPeerRootXDIArc));

		if (route instanceof XDIAbstractClientRoute) {

			((XDIAbstractClientRoute<?>) route).setManipulators(this.getManipulators());
		}

		return route;
	}

	protected XDIArc overrideToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		XDIArc overrideToPeerRootXDIArc = getOverrideToPeerRootXDIArc();
		if (overrideToPeerRootXDIArc != null) return overrideToPeerRootXDIArc;

		return toPeerRootXDIArc;
	}

	protected abstract ROUTE routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException;

	/*
	 * Getters and setters
	 */

	public XDIArc getOverrideToPeerRootXDIArc() {

		return this.overrideToPeerRootXDIArc;
	}

	public void setOverrideToPeerRootXDIArc(XDIArc overrideToPeerRootXDIArc) {

		this.overrideToPeerRootXDIArc = overrideToPeerRootXDIArc;
	}

	public ManipulatorList getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(ManipulatorList manipulators) {

		this.manipulators = manipulators;
	}
}
