package xdi2.transport.impl.http.registry;

import java.util.List;

public interface MessagingTargetRegistry {

	public List<MessagingTargetMount> getMessagingTargetMounts();
	public int getNumMessagingTargets();
}
