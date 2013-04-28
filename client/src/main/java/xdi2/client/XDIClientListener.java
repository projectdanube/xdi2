package xdi2.client;

import xdi2.client.events.XDIDiscoveryEvent;
import xdi2.client.events.XDISendEvent;

public interface XDIClientListener {

	public void onSend(XDISendEvent sendEvent);
	public void onDiscovery(XDIDiscoveryEvent discoveryEvent);
}
