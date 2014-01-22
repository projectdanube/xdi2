package xdi2.messaging.target.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.MessageResultInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class InterceptorExecutor {

	private static final Logger log = LoggerFactory.getLogger(InterceptorExecutor.class);

	private InterceptorExecutor() {

	}

	/*
	 * Methods for executing interceptors
	 */

	public static InterceptorResult executeMessageEnvelopeInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (! messageEnvelopeInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: before");

				InterceptorResult interceptorResult = messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext);
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

	public static InterceptorResult executeMessageEnvelopeInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (! messageEnvelopeInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: after");

				InterceptorResult interceptorResult = messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext);
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

	public static void executeMessageEnvelopeInterceptorsException(InterceptorList<MessagingTarget> interceptorList, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Xdi2MessagingException ex) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = findMessageEnvelopeInterceptors(interceptorList); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (! messageEnvelopeInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: exception");

				messageEnvelopeInterceptor.exception(messageEnvelope, messageResult, executionContext, ex);
			} catch (Exception ex2) {

				throw executionContext.processException(ex2);
			} finally {

				executionContext.popInterceptor();
			}
		}
	}

	public static InterceptorResult executeMessageInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<MessageInterceptor> messageInterceptors = findMessageInterceptors(interceptorList); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (! messageInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: before");

				InterceptorResult interceptorResult = messageInterceptor.before(message, messageResult, executionContext);
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

	public static InterceptorResult executeMessageInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<MessageInterceptor> messageInterceptors = findMessageInterceptors(interceptorList); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (! messageInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: after");

				InterceptorResult interceptorResult = messageInterceptor.after(message, messageResult, executionContext);
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

	public static InterceptorResult executeOperationInterceptorsBefore(InterceptorList<MessagingTarget> interceptorList, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultBefore = InterceptorResult.DEFAULT;

		for (Iterator<OperationInterceptor> operationInterceptors = findOperationInterceptors(interceptorList); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (! operationInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: before");

				InterceptorResult interceptorResult = operationInterceptor.before(operation, operationMessageResult, executionContext);
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

	public static InterceptorResult executeOperationInterceptorsAfter(InterceptorList<MessagingTarget> interceptorList, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		InterceptorResult interceptorResultAfter = InterceptorResult.DEFAULT;

		for (Iterator<OperationInterceptor> operationInterceptors = findOperationInterceptors(interceptorList); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (! operationInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: after");

				InterceptorResult interceptorResult = operationInterceptor.after(operation, operationMessageResult, executionContext);
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

	public static XDI3Segment executeTargetInterceptorsAddress(InterceptorList<MessagingTarget> interceptorList, XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = findTargetInterceptors(interceptorList); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (! targetInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on address " + targetAddress + ".");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on address " + targetAddress + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: address: " + targetAddress);

				targetAddress = targetInterceptor.targetAddress(targetAddress, operation, messageResult, executionContext);

				if (targetAddress == null) {

					if (log.isDebugEnabled()) log.debug("Address has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned address: " + targetAddress + ".");
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetAddress;
	}

	public static XDI3Statement executeTargetInterceptorsStatement(InterceptorList<MessagingTarget> interceptorList, XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = findTargetInterceptors(interceptorList); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (! targetInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on statement " + targetStatement + ".");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on statement " + targetStatement + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: statement: " + targetStatement);

				targetStatement = targetInterceptor.targetStatement(targetStatement, operation, messageResult, executionContext);

				if (targetStatement == null) {

					if (log.isDebugEnabled()) log.debug("Statement has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned statement: " + targetStatement + ".");
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetStatement;
	}

	public static void executeResultInterceptorsFinish(InterceptorList<MessagingTarget> interceptorList, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageResultInterceptor> resultInterceptors = findResultInterceptors(interceptorList); resultInterceptors.hasNext(); ) {

			MessageResultInterceptor resultInterceptor = resultInterceptors.next();

			if (! resultInterceptor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");

			try {

				executionContext.pushInterceptor(resultInterceptor, "MessageResultInterceptor: finish");

				resultInterceptor.finish(messageResult, executionContext);
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

	public static Iterator<MessageResultInterceptor> findResultInterceptors(InterceptorList<MessagingTarget> interceptorList) {

		return interceptorList.findInterceptors(MessageResultInterceptor.class);
	}
}
