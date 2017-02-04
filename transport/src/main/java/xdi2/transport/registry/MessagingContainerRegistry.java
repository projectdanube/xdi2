package xdi2.transport.registry;

import java.util.List;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.transport.exceptions.Xdi2TransportException;

public interface MessagingContainerRegistry {

	public List<? extends MessagingContainerMount> getMessagingContainerMounts();
	public List<? extends MessagingContainerFactoryMount> getMessagingContainerFactoryMounts();
	public MessagingContainerMount lookup(XDIArc ownerPeerRootXDIArc) throws Xdi2TransportException, Xdi2MessagingException;
	public int getNumMessagingContainers();
	public int getNumMessagingContainerFactorys();
}
