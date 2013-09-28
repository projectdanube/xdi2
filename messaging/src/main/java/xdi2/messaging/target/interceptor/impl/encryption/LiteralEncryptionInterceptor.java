package xdi2.messaging.target.interceptor.impl.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor encrypts literals in an incoming XDI message, and decrypts literals
 * in the XDI message result. It invokes an instance of LiteralCryptoService to
 * perform encryption and decryption. 
 */
public class LiteralEncryptionInterceptor extends AbstractInterceptor implements MessagingTargetInterceptor, TargetInterceptor, ResultInterceptor, Prototype<LiteralEncryptionInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(LiteralEncryptionInterceptor.class);

	private LiteralCryptoService literalCryptoService;

	/*
	 * Prototype
	 */

	@Override
	public LiteralEncryptionInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		LiteralEncryptionInterceptor interceptor = new LiteralEncryptionInterceptor();

		// set the link contracts graph

		interceptor.setLiteralCryptoService(this.getLiteralCryptoService());

		// done

		return interceptor;
	}

	/*
	 * MessagingTargetInterceptor
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		this.getLiteralCryptoService().init();
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		this.getLiteralCryptoService().shutdown();
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// encrypt literals

		if (targetStatement.isLiteralStatement()) {

			XDI3Segment contextNodeXri = targetStatement.getContextNodeXri();
			Object literalData = targetStatement.getLiteralData();

			String literalDataString = AbstractLiteral.literalDataToString(literalData);

			String encryptedLiteralDataString;

			try {

				encryptedLiteralDataString = this.getLiteralCryptoService().encryptLiteralDataString(literalDataString);
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while encrypting literal string: " + ex.getMessage(), ex, executionContext);
			}

			return XDI3Statement.fromLiteralComponents(contextNodeXri, encryptedLiteralDataString);
		}

		// done

		return targetStatement;
	}

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return targetAddress;
	}

	/*
	 * ResultInterceptor
	 */

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Literal literal : messageResult.getGraph().getRootContextNode().getAllLiterals()) {

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
