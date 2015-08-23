package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphAware;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.Extension;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.StatementHandler;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2PushRequiredException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.messaging.target.interceptor.InterceptorResult;

/**
 * The AbstractMessagingTarget provides the following functionality:
 * - Implementation of execute() with a message envelope (all messages
 *   in the envelope and all operations in the messages will be executed).
 * - Implementation of execute() with a message (all operations
 *   in the messages will be executed).
 * - Support for interceptors and contributors.
 * - Maintaining an "execution context" object where state can be kept between
 *   individual phases.
 * 
 * Subclasses must do the following:
 * - Implement execute() with an operation.
 * 
 * @author markus
 */
public abstract class AbstractMessagingTarget implements MessagingTarget {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingTarget.class);

	private XDIArc ownerPeerRootXDIArc;
	private InterceptorList<MessagingTarget> interceptors;
	private ContributorMap contributors;

	public AbstractMessagingTarget(XDIArc ownerPeerRootXDIArc) {

		this.ownerPeerRootXDIArc = ownerPeerRootXDIArc;
		this.interceptors = new InterceptorList<MessagingTarget> ();
		this.contributors = new ContributorMap();
	}

	public AbstractMessagingTarget() {

		this((XDIArc) null);
	}

	@Override
	public void init() throws Exception {

		if (log.isInfoEnabled()) log.info("Initializing " + this.getClass().getSimpleName() + " [" + this.getInterceptors().size() + " interceptors: " + this.getInterceptors().stringList() + "] [" + this.getContributors().size() + " contributors: " + this.getContributors().stringList() + "].");

		// init interceptors and contributors

		List<Extension<MessagingTarget>> extensions = new ArrayList<Extension<MessagingTarget>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<MessagingTarget>> (this.getInterceptors().iterator()).list());
		extensions.addAll(new IteratorListMaker<Contributor> (this.getContributors().iterator()).list());

		Collections.sort(extensions, new Extension.InitPriorityComparator());

		for (Extension<MessagingTarget> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Initializing extension " + extension.getClass().getSimpleName() + ".");

			extension.init(this);

			if (this instanceof GraphMessagingTarget && extension instanceof GraphAware) {

				((GraphAware) extension).setGraph(((GraphMessagingTarget) this).getGraph());
			}
		}
	}

	@Override
	public void shutdown() throws Exception {

		if (log.isInfoEnabled()) log.info("Shutting down " + this.getClass().getSimpleName() + ".");

		// shutdown interceptors and contributors

		List<Extension<MessagingTarget>> extensions = new ArrayList<Extension<MessagingTarget>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<MessagingTarget>> (this.getInterceptors().iterator()).list());
		extensions.addAll(new IteratorListMaker<Contributor> (this.getContributors().iterator()).list());

		Collections.sort(extensions, new Extension.ShutdownPriorityComparator());

		for (Extension<MessagingTarget> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Shutting down extension " + extension.getClass().getSimpleName() + ".");

			extension.shutdown(this);
		}
	}

	/**
	 * Executes a messaging request against this messaging target.
	 * @param messageEnvelope The XDI message envelope to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	@Override
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (messageEnvelope == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();
		if (executionResult == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope (" + messageEnvelope.getMessageCount() + " messages).");

		try {

			executionContext.pushMessagingTarget(this, null);

			executionContext.pushMessageEnvelope(messageEnvelope, null);

			// reset execution context

			executionContext.resetMessageEnvelopeAttributes();

			// before message envelope

			this.before(messageEnvelope, executionResult, executionContext);

			// execute message envelope interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeMessageEnvelopeInterceptorsBefore(this.getInterceptors(), messageEnvelope, executionResult, executionContext);

			if (interceptorResultBefore.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to message envelope interceptors (before).");
				return;
			}

			// execute the messages in the message envelope

			Iterator<Message> messages = messageEnvelope.getMessages();

			while (messages.hasNext()) {

				Message message = messages.next();

				this.execute(message, executionContext, executionResult);
			}

			// execute message envelope interceptors (after)

			InterceptorResult interceptorResultAfter = InterceptorExecutor.executeMessageEnvelopeInterceptorsAfter(this.getInterceptors(), messageEnvelope, executionResult, executionContext);

			if (interceptorResultAfter.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to message envelope interceptors (after).");
				return;
			}

			// after message envelope

			this.after(messageEnvelope, executionResult, executionContext);

			// execute result interceptors (finish)

			InterceptorExecutor.executeResultGraphInterceptorsFinish(this.getInterceptors(), executionResult, executionContext);
		} catch (Exception ex) {

			// process exception

			ex = executionContext.processException(ex);

			// execute message envelope interceptors (exception)

			try {

				InterceptorExecutor.executeMessageEnvelopeInterceptorsException(this.getInterceptors(), messageEnvelope, executionResult, executionContext, (Xdi2MessagingException) ex);
			} catch (Exception ex2) {

				log.warn("Error while messaging envelope interceptor tried to handle exception: " + ex2.getMessage(), ex2);
			}

			// exception in message envelope

			try {

				this.exception(messageEnvelope, executionResult, executionContext, (Xdi2MessagingException) ex);
			} catch (Exception ex2) {

				log.warn("Error while messaging envelope target tried to handle exception: " + ex2.getMessage(), ex2);
			}

			// re-throw it

			throw (Xdi2MessagingException) ex;
		} finally {

			this.getInterceptors().clearDisabledForMessageEnvelope(messageEnvelope);
			this.getContributors().clearDisabledForMessageEnvelope(messageEnvelope);

			try {

				executionContext.popMessageEnvelope();
			} catch (Exception ex) {

				log.warn("Error while popping message envelope: " + ex.getMessage(), ex);
			}

			try {

				executionContext.popMessagingTarget();
			} catch (Exception ex) {

				log.warn("Error while popping messaging target: " + ex.getMessage(), ex);
			}

			if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + " finished execution. Trace: " + executionContext.getTraceBlock());
		}
	}

	/**
	 * Executes a message by executing all its operations.
	 * @param message The XDI message containing XDI operations to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (message == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();
		if (executionResult == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message (" + message.getContextNode().getXDIAddress().toString() + ") (" + message.getOperationCount() + " operations).");

		try {

			executionContext.pushMessage(message, message.getContextNode().getXDIAddress().toString());

			// reset execution context

			executionContext.resetMessageAttributes();

			// before message

			this.before(message, executionResult, executionContext);

			// execute message interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeMessageInterceptorsBefore(this.getInterceptors(), message, executionResult, executionContext);

			if (interceptorResultBefore.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to message interceptors (before).");
				return;
			}

			// execute the operations in the message

			Iterator<Operation> operations = message.getOperations();

			while (operations.hasNext()) {

				Operation operation = operations.next();

				Graph operationResultGraph = executionResult.createOperationResultGraph(operation);

				this.execute(operation, executionContext, operationResultGraph);
			}

			// execute message interceptors (after)

			InterceptorResult interceptorResultAfter = InterceptorExecutor.executeMessageInterceptorsAfter(this.getInterceptors(), message, executionResult, executionContext);

			if (interceptorResultAfter.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to message interceptors (after).");
				return;
			}

			// after message

			this.after(message, executionResult, executionContext);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			this.getInterceptors().clearDisabledForMessage(message);
			this.getContributors().clearDisabledForMessage(message);

			try {

				executionContext.popMessage();
			} catch (Exception ex) {

				log.warn("Error while popping message: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Executes an operation.
	 * @param operation The XDI operation.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 * @param operationResultGraph The operation's result graph.
	 */
	public void execute(Operation operation, ExecutionContext executionContext, Graph operationResultGraph) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation (" + operation.getOperationXDIAddress() + ").");

		try {

			executionContext.pushOperation(operation, operation.getOperationXDIAddress().toString());

			// reset execution context

			executionContext.resetOperationAttributes();

			// before operation

			this.before(operation, operationResultGraph, executionContext);

			// execute operation interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeOperationInterceptorsBefore(this.getInterceptors(), operation, operationResultGraph, executionContext);

			if (interceptorResultBefore.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to operation interceptors (before).");
				return;
			}

			// execute the address or statements in the operation

			XDIAddress targetAddress = operation.getTargetXDIAddress();
			Iterator<XDIStatement> targetStatementAddresses = operation.getTargetXDIStatements();

			try {

				if (targetAddress != null) {

					this.execute(targetAddress, operation, operationResultGraph, executionContext);
				} else if (targetStatementAddresses != null) {

					while (targetStatementAddresses.hasNext()) {

						XDIStatement targetStatementAddress = targetStatementAddresses.next();

						this.execute(targetStatementAddress, operation, operationResultGraph, executionContext);
					}
				}
			} catch (Xdi2PushRequiredException ex) {

				this.instantiatePushLinkContract(ex, operationResultGraph);
			}

			// execute operation interceptors (after)

			InterceptorResult interceptorResultAfter = InterceptorExecutor.executeOperationInterceptorsAfter(this.getInterceptors(), operation, operationResultGraph, executionContext);

			if (interceptorResultAfter.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to operation interceptors (after).");
				return;
			}

			// after operation

			this.after(operation, operationResultGraph, executionContext);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			this.getInterceptors().clearDisabledForOperation(operation);
			this.getContributors().clearDisabledForOperation(operation);

			try {

				executionContext.popOperation();
			} catch (Exception ex) {

				log.warn("Error while popping operation: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Executes a target address.
	 * @param targetAddress The target address.
	 * @param operation The XDI operation.
	 * @param operationResultGraph The operation's result graph.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	public void execute(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException {

		if (targetAddress == null) throw new NullPointerException();
		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		try {

			executionContext.pushTargetAddress(targetAddress, "" + targetAddress);

			// execute target interceptors (address)

			if ((targetAddress = InterceptorExecutor.executeTargetInterceptorsAddress(this.getInterceptors(), targetAddress, operation, operationResultGraph, executionContext)) == null) {

				return;
			}

			// execute contributors (address)

			ContributorResult contributorResultAddress = ContributorExecutor.executeContributorsAddress(this.getContributors(), new XDIAddress[0], XDIConstants.XDI_ADD_ROOT, targetAddress, operation, operationResultGraph, executionContext);

			if (contributorResultAddress.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to contributors (address).");
				return;
			}

			// get an address handler, and execute on the address

			AddressHandler addressHandler = this.getAddressHandler(targetAddress);

			if (addressHandler == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No address handler for target address " + targetAddress + ".");
				return;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXDIAddress() + " on target address " + targetAddress + " (" + addressHandler.getClass().getName() + ").");

			addressHandler.executeOnAddress(targetAddress, operation, operationResultGraph, executionContext);
		} catch (Xdi2PushRequiredException ex) {

			throw ex;
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			try {

				executionContext.popTargetAddress();
			} catch (Exception ex) {

				log.warn("Error while popping target address: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Executes a target statement.
	 * @param targetStatement The target statement.
	 * @param operation The XDI operation.
	 * @param operationResultGraph The operation's result graph.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	public void execute(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException {

		if (targetStatement == null) throw new NullPointerException();
		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		try {

			executionContext.pushTargetStatement(targetStatement, "" + targetStatement);

			// execute target interceptors (statement)

			if ((targetStatement = InterceptorExecutor.executeTargetInterceptorsStatement(this.getInterceptors(), targetStatement, operation, operationResultGraph, executionContext)) == null) {

				return;
			}

			// execute contributors (statement)

			ContributorResult contributorResultAddress = ContributorExecutor.executeContributorsStatement(this.getContributors(), new XDIAddress[0], XDIConstants.XDI_ADD_ROOT, targetStatement, operation, operationResultGraph, executionContext);

			if (contributorResultAddress.isSkipMessagingTarget()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging target according to contributors (statement).");
				return;
			}

			// get a statement handler, and execute on the statement

			StatementHandler statementHandler = this.getStatementHandler(targetStatement);

			if (statementHandler == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No statement handler for target statement " + targetStatement + ".");
				return;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXDIAddress() + " on target statement " + targetStatement + " (" + statementHandler.getClass().getName() + ").");

			statementHandler.executeOnStatement(targetStatement, operation, operationResultGraph, executionContext);
		} catch (Xdi2PushRequiredException ex) {

			throw ex;
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			try {

				executionContext.popTargetStatement();
			} catch (Exception ex) {

				log.warn("Error while popping target statement: " + ex.getMessage(), ex);
			}
		}
	}

	/*
	 * Push contract required, if an operation result is not available
	 */

	private void instantiatePushLinkContract(Xdi2PushRequiredException ex, Graph operationResultGraph) {

		XDIAddress authorizingAuthority = this.getOwnerXDIAddress();
		XDIAddress requestingAuthority = ex.getOperation().getMessage().getSenderXDIAddress();

		XDIAddress target = null;
		if (target == null && ex.getXDIaddress() != null) target = ex.getXDIaddress();
		if (target == null && ex.getXDIstatement() != null) target = ex.getXDIstatement().getContextNodeXDIAddress();

		if (target == null) throw new NullPointerException();

		Map<XDIArc, XDIAddress> variableValues = new HashMap<XDIArc, XDIAddress> ();
		variableValues.put(XDIArc.create("{$target}"), target);

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(XDIBootstrap.PUSH_LINK_CONTRACT_TEMPLATE);
		linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
		linkContractInstantiation.setRequestingAuthority(requestingAuthority);
		linkContractInstantiation.setVariableValues(variableValues);

		linkContractInstantiation.execute(operationResultGraph, true, true);
	}

	/*
	 * These are for being overridden by subclasses
	 */

	protected void before(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void before(Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void after(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void after(Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void exception(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Xdi2MessagingException ex) throws Xdi2MessagingException {

	}

	protected AddressHandler getAddressHandler(XDIAddress targetAddress) throws Xdi2MessagingException {

		return null;
	}

	protected StatementHandler getStatementHandler(XDIStatement targetStatement) throws Xdi2MessagingException {

		return null;
	}

	/*
	 * Getters and setters
	 */

	@Override
	public XDIArc getOwnerPeerRootXDIArc() {

		return this.ownerPeerRootXDIArc;
	}

	public void setOwnerPeerRootXDIArc(XDIArc ownerPeerRootXDIArc) {

		this.ownerPeerRootXDIArc = ownerPeerRootXDIArc;
	}

	@Override
	public XDIAddress getOwnerXDIAddress() {

		return XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(this.getOwnerPeerRootXDIArc());
	}

	public void setOwnerXDIAddress(XDIAddress ownerXDIAddress) {

		this.setOwnerPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(ownerXDIAddress));
	}

	public InterceptorList<MessagingTarget> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList<MessagingTarget> interceptors) {

		this.interceptors = interceptors;
	}

	public ContributorMap getContributors() {

		return this.contributors;
	}

	public void setContributors(ContributorMap contributors) {

		this.contributors = contributors;
	}
}
