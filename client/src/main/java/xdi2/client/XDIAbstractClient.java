package xdi2.client;

import java.util.ArrayList;
import java.util.List;

import xdi2.client.events.XDIDiscoveryEvent;
import xdi2.client.events.XDISendEvent;

public abstract class XDIAbstractClient implements XDIClient {

	private final List<XDIClientListener> clientListeners;

	public XDIAbstractClient() {

		this.clientListeners = new ArrayList<XDIClientListener> ();
	}

	public void addClientListener(XDIClientListener clientListener) {

		if (this.clientListeners.contains(clientListener)) return;
		this.clientListeners.add(clientListener);
	}

	public void removeClientListener(XDIClientListener clientListener) {

		this.clientListeners.remove(clientListener);
	}

	public void fireSendEvent(XDISendEvent sendEvent) {

		for (XDIClientListener clientListener : this.clientListeners) clientListener.onSend(sendEvent);
	}

	public void fireDiscoveryEvent(XDIDiscoveryEvent discoveryEvent) {

		for (XDIClientListener clientListener : this.clientListeners) clientListener.onDiscovery(discoveryEvent);
	}
}
