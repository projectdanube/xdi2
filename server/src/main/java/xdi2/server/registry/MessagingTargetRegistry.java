package xdi2.server.registry;

import java.util.List;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.factory.MessagingTargetFactory;

public interface MessagingTargetRegistry {

	public List<MessagingTarget> getMessagingTargets();
	public int getNumMessagingTargets();

	public List<MessagingTargetFactory> getMessagingTargetFactorys();
	public int getNumMessagingTargetFactorys();
}
