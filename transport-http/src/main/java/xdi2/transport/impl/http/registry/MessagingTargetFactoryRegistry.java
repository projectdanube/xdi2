package xdi2.transport.impl.http.registry;

import java.util.List;

public interface MessagingTargetFactoryRegistry {

	public List<MessagingTargetFactoryMount> getMessagingTargetFactoryMounts();
	public int getNumMessagingTargetFactorys();
}
