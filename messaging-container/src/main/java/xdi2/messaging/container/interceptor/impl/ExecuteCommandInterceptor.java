package xdi2.messaging.container.interceptor.impl;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.container.interceptor.MessageInterceptor;
import xdi2.messaging.container.interceptor.OperationInterceptor;
import xdi2.messaging.operations.Operation;

/**
 * This interceptor will execute a command line
 * on an incoming message envelope, message, or operation.
 */
public class ExecuteCommandInterceptor extends AbstractInterceptor<MessagingContainer> implements MessageEnvelopeInterceptor, MessageInterceptor, OperationInterceptor, Prototype<ExecuteCommandInterceptor> {

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
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (this.isEnableMessageEnvelope()) {

			StringWriter stringWriter = new StringWriter();

			try {

				XDIWriterRegistry.getDefault().write(messageEnvelope.getGraph(), stringWriter);
			} catch (IOException ex) {

				throw new Xdi2MessagingException("Cannot write graph: " + ex.getMessage(), ex, executionContext);
			}

			String param = stringWriter.getBuffer().toString();
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex) {

	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (this.isEnableMessage()) {

			String param = message.getContextNode().getXDIAddress().toString();
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (this.isEnableOperation()) {

			String param = operation.getOperationXDIAddress().toString();
			this.executeCommandLine(param, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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
