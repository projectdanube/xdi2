package xdi2.messaging.target.interceptor.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;

/**
 * This interceptor will execute a command line
 * on an incoming message envelope, message, or operation.
 */
public class ExecuteCommandInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, MessageInterceptor, OperationInterceptor, Prototype<ExecuteCommandInterceptor> {

	private static Logger log = LoggerFactory.getLogger(ExecuteCommandInterceptor.class.getName());

	private String commandLine;
	private boolean enableMessageEnvelope;
	private boolean enableMessage;
	private boolean enableOperation;
	private boolean waitFor;

	public ExecuteCommandInterceptor() {

		this.enableMessageEnvelope = false;
		this.enableMessage = false;
		this.enableOperation = false;
		this.waitFor = false;
	}

	/*
	 * Prototype
	 */

	@Override
	public ExecuteCommandInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (this.isEnableMessageEnvelope()) {

			String param = messageEnvelope.getGraph().toString("XDI/JSON", null);
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (this.isEnableMessage()) {

			String param = message.getContextNode().getXDIAddress().toString();
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (this.isEnableOperation()) {

			String param = operation.getOperationXDIAddress().toString();
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Helper
	 */

	private void executeCommandLine(String param, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Executing command " + this.getCommandLine() + " with parameter " + param);

		try {

			Process process = Runtime.getRuntime().exec(this.getCommandLine(), new String[] { param });

			if (this.isWaitFor()) {

				int exitValue = process.waitFor();

				if (log.isDebugEnabled()) log.debug("Exit value " + exitValue);
			}
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Cannot execute command line: " + param, ex, executionContext);
		} catch (InterruptedException ex) {

			throw new Xdi2MessagingException("Interrupted: " + param, ex, executionContext);
		}
	}

	/*
	 * Getters and setters
	 */

	public String getCommandLine() {

		return this.commandLine;
	}

	public void setCommandLine(String commandLine) {

		this.commandLine = commandLine;
	}

	public boolean isEnableMessageEnvelope() {

		return this.enableMessageEnvelope;
	}

	public void setEnableMessageEnvelope(boolean enableMessageEnvelope) {

		this.enableMessageEnvelope = enableMessageEnvelope;
	}

	public boolean isEnableMessage() {

		return this.enableMessage;
	}

	public void setEnableMessage(boolean enableMessage) {

		this.enableMessage = enableMessage;
	}

	public boolean isEnableOperation() {

		return this.enableOperation;
	}

	public void setEnableOperation(boolean enableOperation) {

		this.enableOperation = enableOperation;
	}

	public boolean isWaitFor() {

		return this.waitFor;
	}

	public void setWaitFor(boolean waitFor) {

		this.waitFor = waitFor;
	}
}
