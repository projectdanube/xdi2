package xdi2.messaging.target.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2PushRequiredException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.ResultGraphInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class InterceptorExecutor {

	private static final Logger log = LoggerFactory.getLogger(InterceptorExecutor.class);

	private InterceptorExecutor() {

	}

	/*
	 * Methods for executing interceptors
	 */

	public static InterceptorResult executeMessageEnvelopeInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (messageEnvelopeInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: before");

				InterceptorResult interceptorResult = messageEnvelopeInterceptor.before(messageEnvelope, executionResult, executionContext);
				interceptorResultBefore = interceptorResultBefore.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling message envelope interceptors (before) according to " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultBefore;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultBefore;
	}

	public static InterceptorResult executeMessageEnvelopeInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (messageEnvelopeInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: after");

				InterceptorResult interceptorResult = messageEnvelopeInterceptor.after(messageEnvelope, executionResult, executionContext);
				interceptorResultAfter = interceptorResultAfter.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling message envelope interceptors (after) according to " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultAfter;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultAfter;
	}

	public static void executeMessageEnvelopeInterceptorsException(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Xdi2MessagingException ex) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (messageEnvelopeInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: exception");

				messageEnvelopeInterceptor.exception(messageEnvelope, executionResult, executionContext, ex);
			} catch (Exception ex2) {

				throw executionContext.processException(ex2);
			} finally {

				executionContext.popInterceptor();
			}
		}
	}

	public static InterceptorResult executeMessageInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<MessageInterceptor> messageInterceptors = findMessageInterceptors(interceptorList); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (messageInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: before");

				InterceptorResult interceptorResult = messageInterceptor.before(message, executionResult, executionContext);
				interceptorResultBefore = interceptorResultBefore.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling message interceptors (before) according to " + messageInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultBefore;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultBefore;
	}

	public static InterceptorResult executeMessageInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<MessageInterceptor> messageInterceptors = findMessageInterceptors(interceptorList); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (messageInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: after");

				InterceptorResult interceptorResult = messageInterceptor.after(message, executionResult, executionContext);
				interceptorResultAfter = interceptorResultAfter.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling message interceptors (after) according to " + messageInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultAfter;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultAfter;
	}

	public static InterceptorResult executeOperationInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<OperationInterceptor> operationInterceptors = findOperationInterceptors(interceptorList); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (operationInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: before");

				InterceptorResult interceptorResult = operationInterceptor.before(operation, operationResultGraph, executionContext);
				interceptorResultBefore = interceptorResultBefore.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling operation interceptors (before) according to " + operationInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultBefore;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultBefore;
	}

	public static InterceptorResult executeOperationInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<OperationInterceptor> operationInterceptors = findOperationInterceptors(interceptorList); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (operationInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: after");

				InterceptorResult interceptorResult = operationInterceptor.after(operation, operationResultGraph, executionContext);
				interceptorResultAfter = interceptorResultAfter.or(interceptorResult);

				if (interceptorResult.isSkipSiblingInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling operation interceptors (after) according to " + operationInterceptor.getClass().getSimpleName() + ".");
					return interceptorResultAfter;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return interceptorResultAfter;
	}

	public static XDIAddress executeTargetInterceptorsAddress(InterceptorList<MessagingTarget> interceptorList, XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException {

		for (Iterator<TargetInterceptor> targetInterceptors = findTargetInterceptors(interceptorList); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (targetInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on address " + targetAddress + ".");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on address " + targetAddress + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: address: " + targetAddress);

				targetAddress = targetInterceptor.targetAddress(targetAddress, operation, operationResultGraph, executionContext);

				if (targetAddress == null) {

					if (log.isDebugEnabled()) log.debug("Address has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned address: " + targetAddress + ".");
			} catch (Xdi2PushRequiredException ex) {

				throw ex;
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetAddress;
	}

	public static XDIStatement executeTargetInterceptorsStatement(InterceptorList<MessagingTarget> interceptorList, XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException {

		for (Iterator<TargetInterceptor> targetInterceptors = findTargetInterceptors(interceptorList); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (targetInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on statement " + targetStatement + ".");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on statement " + targetStatement + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: statement: " + targetStatement);

				targetStatement = targetInterceptor.targetStatement(targetStatement, operation, operationResultGraph, executionContext);

				if (targetStatement == null) {

					if (log.isDebugEnabled()) log.debug("Statement has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned statement: " + targetStatement + ".");
			} catch (Xdi2PushRequiredException ex) {

				throw ex;
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetStatement;
	}

	public static void executeResultGraphInterceptorsFinish(InterceptorList<MessagingTarget> interceptorList, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<ResultGraphInterceptor> resultInterceptors = findResultInterceptors(interceptorList); resultInterceptors.hasNext(); ) {

			ResultGraphInterceptor resultInterceptor = resultInterceptors.next();

			if (resultInterceptor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");

			try {

				executionContext.pushInterceptor(resultInterceptor, "MessageResultInterceptor: finish");

				resultInterceptor.finish(executionResult, executionContext);
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}
	}
	
	/*
	 * Methods for finding interceptors
	 */

	public static Iterator<MessageEnvelopeInterceptor> findMessageEnvelopeInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(MessageEnvelopeInterceptor.class);
	}

	public static Iterator<MessageInterceptor> findMessageInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(MessageInterceptor.class);
	}

	public static Iterator<OperationInterceptor> findOperationInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(OperationInterceptor.class);
	}

	public static Iterator<TargetInterceptor> findTargetInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(TargetInterceptor.class);
	}

	public static Iterator<ResultGraphInterceptor> findResultInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(ResultGraphInterceptor.class);
	}
}
