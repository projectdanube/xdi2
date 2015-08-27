package xdi2.messaging.target.interceptor.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;

/**
 * This interceptor handles $has relations.
 * 
 * @author markus
 */
public class HasInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor, Prototype<HasInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(HasInterceptor.class);

	/*
	 * Prototype
	 */

	@Override
	public HasInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (! (messagingTarget instanceof AbstractMessagingTarget)) throw new Xdi2MessagingException("Can only add this interceptor to an AbstractMessagingTarget", null, null);
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to process result $has relations

		if (operation instanceof GetOperation && operation.getTargetXDIAddress() != null) {

			List<Relation> hasRelations = new IteratorListMaker<Relation> (Equivalence.getAllReferenceAndReplacementRelations(operationResultGraph.getRootContextNode(true))).list();

			for (Relation hasRelation : hasRelations) {

				if (log.isDebugEnabled()) log.debug("In message result: Found $has relation: " + hasRelation);

				XDIAddress hasTargetXDIAddress = hasRelation.getTargetXDIAddress();

				// $get feedback on the target of the $has relation

				Graph feedbackResultGraph = feedbackGetTargetOfHasRelation(hasTargetXDIAddress, operation, executionContext);

				// merge the message result

				CopyUtil.copyGraph(feedbackResultGraph, operationResultGraph, null);

				// done with this $has relation

				if (log.isDebugEnabled()) log.debug("In message result: After $get feedback on $has relation " + hasRelation + " we now have: " + operationResultGraph);
			}
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Feedback methods
	 */

	private static Graph feedbackGetTargetOfHasRelation(XDIAddress hasTargetXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to get target of $has relation: " + hasTargetXDIAddress);

		// prepare messaging target

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		// prepare feedback message

		MessageEnvelope feedbackMessageEnvelope = new MessageEnvelope();

		Message feedbackMessage = feedbackMessageEnvelope.createMessage(operation.getSenderXDIAddress());
		feedbackMessage.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());

		Operation feedbackOperation = feedbackMessage.createGetOperation(hasTargetXDIAddress);
		if (Boolean.TRUE.equals(operation.getParameterBoolean(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF))) feedbackOperation.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF, Boolean.TRUE);

		// prepare feedback execution result

		ExecutionResult feedbackExecutionResult = ExecutionResult.createExecutionResult(feedbackMessageEnvelope);

		// feedback

		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			LinkContractInterceptor linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(feedbackMessage);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback messages

			messagingTarget.execute(feedbackMessage, executionContext, feedbackExecutionResult);
		} finally {

			// after feedback: restore the execution context and messaging target

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// finish feedback execution result

		feedbackExecutionResult.finish();

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback on target of $ref/$rep relation: " + hasTargetXDIAddress + ", execution result: " + feedbackExecutionResult);

		return feedbackExecutionResult.getFinishedResultGraph();
	}
}
