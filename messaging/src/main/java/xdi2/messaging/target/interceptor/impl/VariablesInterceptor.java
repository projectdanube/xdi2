package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import xdi2.core.Statement;
import xdi2.core.dictionary.util.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.features.variables.Variables;
import xdi2.core.impl.AbstractStatement;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphExecutionContext;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class VariablesInterceptor implements TargetInterceptor, ResultInterceptor {

	@Override
	public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment subject = substituteSegment(targetStatement.getSubject(), executionContext);
		XRI3Segment predicate = substituteSegment(targetStatement.getPredicate(), executionContext);
		XRI3Segment object = substituteSegment(targetStatement.getObject(), executionContext);

		return AbstractStatement.fromComponents(subject, predicate, object);
	}

	@Override
	public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		return substituteSegment(targetAddress, executionContext);
	}

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// add $is statements for all the substituted variables

		for (Entry<XRI3SubSegment, XRI3SubSegment> entry : GraphExecutionContext.getVariablesPerMessageEnvelope(executionContext).entrySet()) {

			XRI3Segment subject = new XRI3Segment(entry.getKey().toString());
			XRI3Segment predicate = XDIDictionaryConstants.XRI_S_IS;
			XRI3Segment object = new XRI3Segment(entry.getValue().toString());

			Statement statement = AbstractStatement.fromComponents(subject, predicate, object);

			messageResult.getGraph().addStatement(statement);
		}
	}

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

		XRI3SubSegment newSubSegment = GraphExecutionContext.getVariablesPerMessageEnvelope(executionContext).get(subSegment);

		if (newSubSegment == null) {

			newSubSegment = XRIUtil.randomHEXSubSegment("$!");
			GraphExecutionContext.getVariablesPerMessageEnvelope(executionContext).put(subSegment, newSubSegment);
		}

		return newSubSegment;
	}
}
