package xdi2.messaging.container.interceptor.impl.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.OperationInterceptor;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.container.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.Operation;

/**
 * This interceptor encrypts literals in an incoming XDI message, and decrypts literals
 * in the XDI message result. It invokes an instance of LiteralCryptoService to
 * perform encryption and decryption. 
 */
public class LiteralEncryptionInterceptor extends AbstractInterceptor<MessagingContainer> implements OperationInterceptor, TargetInterceptor, Prototype<LiteralEncryptionInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(LiteralEncryptionInterceptor.class);

	private LiteralCryptoService literalCryptoService;

	/*
	 * Prototype
	 */

	@Override
	public LiteralEncryptionInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		LiteralEncryptionInterceptor interceptor = new LiteralEncryptionInterceptor();

		// set the crypto service

		interceptor.setLiteralCryptoService(this.getLiteralCryptoService());

		// done

		return interceptor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingContainer messagingContainer) throws Exception {

		super.init(messagingContainer);

		this.getLiteralCryptoService().init();
	}

	@Override
	public void shutdown(MessagingContainer messagingContainer) throws Exception {

		super.shutdown(messagingContainer);

		this.getLiteralCryptoService().shutdown();
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (LiteralNode literal : operationResultGraph.getRootContextNode(true).getAllLiteralNodes()) {

			String encryptedLiteralDataString = literal.getLiteralDataString();
			if (encryptedLiteralDataString == null) continue;

			String literalDataString;

			try {

				literalDataString = this.getLiteralCryptoService().decryptLiteralDataString(encryptedLiteralDataString);
			} catch (Exception ex) {

				if (log.isDebugEnabled()) log.debug("Problem while decrypting literal string: " + ex.getMessage(), ex);

				continue;
			}

			Object literalData = AbstractLiteralNode.stringToLiteralData(literalDataString);

			literal.setLiteralData(literalData);
		}

		// done
		
		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof DoOperation) return targetStatement;

		// encrypt literals

		if (targetStatement.isLiteralStatement()) {

			XDIAddress contextNodeXDIAddress = targetStatement.getContextNodeXDIAddress();
			Object literalData = targetStatement.getLiteralData();

			String literalDataString = AbstractLiteralNode.literalDataToString(literalData);

			String encryptedLiteralDataString;

			try {

				encryptedLiteralDataString = this.getLiteralCryptoService().encryptLiteralDataString(literalDataString);
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while encrypting literal string: " + ex.getMessage(), ex, executionContext);
			}

			return XDIStatement.fromLiteralComponents(contextNodeXDIAddress, encryptedLiteralDataString);
		}

		// done

		return targetStatement;
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof DoOperation) return targetAddress;

		// done

		return targetAddress;
	}

	/*
	 * Getters and setters
	 */

	public LiteralCryptoService getLiteralCryptoService() {

		return this.literalCryptoService;
	}

	public void setLiteralCryptoService(LiteralCryptoService literalCryptoService) {

		this.literalCryptoService = literalCryptoService;
	}
}
