package xdi2.server.impl.graph;

import java.util.Map;

import javax.security.auth.Subject;
import javax.sql.rowset.Predicate;

import xdi2.server.ExecutionContext;
import xdi2.xri3.impl.XRI3;
import xdi2.xri3.impl.XRI3Segment;

public class GraphExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION = GraphExecutionContext.class.getCanonicalName() + "#affectedsubjectsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE = GraphExecutionContext.class.getCanonicalName() + "#affectedsubjectspermessageenvelope";
	private static final String EXECUTIONCONTEXT_KEY_VARIABLE_SUBJECTS_PER_OPERATION = GraphExecutionContext.class.getCanonicalName() + "#variablesubjectsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_VARIABLE_PREDICATES_PER_OPERATION = GraphExecutionContext.class.getCanonicalName() + "#variablepredicatesperoperation";

	private GraphExecutionContext() { }

	@SuppressWarnings("unchecked")
	public static Map<XRI3, Subject> getAffectedSubjectsPerOperation(ExecutionContext executionContext) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		return (Map<XRI3, Subject>) operationAttributes.get(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION);
	}

	public static void setAffectedSubjectsPerOperation(ExecutionContext executionContext, Map<XRI3, Subject> affectedSubjectsPerOperation) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		operationAttributes.put(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_OPERATION, affectedSubjectsPerOperation);
	}

	@SuppressWarnings("unchecked")
	public static Map<XRI3, Subject> getAffectedSubjectsPerMessageEnvelope(ExecutionContext executionContext) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		return (Map<XRI3, Subject>) messageEnvelopeAttributes.get(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE);
	}

	public static void setAffectedSubjectsPerMessageEnvelope(ExecutionContext executionContext, Map<XRI3, Subject> affectedSubjectsPerMessageEnvelope) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		messageEnvelopeAttributes.put(EXECUTIONCONTEXT_KEY_AFFECTED_SUBJECTS_PER_MESSAGEENVELOPE, affectedSubjectsPerMessageEnvelope);
	}

	@SuppressWarnings("unchecked")
	public static Map<XRI3Segment, Subject> getVariableSubjectsPerOperation(ExecutionContext executionContext) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		return (Map<XRI3Segment, Subject>) operationAttributes.get(EXECUTIONCONTEXT_KEY_VARIABLE_SUBJECTS_PER_OPERATION);
	}

	public static void setVariableSubjectsPerOperation(ExecutionContext executionContext, Map<XRI3Segment, Subject> variableSubjectsPerOperation) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		operationAttributes.put(EXECUTIONCONTEXT_KEY_VARIABLE_SUBJECTS_PER_OPERATION, variableSubjectsPerOperation);
	}

	@SuppressWarnings("unchecked")
	public static Map<XRI3Segment, Predicate> getVariablePredicatesPerOperation(ExecutionContext executionContext) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		return (Map<XRI3Segment, Predicate>) operationAttributes.get(EXECUTIONCONTEXT_KEY_VARIABLE_PREDICATES_PER_OPERATION);
	}

	public static void setVariablePredicatesPerOperation(ExecutionContext executionContext, Map<XRI3Segment, Predicate> variablePredicatesPerOperation) {

		Map<String, Object> operationAttributes = executionContext.getOperationAttributes();

		operationAttributes.put(EXECUTIONCONTEXT_KEY_VARIABLE_PREDICATES_PER_OPERATION, variablePredicatesPerOperation);
	}
}
