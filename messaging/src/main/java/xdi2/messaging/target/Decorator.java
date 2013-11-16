package xdi2.messaging.target;

import java.util.Comparator;

public interface Decorator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;

	public int getInitPriority();
	public int getShutdownPriority();

	/*
	 * Enabled?
	 */

	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	/*
	 * Class for sorting by init or shutdown priority
	 */

	public static class InitPriorityComparator implements Comparator<Decorator> {

		@Override
		public int compare(Decorator decorator1, Decorator decorator2) {

			return Integer.valueOf(decorator1.getInitPriority()).compareTo(Integer.valueOf(decorator2.getInitPriority()));
		}
	}

	public static class ShutdownPriorityComparator implements Comparator<Decorator> {

		@Override
		public int compare(Decorator decorator1, Decorator decorator2) {

			return Integer.valueOf(decorator1.getShutdownPriority()).compareTo(Integer.valueOf(decorator2.getShutdownPriority()));
		}
	}
}
