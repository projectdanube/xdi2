package xdi2.server.registry;

import java.util.List;

public interface MessagingTargetRegistry {

	public List<MessagingTargetMount> getMessagingTargetMounts();
	public int getNumMessagingTargets();
}
