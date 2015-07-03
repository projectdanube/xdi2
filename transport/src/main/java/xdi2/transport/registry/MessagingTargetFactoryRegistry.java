package xdi2.transport.registry;

import java.util.List;

public interface MessagingTargetFactoryRegistry {

	public List<? extends MessagingTargetFactoryMount> getMessagingTargetFactoryMounts();
	public int getNumMessagingTargetFactorys();
}
