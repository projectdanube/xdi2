package xdi2.messaging.target;

import java.util.Comparator;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;

public interface Extension <CONTAINER> {

	/*
	 * Init and shutdown
	 */

	public void init(CONTAINER container) throws Exception;
	public void shutdown(CONTAINER container) throws Exception;

	public int getInitPriority();
	public int getShutdownPriority();

	/*
	 * Enabled?
	 */

	public boolean skip(ExecutionContext executionContext);
	public void setDisabled();
	public void clearDisabled();
	public void setDisabledForMessageEnvelope(MessageEnvelope messageEnvelope);
	public void clearDisabledForMessageEnvelope(MessageEnvelope messageEnvelope);
	public void setDisabledForMessage(Message message);
	public void clearDisabledForMessage(Message message);
	public void setDisabledForOperation(Operation operation);
	public void clearDisabledForOperation(Operation operation);

	/*
	 * Class for sorting by init or shutdown priority
	 */

	public static class InitPriorityComparator implements Comparator<Extension<?>> {

		@Override
		public int compare(Extension<?> extension1, Extension<?> extension2) {

			return Integer.valueOf(extension1.getInitPriority()).compareTo(Integer.valueOf(extension2.getInitPriority()));
		}
	}

	public static class ShutdownPriorityComparator implements Comparator<Extension<?>> {

		@Override
		public int compare(Extension<?> extension1, Extension<?> extension2) {

			return Integer.valueOf(extension1.getShutdownPriority()).compareTo(Integer.valueOf(extension2.getShutdownPriority()));
		}
	}
}
