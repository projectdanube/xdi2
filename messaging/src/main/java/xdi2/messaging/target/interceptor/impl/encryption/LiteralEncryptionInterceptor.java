package xdi2.messaging.target.interceptor.impl.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor encrypts literals in an incoming XDI message, and decrypts literals
 * in the XDI message result. It invokes an instance of LiteralCryptoService to
 * perform encryption and decryption. 
 */
public class LiteralEncryptionInterceptor extends AbstractInterceptor<MessagingTarget> implements TargetInterceptor, MessageResultInterceptor, Prototype<LiteralEncryptionInterceptor> {

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
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		this.getLiteralCryptoService().init();
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);

		this.getLiteralCryptoService().shutdown();
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof DoOperation) return targetStatement;

		// encrypt literals

		if (targetStatement.isLiteralStatement()) {

			XDIAddress contextNodeAddress = targetStatement.getContextNodeAddress();
			Object literalData = targetStatement.getLiteralData();

			String literalDataString = AbstractLiteral.literalDataToString(literalData);

			String encryptedLiteralDataString;

			try {

				encryptedLiteralDataString = this.getLiteralCryptoService().encryptLiteralDataString(literalDataString);
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while encrypting literal string: " + ex.getMessage(), ex, executionContext);
			}

			return XDIStatement.fromLiteralComponents(contextNodeAddress, encryptedLiteralDataString);
		}

		// done

		return targetStatement;
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof DoOperation) return targetAddress;

		// done

		return targetAddress;
	}

	/*
	 * MessageResultInterceptor
	 */

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Literal literal : messageResult.getGraph().getRootContextNode(true).getAllLiterals()) {

			String encryptedLiteralDataString = literal.getLiteralDataString();
			if (encryptedLiteralDataString == null) continue;

			String literalDataString;

			try {

				literalDataString = this.getLiteralCryptoService().decryptLiteralDataString(encryptedLiteralDataString);
			} catch (Exception ex) {

				if (log.isDebugEnabled()) log.debug("Problem while decrypting literal string: " + ex.getMessage(), ex);

				continue;
			}

			Object literalData = AbstractLiteral.stringToLiteralData(literalDataString);

			literal.setLiteralData(literalData);
		}
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
