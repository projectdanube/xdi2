package xdi2.transport.registry;

import java.util.List;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.transport.exceptions.Xdi2TransportException;

public interface MessagingTargetRegistry {

	public List<? extends MessagingTargetMount> getMessagingTargetMounts();
	public MessagingTargetMount lookup(XDIArc ownerPeerRootXDIArc) throws Xdi2TransportException, Xdi2MessagingException;
	public int getNumMessagingTargets();
}
