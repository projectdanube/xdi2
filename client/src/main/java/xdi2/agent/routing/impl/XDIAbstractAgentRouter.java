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

		// the TO peer root may be overridden either by a property or by a subclass, in order
		// to "force" a route to a peer root other than the one that was requested

		toPeerRootXDIArc = this.overrideToPeerRootXDIArc(toPeerRootXDIArc);

		ROUTE route = this.routeInternal(toPeerRootXDIArc);

		if (route instanceof XDIAbstractClientRoute) {

			((XDIAbstractClientRoute<?>) route).setManipulators(this.getManipulators());
		}

		return route;
	}

	protected XDIArc overrideToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		XDIArc overrideToPeerRootXDIArc = this.getOverrideToPeerRootXDIArc();
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
