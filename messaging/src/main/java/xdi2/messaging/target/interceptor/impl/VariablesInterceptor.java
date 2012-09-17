package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xdi2.core.Statement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor can replace XDI variables in a $add operation with automatically generated persistent XRI subsegments.
 * 
 * @author markus
 */
public class VariablesInterceptor extends AbstractInterceptor implements MessageEnvelopeInterceptor, TargetInterceptor, ResultInterceptor {

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetVariables(executionContext);

		return false;
	}

	@Override
	public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public Statement targetStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof AddOperation)) return targetStatement;

		XRI3Segment subject = substituteSegment(targetStatement.getSubject(), executionContext);
		XRI3Segment predicate = substituteSegment(targetStatement.getPredicate(), executionContext);
		XRI3Segment object = substituteSegment(targetStatement.getObject(), executionContext);

		if (subject == targetStatement.getSubject() && predicate == targetStatement.getPredicate() && object == targetStatement.getObject()) return targetStatement;

		return StatementUtil.fromComponents(subject, predicate, object);
	}

	@Override
	public XRI3Segment targetAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof AddOperation)) return targetAddress;

		return substituteSegment(targetAddress, executionContext);
	}

	/*
	 * ResultInterceptor
	 */

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// add $is statements for all the substituted variables

		for (Entry<XRI3SubSegment, XRI3SubSegment> entry : getVariables(executionContext).entrySet()) {

			XRI3Segment subject = new XRI3Segment(entry.getKey().toString());
			XRI3Segment predicate = XDIDictionaryConstants.XRI_S_IS;
			XRI3Segment object = new XRI3Segment(entry.getValue().toString());

			Statement statement = StatementUtil.fromComponents(subject, predicate, object);

			messageResult.getGraph().addStatement(statement);
		}
	}

	/*
	 * Substitution helper methods
	 */

	private static XRI3Segment substituteSegment(XRI3Segment segment, ExecutionContext executionContext) {

		List<XRI3SubSegment> newSubSegments = null;

		// substitute segment

		for (int i=0; i<segment.getNumSubSegments(); i++) {

			XRI3SubSegment subSegment = (XRI3SubSegment) segment.getSubSegment(i);
			if (! Variables.isVariable(subSegment)) continue;

			if (newSubSegments == null) {

				newSubSegments = new ArrayList<XRI3SubSegment> (segment.getNumSubSegments());
				for (int ii=0; ii<segment.getNumSubSegments(); ii++) newSubSegments.add((XRI3SubSegment) segment.getSubSegment(ii));
			}

			// substitute subsegment

			newSubSegments.set(i, substituteSubSegment(subSegment, executionContext));
		}

		// no substitutions?

		if (newSubSegments == null) return segment;

		// build new target address

		StringBuilder newTargetAddress = new StringBuilder();
		for (XRI3SubSegment subSegment : newSubSegments) newTargetAddress.append(subSegment.toString());

		return new XRI3Segment(newTargetAddress.toString());
	}

	private static XRI3SubSegment substituteSubSegment(XRI3SubSegment subSegment, ExecutionContext executionContext) {

		XRI3SubSegment newSubSegment = getVariable(executionContext, subSegment);

		if (newSubSegment == null) {

			newSubSegment = XRIUtil.randomSubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG);
			putVariable(executionContext, subSegment, newSubSegment);
		}

		return newSubSegment;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE = VariablesInterceptor.class.getCanonicalName() + "#variablespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Map<XRI3SubSegment, XRI3SubSegment> getVariables(ExecutionContext executionContext) {

		return (Map<XRI3SubSegment, XRI3SubSegment>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE);
	}

	private static XRI3SubSegment getVariable(ExecutionContext executionContext, XRI3SubSegment key) {

		return getVariables(executionContext).get(key);
	}

	private static void putVariable(ExecutionContext executionContext, XRI3SubSegment key, XRI3SubSegment value) {

		getVariables(executionContext).put(key, value);
	}

	private static void resetVariables(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE, new HashMap<XRI3SubSegment, XRI3SubSegment> ());
	}
}
