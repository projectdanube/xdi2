package xdi2.messaging.target.interceptor.impl.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

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
 * This interceptor uses a preconfigured symmetric key to decrypt/encrypt literal data.
 * 
 * @author animesh
 */
public class LiteralEncryptionInterceptor extends AbstractInterceptor implements MessagingTargetInterceptor, TargetInterceptor, ResultInterceptor, Prototype<LiteralEncryptionInterceptor> {

	private String secretKeyString;

	private SecretKey secretKey;

	/*
	 * Prototype
	 */

	@Override
	public LiteralEncryptionInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		LiteralEncryptionInterceptor interceptor = new LiteralEncryptionInterceptor();

		// set the link contracts graph

		interceptor.setSecretKeyString(this.getSecretKeyString());

		// done

		return interceptor;
	}

	/*
	 * MessagingTargetInterceptor
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		if (this.getSecretKeyString() == null) {

			throw new Xdi2MessagingException("No secret key string.", null, null);
		}

		this.secretKey = new SecretKeySpec(Base64.decodeBase64(this.getSecretKeyString()), "AES");
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

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

				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
				byte[] encryptedLiteralDataBytes = cipher.doFinal(literalDataString.getBytes("UTF-8"));
				encryptedLiteralDataString = Base64.encodeBase64String(encryptedLiteralDataBytes);
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

				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
				byte[] literalDataBytes = cipher.doFinal(Base64.decodeBase64(encryptedLiteralDataString));
				literalDataString = new String(literalDataBytes, "UTF-8");
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while decrypting literal string: " + ex.getMessage(), ex, executionContext);
			}

			Object literalData = AbstractLiteral.stringToLiteralData(literalDataString);

			literal.setLiteralData(literalData);
		}
	}

	/*
	 * Getters and setters
	 */

	public String getSecretKeyString() {

		return this.secretKeyString;
	}

	public void setSecretKeyString(String secretKeyString) {

		this.secretKeyString = secretKeyString;
	}
}
