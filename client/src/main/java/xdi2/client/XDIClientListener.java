package xdi2.client;

import xdi2.client.events.XDIDiscoverEvent;
import xdi2.client.events.XDISendEvent;

public interface XDIClientListener {

	public void onSend(XDISendEvent sendEvent);
	public void onDiscover(XDIDiscoverEvent discoverEvent);
}
