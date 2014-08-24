package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.VariableUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor can replace XDI variables in a $add operation with automatically generated persistent addresses.
 * 
 * @author markus
 */
public class VariablesInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, TargetInterceptor, MessageResultInterceptor, Prototype<VariablesInterceptor> {

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
	public InterceptorResult before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetVariables(executionContext);

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
	 * TargetInterceptor
	 */

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof SetOperation)) return targetStatement;

		XDIAddress substitutedTargetSubject = substituteAddress(targetStatement.getSubject(), executionContext);
		XDIAddress substitutedTargetPredicate = substituteAddress(targetStatement.getPredicate(), executionContext);
		Object substitutedTargetObject = substituteObject(targetStatement.getObject(), executionContext);

		if (substitutedTargetSubject == targetStatement.getSubject() && substitutedTargetPredicate == targetStatement.getPredicate() && substitutedTargetObject == targetStatement.getObject()) return targetStatement;

		return XDIStatement.fromComponents(substitutedTargetSubject, substitutedTargetPredicate, substitutedTargetObject);
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof SetOperation)) return targetAddress;

		return substituteAddress(targetAddress, executionContext);
	}

	/*
	 * MessageResultInterceptor
	 */

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// add $is statements for all the substituted variables

		for (Entry<XDIArc, XDIArc> entry : getVariables(executionContext).entrySet()) {

			XDIAddress subject = XDIAddress.create(entry.getKey().toString());
			XDIAddress predicate = XDIDictionaryConstants.XDI_ADD_IS;
			XDIAddress object = XDIAddress.create(entry.getValue().toString());

			XDIStatement statement = XDIStatement.fromComponents(subject, predicate, object);

			messageResult.getGraph().setStatement(statement);
		}
	}

	/*
	 * Substitution helper methods
	 */

	private static XDIAddress substituteAddress(XDIAddress address, ExecutionContext executionContext) {

		List<XDIArc> substitutedArcs = null;

		// substitute address

		for (int i=0; i<address.getNumXDIArcs(); i++) {

			XDIArc arc = address.getXDIArc(i);
			XDIArc substitutedArc = substituteArc(arc, executionContext);

			if (substitutedArc == null) continue;

			if (log.isDebugEnabled()) log.debug("Substituted " + arc + " for " + substitutedArc);

			// substitute address

			if (substitutedArcs == null) {

				substitutedArcs = new ArrayList<XDIArc> (address.getNumXDIArcs());
				for (int ii=0; ii<address.getNumXDIArcs(); ii++) substitutedArcs.add(address.getXDIArc(ii));
			}

			substitutedArcs.set(i, substitutedArc);
		}

		// no substitutions?

		if (substitutedArcs == null) return address;

		// build new target address

		StringBuilder newTargetAddress = new StringBuilder();
		for (XDIArc substitutedArc : substitutedArcs) newTargetAddress.append(substitutedArc.toString());

		return XDIAddress.create(newTargetAddress.toString());
	}

	private static Object substituteObject(Object object, ExecutionContext executionContext) {

		if (! (object instanceof XDIAddress)) return object;

		return substituteAddress((XDIAddress) object, executionContext);
	}

	private static XDIArc substituteArc(XDIArc arc, ExecutionContext executionContext) {

		if (! VariableUtil.isVariable(arc)) return null;
		if (! arc.getXRef().isEmpty()) return null;

		// substitute the arc

		XDIArc newArc = getVariable(executionContext, arc);

		if (newArc == null) {

			newArc = XdiAbstractMemberUnordered.createRandomUuidXDIArc(XdiEntityCollection.class);
			putVariable(executionContext, arc, newArc);
		}

		// done

		return newArc;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE = VariablesInterceptor.class.getCanonicalName() + "#variablespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Map<XDIArc, XDIArc> getVariables(ExecutionContext executionContext) {

		return (Map<XDIArc, XDIArc>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE);
	}

	private static XDIArc getVariable(ExecutionContext executionContext, XDIArc key) {

		return getVariables(executionContext).get(key);
	}

	private static void putVariable(ExecutionContext executionContext, XDIArc key, XDIArc value) {

		getVariables(executionContext).put(key, value);
	}

	private static void resetVariables(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE, new HashMap<XDIArc, String> ());
	}
}
