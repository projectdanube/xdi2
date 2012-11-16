package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor can replace XDI variables in a $add operation with automatically generated persistent XRI subsegments.
 * 
 * @author markus
 */
public class VariablesInterceptor extends AbstractInterceptor implements MessageEnvelopeInterceptor, TargetInterceptor, ResultInterceptor, Prototype<VariablesInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(VariablesInterceptor.class);

	/*
	 * Prototype
	 */

	@Override
	public VariablesInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

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

		XDI3Segment substitutedTargetSubject = substituteSegment(targetStatement.getSubject(), executionContext);
		XDI3Segment substitutedTargetPredicate = substituteSegment(targetStatement.getPredicate(), executionContext);
		XDI3Segment substitutedTargetObject = substituteSegment(targetStatement.getObject(), executionContext);

		if (substitutedTargetSubject == targetStatement.getSubject() && substitutedTargetPredicate == targetStatement.getPredicate() && substitutedTargetObject == targetStatement.getObject()) return targetStatement;

		return StatementUtil.fromComponents(substitutedTargetSubject, substitutedTargetPredicate, substitutedTargetObject);
	}

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof AddOperation)) return targetAddress;

		return substituteSegment(targetAddress, executionContext);
	}

	/*
	 * ResultInterceptor
	 */

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// add $is statements for all the substituted variables

		for (Entry<XDI3SubSegment, XDI3SubSegment> entry : getVariables(executionContext).entrySet()) {

			XDI3Segment subject = new XDI3Segment(entry.getKey().toString());
			XDI3Segment predicate = XDIDictionaryConstants.XRI_S_IS;
			XDI3Segment object = new XDI3Segment(entry.getValue().toString());

			Statement statement = StatementUtil.fromComponents(subject, predicate, object);

			messageResult.getGraph().addStatement(statement);
		}
	}

	/*
	 * Substitution helper methods
	 */

	private static XDI3Segment substituteSegment(XDI3Segment segment, ExecutionContext executionContext) {

		List<XDI3SubSegment> substitutedSubSegments = null;

		// substitute segment

		for (int i=0; i<segment.getNumSubSegments(); i++) {

			XDI3SubSegment subSegment = (XDI3SubSegment) segment.getSubSegment(i);
			XDI3SubSegment substitutedSubSegment = substituteSubSegment(subSegment, executionContext);

			if (substitutedSubSegment == null) continue;

			if (log.isDebugEnabled()) log.debug("Substituted " + subSegment + " for " + substitutedSubSegment);

			// substitute subsegment

			if (substitutedSubSegments == null) {

				substitutedSubSegments = new ArrayList<XDI3SubSegment> (segment.getNumSubSegments());
				for (int ii=0; ii<segment.getNumSubSegments(); ii++) substitutedSubSegments.add((XDI3SubSegment) segment.getSubSegment(ii));
			}

			substitutedSubSegments.set(i, substitutedSubSegment);
		}

		// no substitutions?

		if (substitutedSubSegments == null) return segment;

		// build new target address

		StringBuilder newTargetAddress = new StringBuilder();
		for (XDI3SubSegment subSegment : substitutedSubSegments) newTargetAddress.append(subSegment.toString());

		return new XDI3Segment(newTargetAddress.toString());
	}

	private static XDI3SubSegment substituteSubSegment(XDI3SubSegment subSegment, ExecutionContext executionContext) {

		// we remember the multiplicity of the subsegment

		boolean entityMember = Multiplicity.isEntityMemberArcXri(subSegment);
		boolean attributeMember = Multiplicity.isAttributeMemberArcXri(subSegment);

		XDI3SubSegment baseSubSegment;

		if (entityMember || attributeMember) {

			baseSubSegment = Multiplicity.baseArcXri(subSegment);
			if (baseSubSegment.hasXRef()) baseSubSegment = new XDI3SubSegment("" + baseSubSegment.toString().substring(1));
		} else {

			baseSubSegment = subSegment;
		}

		if (log.isDebugEnabled()) log.debug("entityMember: " + entityMember + ", attributeMember: " + attributeMember + ", baseSubSegment: " + baseSubSegment);

		if (! Variables.isVariableSingle(baseSubSegment)) return null;

		// substitute the base subsegment (without multiplicity)

		XDI3SubSegment newBaseSubSegment = getVariable(executionContext, baseSubSegment);

		if (newBaseSubSegment == null) {

			newBaseSubSegment = XRIUtil.randomSubSegment("" + XRI3Constants.LCS_BANG);
			putVariable(executionContext, baseSubSegment, newBaseSubSegment);
		}

		// re-apply multiplicity to the substitution

		XDI3SubSegment newSubSegment;

		if (entityMember) newSubSegment = Multiplicity.entityMemberArcXri(newBaseSubSegment);
		else if (attributeMember) newSubSegment = Multiplicity.attributeMemberArcXri(newBaseSubSegment);
		else newSubSegment = newBaseSubSegment;

		// done

		return newSubSegment;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE = VariablesInterceptor.class.getCanonicalName() + "#variablespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Map<XDI3SubSegment, XDI3SubSegment> getVariables(ExecutionContext executionContext) {

		return (Map<XDI3SubSegment, XDI3SubSegment>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE);
	}

	private static XDI3SubSegment getVariable(ExecutionContext executionContext, XDI3SubSegment key) {

		return getVariables(executionContext).get(key);
	}

	private static void putVariable(ExecutionContext executionContext, XDI3SubSegment key, XDI3SubSegment value) {

		getVariables(executionContext).put(key, value);
	}

	private static void resetVariables(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE, new HashMap<XDI3SubSegment, XDI3SubSegment> ());
	}
}
