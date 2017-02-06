package xdi2.messaging.container.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.AddressHandler;
import xdi2.messaging.container.Extension;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.StatementHandler;
import xdi2.messaging.container.contributor.Contributor;
import xdi2.messaging.container.contributor.ContributorMap;
import xdi2.messaging.container.contributor.ContributorResult;
import xdi2.messaging.container.contributor.impl.digest.GenerateDigestSecretTokenContributor;
import xdi2.messaging.container.contributor.impl.keygen.GenerateKeyContributor;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.messaging.container.interceptor.InterceptorList;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.impl.HasInterceptor;
import xdi2.messaging.container.interceptor.impl.RefInterceptor;
import xdi2.messaging.container.interceptor.impl.ToInterceptor;
import xdi2.messaging.container.interceptor.impl.connect.ConnectInterceptor;
import xdi2.messaging.container.interceptor.impl.defer.DeferResultInterceptor;
import xdi2.messaging.container.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.container.interceptor.impl.push.PushInInterceptor;
import xdi2.messaging.container.interceptor.impl.push.PushOutInterceptor;
import xdi2.messaging.container.interceptor.impl.security.digest.DigestInterceptor;
import xdi2.messaging.container.interceptor.impl.security.secrettoken.SecretTokenInterceptor;
import xdi2.messaging.container.interceptor.impl.security.signature.SignatureInterceptor;
import xdi2.messaging.container.interceptor.impl.send.SendInterceptor;
import xdi2.messaging.container.interceptor.impl.signing.SigningInterceptor;
import xdi2.messaging.operations.Operation;

