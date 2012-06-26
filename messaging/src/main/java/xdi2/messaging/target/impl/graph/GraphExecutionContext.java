package xdi2.messaging.target.impl.graph;

import java.util.Map;

import javax.security.auth.Subject;

import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.target.ExecutionContext;

public class GraphExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION = GraphExecutionContext.class.getCanonicalName() + "#affectedsubjectsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE = GraphExecutionContext.class.getCanonicalName() + "#affectedsubjectspermessageenvelope";
	private static final String EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE = GraphExecutionContext.class.getCanonicalName() + "#variablespermessageenvelope";

	private GraphExecutionContext() { }

	@SuppressWarnings("unchecked")
	public static Map<XRI3, Subject> getAffectedSubjectsPerOperation(ExecutionContext executionContext) {

		return (Map<XRI3, Subject>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION);
	}

	public static void setAffectedSubjectsPerOperation(ExecutionContext executionContext, Map<XRI3, Subject> affectedSubjectsPerOperation) {

		executionContext.setOperationAttribute(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION, affectedSubjectsPerOperation);
	}

	@SuppressWarnings("unchecked")
	public static Map<XRI3, Subject> getAffectedSubjectsPerMessageEnvelope(ExecutionContext executionContext) {

		return (Map<XRI3, Subject>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE);
	}

	public static void setAffectedSubjectsPerMessageEnvelope(ExecutionContext executionContext, Map<XRI3, Subject> affectedSubjectsPerMessageEnvelope) {

		executionContext.setMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE, affectedSubjectsPerMessageEnvelope);
	}

	@SuppressWarnings("unchecked")
	public static Map<XRI3SubSegment, XRI3SubSegment> getVariablesPerMessageEnvelope(ExecutionContext executionContext) {

		return (Map<XRI3SubSegment, XRI3SubSegment>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE);
	}

	public static void setVariablesPerMessageEnvelope(ExecutionContext executionContext, Map<XRI3SubSegment, XRI3SubSegment> variablesPerMessageEnvelope) {

		executionContext.setMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_VARIABLES_PER_MESSAGEENVELOPE, variablesPerMessageEnvelope);
	}
}
