package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;

/**
 * This interceptor listens to changes on a messaging target.
 * 
 * @author markus
 */
public class WriteListenerInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, OperationInterceptor, Prototype<WriteListenerInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(WriteListenerInterceptor.class);

	private final Map<XDIAddress, Set<WriteListener>> writeListeners;

	public WriteListenerInterceptor() {

		this.writeListeners = new HashMap<XDIAddress, Set<WriteListener>> ();
	}

	/*
	 * Prototype
	 */

	@Override
	public WriteListenerInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		resetWriteOperationsPerMessageEnvelope(executionContext);

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// create a fire map for all write listeners

		List<Operation> writeOperations = getWriteOperationsPerMessageEnvelope(executionContext);
		Map<WriteListener, List<XDIAddress>> fireMap = new HashMap<WriteListener, List<XDIAddress>> ();

		for (Operation writeOperation : writeOperations) {

			for (Entry<XDIAddress, Set<WriteListener>> entry : this.writeListeners.entrySet()) {

				XDIAddress writeListenerXDIAddress = entry.getKey();
				Set<WriteListener> writeListeners = entry.getValue();

				for (WriteListener writeListener : writeListeners) {

					// get the fire list for this write listener

					List<XDIAddress> fireList = fireMap.get(writeListener);

					if (fireList == null) {

						fireList = new ArrayList<XDIAddress> ();
						fireMap.put(writeListener, fireList);
					}

					XDIAddress targetXDIAddress = writeOperation.getTargetXDIAddress();
					Iterator<XDIStatement> targetXDIStatements = writeOperation.getTargetXDIStatements();

					// add the operation's target address(es) to the fire list

					if (targetXDIAddress != null) {

						if (XDIAddressUtil.startsWithXDIAddress(targetXDIAddress, writeListenerXDIAddress) != null) {

							fireList.add(targetXDIAddress);
						}
					}

					if (targetXDIStatements != null) {

						while (targetXDIStatements.hasNext()) {

							XDIStatement targetXDIStatement = targetXDIStatements.next();

							if (XDIAddressUtil.startsWithXDIAddress(targetXDIStatement.getContextNodeXDIAddress(), writeListenerXDIAddress) != null) {

								fireList.add(targetXDIStatement.getContextNodeXDIAddress());
							}
						}
					}
				}
			}
		}

		// notify write listeners

		for (Entry<WriteListener, List<XDIAddress>> entry : fireMap.entrySet()) {

			WriteListener writeListener = entry.getKey();
			List<XDIAddress> fireList = entry.getValue();

			if (log.isDebugEnabled()) log.debug("onWrite: " + writeListener.getClass().getName() + " on address list " + fireList);

			writeListener.onWrite(fireList);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex) {

	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// is this a write operation?

		if (! isWriteOperation(operation)) return InterceptorResult.DEFAULT;

		// add the write address

		addWriteOperationPerMessageEnvelope(executionContext, operation);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * WriteListener methods
	 */

	public void addWriteListener(XDIAddress writeListenerXDIAddress, WriteListener writeListener) {

		Set<WriteListener> writeListeners = this.writeListeners.get(writeListenerXDIAddress);

		if (writeListeners == null) {

			writeListeners = new HashSet<WriteListener> ();
			this.writeListeners.put(writeListenerXDIAddress, writeListeners);
		}

		writeListeners.add(writeListener);
	}

	public void removeWriteListener(XDIAddress writeListenerXDIAddress, WriteListener writeListener) {

		Set<WriteListener> writeListeners = this.writeListeners.get(writeListenerXDIAddress);
		if (writeListeners == null) return;

		writeListeners.remove(writeListener);

		if (writeListeners.isEmpty()) {

			this.writeListeners.remove(writeListenerXDIAddress);
		}
	}

	/*
	 * Helper methods
	 */

	private static boolean isWriteOperation(Operation operation) {

		if (operation instanceof GetOperation) return false;

		return true;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE = WriteListenerInterceptor.class.getCanonicalName() + "#writeoperationspermessageenvelope";

	@SuppressWarnings("unchecked")
	private static List<Operation> getWriteOperationsPerMessageEnvelope(ExecutionContext executionContext) {

		return (List<Operation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE);
	}

	private static void addWriteOperationPerMessageEnvelope(ExecutionContext executionContext, Operation operation) {

		List<Operation> operations = getWriteOperationsPerMessageEnvelope(executionContext);

		operations.add(operation);

		if (log.isDebugEnabled()) log.debug("Set operation: " + operation);
	}

	private static void resetWriteOperationsPerMessageEnvelope(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE, new ArrayList<Operation> ());
	}

	/*
	 * WriteListener
	 */

	public interface WriteListener {

		public void onWrite(List<XDIAddress> targetAddresses);
	}
}