/**
 * The AbstractMessagingContainer provides the following functionality:
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
public abstract class AbstractMessagingContainer implements MessagingContainer {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingContainer.class);

	private XDIArc ownerPeerRootXDIArc;
	private InterceptorList<MessagingContainer> interceptors;
	private ContributorMap contributors;

	public AbstractMessagingContainer(XDIArc ownerPeerRootXDIArc) {

		this.ownerPeerRootXDIArc = ownerPeerRootXDIArc;
		this.interceptors = new InterceptorList<MessagingContainer> ();
		this.contributors = new ContributorMap();
	}

	public AbstractMessagingContainer() {

		this((XDIArc) null);
	}

	@Override
	public void init() throws Exception {

		if (log.isInfoEnabled()) log.info("Initializing " + this.getClass().getSimpleName() + " [" + this.getInterceptors().size() + " interceptors: " + this.getInterceptors().stringList() + "] [" + this.getContributors().size() + " contributors: " + this.getContributors().stringList() + "].");

		// init interceptors and contributors

		List<Extension<MessagingContainer>> extensions = new ArrayList<Extension<MessagingContainer>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<MessagingContainer>> (this.getInterceptors().iterator()).list());
		extensions.addAll(new IteratorListMaker<Contributor> (this.getContributors().iterator()).list());

		Collections.sort(extensions, new Extension.InitPriorityComparator());

		for (Extension<MessagingContainer> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Initializing extension " + extension.getClass().getSimpleName() + ".");

			extension.init(this);
		}
	}

	@Override
	public void shutdown() throws Exception {

		if (log.isInfoEnabled()) log.info("Shutting down " + this.getClass().getSimpleName() + ".");

		// shutdown interceptors and contributors

		List<Extension<MessagingContainer>> extensions = new ArrayList<Extension<MessagingContainer>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<MessagingContainer>> (this.getInterceptors().iterator()).list());
		extensions.addAll(new IteratorListMaker<Contributor> (this.getContributors().iterator()).list());

		Collections.sort(extensions, new Extension.ShutdownPriorityComparator());

		for (Extension<MessagingContainer> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Shutting down extension " + extension.getClass().getSimpleName() + ".");

			extension.shutdown(this);
		}
	}

	/**
	 * Executes a messaging request against this messaging container.
	 * @param messageEnvelope The XDI message envelope to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging containers, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	@Override
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (messageEnvelope == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();
		if (executionResult == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope (" + messageEnvelope.getMessageCount() + " messages).");

		boolean skipMessagingContainer = false;

		try {

			// push

			executionContext.pushMessagingContainer(this, null);

			// push

			executionContext.pushMessageEnvelope(messageEnvelope, null);

			// reset execution context

			executionContext.resetMessageEnvelopeAttributes();

			// before message envelope

			skipMessagingContainer |= this.before(messageEnvelope, executionContext, executionResult);

			// execute message envelope interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeMessageEnvelopeInterceptorsBefore(this.getInterceptors(), messageEnvelope, executionContext, executionResult);

			if (interceptorResultBefore.isSkipMessagingContainer()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to message envelope interceptors (before).");
				skipMessagingContainer |= true;
			}

			// execute the messages in the message envelope

			if (! skipMessagingContainer) {

				Iterator<Message> messages = messageEnvelope.getMessages();

				while (messages.hasNext()) {

					Message message = messages.next();

					this.execute(message, executionContext, executionResult);
				}
			}

			// execute message envelope interceptors (after)

			InterceptorExecutor.executeMessageEnvelopeInterceptorsAfter(this.getInterceptors(), messageEnvelope, executionContext, executionResult);

			// after message envelope

			this.after(messageEnvelope, executionContext, executionResult);
		} catch (Throwable ex) {

			// process exception

			ex = executionContext.processException(ex);

			// execute message envelope interceptors (exception)

			try {

				InterceptorExecutor.executeMessageEnvelopeInterceptorsException(this.getInterceptors(), messageEnvelope, executionContext, executionResult, (Xdi2MessagingException) ex);
			} catch (Exception ex2) {

				log.warn("Error while messaging envelope interceptor tried to handle exception: " + ex2.getMessage(), ex2);
			}

			// exception in message envelope

			try {

				this.exception(messageEnvelope, executionContext, executionResult, (Xdi2MessagingException) ex);
			} catch (Exception ex2) {

				log.warn("Error while messaging envelope target tried to handle exception: " + ex2.getMessage(), ex2);
			}

			// add exception to execution result

			executionResult.addException(ex);

			// re-throw it

			throw (Xdi2MessagingException) ex;
		} finally {

			this.getInterceptors().clearDisabledForMessageEnvelope(messageEnvelope);
			this.getContributors().clearDisabledForMessageEnvelope(messageEnvelope);

			// pop

			try {

				executionContext.popMessageEnvelope();
			} catch (Exception ex) {

				log.warn("Error while popping message envelope: " + ex.getMessage(), ex);
			}

			// finish execution result

			try {

				executionResult.finish();
			} catch (Exception ex) {

				log.warn("Error while finishing execution context: " + ex.getMessage(), ex);
			}

			// execute result interceptors (finish)

			try {

				InterceptorExecutor.executeResultInterceptorsFinish(this.getInterceptors(), this, executionContext, executionResult);
			} catch (Exception ex) {

				log.warn("Error while execution result interceptors: " + ex.getMessage(), ex);
			}

			// pop

			try {

				executionContext.popMessagingContainer();
			} catch (Exception ex) {

				log.warn("Error while popping messaging container: " + ex.getMessage(), ex);
			}

			// done

			if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + " finished execution. Trace: " + executionContext.getTraceBlock());
		}
	}

	/**
	 * Executes a message by executing all its operations.
	 * @param message The XDI message containing XDI operations to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging containers, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		if (message == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();
		if (executionResult == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message (" + message.getContextNode().getXDIAddress().toString() + ") (" + message.getOperationCount() + " operations).");

		boolean skipMessagingContainer = false;

		try {

			// push

			executionContext.pushMessage(message, message.getContextNode().getXDIAddress().toString());

			// reset execution context

			executionContext.resetMessageAttributes();

			// before message

			skipMessagingContainer |= this.before(message, executionContext, executionResult);

			// execute message interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeMessageInterceptorsBefore(this.getInterceptors(), message, executionContext, executionResult);

			if (interceptorResultBefore.isSkipMessagingContainer()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to message interceptors (before).");
				skipMessagingContainer |= true;
			}

			// execute the operations in the message

			if (! skipMessagingContainer) {

				Iterator<Operation> operations = message.getOperations();

				while (operations.hasNext()) {

					Operation operation = operations.next();

					this.execute(operation, executionContext, executionResult);
				}
			}

			// execute message interceptors (after)

			InterceptorExecutor.executeMessageInterceptorsAfter(this.getInterceptors(), message, executionContext, executionResult);

			// after message

			this.after(message, executionContext, executionResult);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			this.getInterceptors().clearDisabledForMessage(message);
			this.getContributors().clearDisabledForMessage(message);

			// pop

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
	 * messaging containers, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(Operation operation, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		Graph operationResultGraph = executionResult.createOperationResultGraph(operation);

		this.execute(operation, operationResultGraph, executionContext);
	}

	/**
	 * Executes an operation.
	 * @param operation The XDI operation.
	 * @param operationResultGraph The graph for result statements from this operation.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation (" + operation.getOperationXDIAddress() + ").");

		boolean skipMessagingContainer = false;

		try {

			// push

			executionContext.pushOperation(operation, operation.getOperationXDIAddress().toString());

			// reset execution context

			executionContext.resetOperationAttributes();

			// before operation

			skipMessagingContainer |= this.before(operation, operationResultGraph, executionContext);

			// execute operation interceptors (before)

			InterceptorResult interceptorResultBefore = InterceptorExecutor.executeOperationInterceptorsBefore(this.getInterceptors(), operation, operationResultGraph, executionContext);

			if (interceptorResultBefore.isSkipMessagingContainer()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to operation interceptors (before).");
				skipMessagingContainer |= true;
			}

			// execute the target address or statements in the operation

			if (! skipMessagingContainer) {

				XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
				IterableIterator<XDIStatement> targetXDIStatements = operation.getTargetXDIStatements();

				if (targetXDIAddress != null) {

					this.execute(targetXDIAddress, operation, operationResultGraph, executionContext);
				}

				if (targetXDIStatements != null) {

					for (XDIStatement targetXDIStatement : targetXDIStatements) {

						this.execute(targetXDIStatement, operation, operationResultGraph, executionContext);
					}
				}
			}

			// execute operation interceptors (after)

			InterceptorExecutor.executeOperationInterceptorsAfter(this.getInterceptors(), operation, operationResultGraph, executionContext);

			// after operation

			this.after(operation, operationResultGraph, executionContext);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			this.getInterceptors().clearDisabledForOperation(operation);
			this.getContributors().clearDisabledForOperation(operation);

			// pop

			try {

				executionContext.popOperation();
			} catch (Exception ex) {

				log.warn("Error while popping operation: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Executes a target address.
	 * @param targetXDIAddress The target address.
	 * @param operation The XDI operation.
	 * @param operationResultGraph The operation's result graph.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging containers, interceptors and contributors.
	 */
	public void execute(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetXDIAddress == null) throw new NullPointerException();
		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		try {

			// push

			executionContext.pushTargetAddress(targetXDIAddress, "" + targetXDIAddress);

			// execute target interceptors (address)

			if ((targetXDIAddress = InterceptorExecutor.executeTargetInterceptorsAddress(this.getInterceptors(), targetXDIAddress, operation, operationResultGraph, executionContext)) == null) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to target interceptors (address).");
				return;
			}

			// execute contributors (address)

			ContributorResult contributorResultAddress = ContributorExecutor.executeContributorsAddress(this.getContributors(), new XDIAddress[0], XDIConstants.XDI_ADD_ROOT, targetXDIAddress, operation, operationResultGraph, executionContext);

			if (contributorResultAddress.isSkipMessagingContainer()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to contributors (address).");
				return;
			}

			// get an address handler, and execute on the address

			AddressHandler addressHandler = this.getAddressHandler(targetXDIAddress);

			if (addressHandler == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No address handler for target address " + targetXDIAddress + ".");
				return;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXDIAddress() + " on target address " + targetXDIAddress + " (" + addressHandler.getClass().getName() + ").");

			addressHandler.executeOnAddress(targetXDIAddress, operation, operationResultGraph, executionContext);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			// pop

			try {

				executionContext.popTargetAddress();
			} catch (Exception ex) {

				log.warn("Error while popping target address: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Executes a target statement.
	 * @param targetXDIStatement The target statement.
	 * @param operation The XDI operation.
	 * @param operationResultGraph The operation's result graph.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging containers, interceptors and contributors.
	 */
	public void execute(XDIStatement targetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetXDIStatement == null) throw new NullPointerException();
		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		try {

			// push

			executionContext.pushTargetStatement(targetXDIStatement, "" + targetXDIStatement);

			// execute target interceptors (statement)

			if ((targetXDIStatement = InterceptorExecutor.executeTargetInterceptorsStatement(this.getInterceptors(), targetXDIStatement, operation, operationResultGraph, executionContext)) == null) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to target interceptors (statement).");
				return;
			}

			// execute contributors (statement)

			ContributorResult contributorResultAddress = ContributorExecutor.executeContributorsStatement(this.getContributors(), new XDIAddress[0], XDIConstants.XDI_ADD_ROOT, targetXDIStatement, operation, operationResultGraph, executionContext);

			if (contributorResultAddress.isSkipMessagingContainer()) {

				if (log.isDebugEnabled()) log.debug("Skipping messaging container according to contributors (statement).");
				return;
			}

			// get a statement handler, and execute on the statement

			StatementHandler statementHandler = this.getStatementHandler(targetXDIStatement);

			if (statementHandler == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No statement handler for target statement " + targetXDIStatement + ".");
				return;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXDIAddress() + " on target statement " + targetXDIStatement + " (" + statementHandler.getClass().getName() + ").");

			statementHandler.executeOnStatement(targetXDIStatement, operation, operationResultGraph, executionContext);
		} catch (Exception ex) {

			// process exception and re-throw it

			throw executionContext.processException(ex);
		} finally {

			// pop

			try {

				executionContext.popTargetStatement();
			} catch (Exception ex) {

				log.warn("Error while popping target statement: " + ex.getMessage(), ex);
			}
		}
	}

	/*
	 * We can provide a container with a standard set of interceptors and contributors.
	 */

	public void addStandardExtensions() {

		this.interceptors.addInterceptor(new ToInterceptor());
		this.interceptors.addInterceptor(new RefInterceptor());
		this.interceptors.addInterceptor(new HasInterceptor());
		this.interceptors.addInterceptor(new SecretTokenInterceptor());
		this.interceptors.addInterceptor(new SignatureInterceptor());
		this.interceptors.addInterceptor(new DigestInterceptor());
		this.interceptors.addInterceptor(new LinkContractInterceptor());
		this.interceptors.addInterceptor(new SigningInterceptor());
		this.interceptors.addInterceptor(new ConnectInterceptor());
		this.interceptors.addInterceptor(new SendInterceptor());
		this.interceptors.addInterceptor(new PushInInterceptor());
		this.interceptors.addInterceptor(new PushOutInterceptor());
		this.interceptors.addInterceptor(new DeferResultInterceptor());
		this.contributors.addContributor(new GenerateDigestSecretTokenContributor());
		this.contributors.addContributor(new GenerateKeyContributor());
	}

	/*
	 * These are for being overridden by subclasses
	 */

	protected boolean before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return false;
	}

	protected boolean before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return false;
	}

	protected boolean before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	protected void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

	}

	protected void after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

	}

	protected void after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	protected void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Xdi2MessagingException ex) throws Xdi2MessagingException {

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
	public boolean ownsPeerRootXDIArc(XDIArc peerRootXDIArc) {

		return peerRootXDIArc.equals(this.getOwnerPeerRootXDIArc());
	}

	public InterceptorList<MessagingContainer> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList<MessagingContainer> interceptors) {

		this.interceptors = interceptors;
	}

	public ContributorMap getContributors() {

		return this.contributors;
	}

	public void setContributors(ContributorMap contributors) {

		this.contributors = contributors;
	}
}
