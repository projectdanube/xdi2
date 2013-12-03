package xdi2.server.registry;

import java.util.List;

public interface MessagingTargetFactoryRegistry {

	public List<MessagingTargetFactoryMount> getMessagingTargetFactoryMounts();
	public int getNumMessagingTargetFactorys();
}
