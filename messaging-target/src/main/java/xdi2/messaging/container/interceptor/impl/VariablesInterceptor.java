package xdi2.messaging.container.interceptor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.container.interceptor.OperationInterceptor;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;

/**
 * This interceptor can replace XDI variables in a $add operation with automatically generated persistent addresses.
 * 
 * @author markus
 */
public class VariablesInterceptor extends AbstractInterceptor<MessagingContainer> implements MessageEnvelopeInterceptor, OperationInterceptor, TargetInterceptor, Prototype<VariablesInterceptor> {

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
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		resetVariablesPerMessageEnvelope(executionContext);

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
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetVariablesPerOperation(executionContext);
		
		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// add $is statements for all the substituted variables

		for (Entry<XDIArc, XDIArc> entry : getVariablesPerOperation(executionContext).entrySet()) {

			XDIAddress subject = XDIAddress.create(entry.getKey().toString());
			XDIAddress predicate = XDIDictionaryConstants.XDI_ADD_IS;
			XDIAddress object = XDIAddress.create(entry.getValue().toString());

			XDIStatement statement = XDIStatement.fromComponents(subject, predicate, object);

			operationResultGraph.setStatement(statement);
		}

		// done
		
		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof SetOperation)) return targetStatement;

		XDIAddress substitutedTargetSubject = substituteAddress(targetStatement.getSubject(), executionContext);
		Object substitutedTargetPredicate = targetStatement.getPredicate() instanceof XDIAddress ? substituteAddress((XDIAddress) targetStatement.getPredicate(), executionContext) : targetStatement.getPredicate();
		Object substitutedTargetObject = substituteObject(targetStatement.getObject(), executionContext);

		if (substitutedTargetSubject == targetStatement.getSubject() && substitutedTargetPredicate == targetStatement.getPredicate() && substitutedTargetObject == targetStatement.getObject()) return targetStatement;

		return XDIStatement.fromComponents(substitutedTargetSubject, substitutedTargetPredicate, substitutedTargetObject);
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (operation instanceof SetOperation)) return targetAddress;

		return substituteAddress(targetAddress, executionContext);
	}

	/*
	 * Substitution helper methods
	 */

	private static XDIAddress substituteAddress(XDIAddress XDIaddress, ExecutionContext executionContext) {

		List<XDIArc> substitutedXDIArcs = null;

		// substitute address

		for (int i=0; i<XDIaddress.getNumXDIArcs(); i++) {

			XDIArc XDIarc = XDIaddress.getXDIArc(i);
			XDIArc substitutedXDIArc = substituteXDIArc(XDIarc, executionContext);

			if (substitutedXDIArc == null) continue;

			if (log.isDebugEnabled()) log.debug("Substituted " + XDIarc + " for " + substitutedXDIArc);

			// substitute arc

			if (substitutedXDIArcs == null) {

				substitutedXDIArcs = new ArrayList<XDIArc> (XDIaddress.getNumXDIArcs());
				for (int ii=0; ii<XDIaddress.getNumXDIArcs(); ii++) substitutedXDIArcs.add(XDIaddress.getXDIArc(ii));
			}

			substitutedXDIArcs.set(i, substitutedXDIArc);
		}

		// no substitutions?

		if (substitutedXDIArcs == null) return XDIaddress;

		// build new target address

		StringBuilder newTargetAddress = new StringBuilder();
		for (XDIArc substitutedArc : substitutedXDIArcs) newTargetAddress.append(substitutedArc.toString());

		return XDIAddress.create(newTargetAddress.toString());
	}

	private static Object substituteObject(Object object, ExecutionContext executionContext) {

		if (! (object instanceof XDIAddress)) return object;

		return substituteAddress((XDIAddress) object, executionContext);
	}

	private static XDIArc substituteXDIArc(XDIArc XDIarc, ExecutionContext executionContext) {

		if (! XDIConstants.XDI_ADD_COMMON_VARIABLE.equals(XDIarc)) return null;
		if (! XDIarc.getXRef().isEmpty()) return null;

		// substitute the arc

		XDIArc newArc = getVariablesPerMessageEnvelope(executionContext).get(XDIarc);

		if (newArc == null) {

			newArc = XdiEntityInstanceUnordered.createXDIArc();
			putVariablePerMessageEnvelope(executionContext, XDIarc, newArc);
			putVariablePerOperation(executionContext, XDIarc, newArc);
		}

		// done

		return newArc;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE = VariablesInterceptor.class.getCanonicalName() + "#variablespermessageenvelope";
	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_OPERATION = VariablesInterceptor.class.getCanonicalName() + "#variablesperoperation";

	@SuppressWarnings("unchecked")
	private static Map<XDIArc, XDIArc> getVariablesPerMessageEnvelope(ExecutionContext executionContext) {

		return (Map<XDIArc, XDIArc>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE);
	}

	private static void putVariablePerMessageEnvelope(ExecutionContext executionContext, XDIArc key, XDIArc value) {

		getVariablesPerMessageEnvelope(executionContext).put(key, value);
	}

	private static void resetVariablesPerMessageEnvelope(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE, new HashMap<XDIArc, String> ());
	}

	@SuppressWarnings("unchecked")
	private static Map<XDIArc, XDIArc> getVariablesPerOperation(ExecutionContext executionContext) {

		return (Map<XDIArc, XDIArc>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_OPERATION);
	}

	private static void putVariablePerOperation(ExecutionContext executionContext, XDIArc key, XDIArc value) {

		getVariablesPerOperation(executionContext).put(key, value);
	}

	private static void resetVariablesPerOperation(ExecutionContext executionContext) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_OPERATION, new HashMap<XDIArc, String> ());
	}
}
