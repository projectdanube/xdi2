package xdi2.server.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xdi2.Relation;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.exceptions.Xdi2RuntimeException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.server.EndpointRegistry;
import xdi2.server.ExecutionContext;

/**
 * The OperationMessagingTarget allows subclasses to register completely specified
 * types of XDI operations that are understood by the endpoint. 
 * 
 * The OperationMessagingTarget executes an XDI operation by executing a matching
 * OperationHandler in a single step.
 * 
 * Subclasses must do the following:
 * - Register Operation classes and OperationHandlers that can handle them.
 * 
 * @author markus
 */
public class OperationMessagingTarget extends AbstractMessagingTarget {

	private static final Log log = LogFactory.getLog(OperationMessagingTarget.class);

	private Map<Class<? extends Operation>, OperationHandler> operationHandlers;
	private Map<Class<? extends Operation>, Method> isValidMethods;
	private Map<Class<? extends Operation>, Method> fromRelationMethods;

	public OperationMessagingTarget() {

		super();
		
		this.operationHandlers = new HashMap<Class<? extends Operation>, OperationHandler> ();
	}

	@Override
	public void init(EndpointRegistry endpointRegistry) throws Exception {

		super.init(endpointRegistry);
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void registerOperationHandler(Class<? extends Operation> operationClass, OperationHandler operationHandler) {

		// find the message class' isValid() and fromRelation() methods

		Method isValidMethod;
		Method fromRelationMethod;

		try {

			isValidMethod = operationClass.getMethod("isValid", Relation.class);
			if (isValidMethod == null) throw new NullPointerException();

			fromRelationMethod = operationClass.getMethod("fromRelation", Relation.class);
			if (fromRelationMethod == null) throw new NullPointerException();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Message class without correct isValid() or fromRelation() method.", ex);
		}

		// register the operation class

		this.operationHandlers.put(operationClass, operationHandler);
		this.isValidMethods.put(operationClass, isValidMethod);
		this.fromRelationMethods.put(operationClass, fromRelationMethod);
	}

	@Override
	public final boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		boolean handled = false;

		// find out which operation handler can handle this operation

		for (Map.Entry<Class<? extends Operation>, OperationHandler> entry : this.operationHandlers.entrySet()) {

			OperationHandler operationHandler = entry.getValue();
			Method isValidMethod = this.isValidMethods.get(entry.getKey());
			Method fromRelationMethod = this.fromRelationMethods.get(entry.getKey());

			// check if the operation is a valid instance

			Boolean valid;

			try {

				valid = (Boolean) isValidMethod.invoke(null, operation.getRelation());
				if (valid == null) throw new NullPointerException();
			} catch (Exception ex) {

				OperationMessagingTarget.log.warn("Warning: Cannot call isValid() method.", ex);
				continue;
			}

			if (! valid.equals(Boolean.TRUE)) continue;

			// create an instance of that particular operation class

			Operation newOperation;

			try {

				newOperation = (Operation) fromRelationMethod.invoke(null, operation.getRelation());
				if (newOperation == null) throw new NullPointerException();
			} catch (Exception ex) {

				OperationMessagingTarget.log.warn("Warning: Cannot call fromPredicate() method.", ex);
				continue;
			}

			// invoke the operation handler

			if (operationHandler.execute(newOperation, messageResult, executionContext)) handled = true;
			break;
		}

		return handled;
	}
}
