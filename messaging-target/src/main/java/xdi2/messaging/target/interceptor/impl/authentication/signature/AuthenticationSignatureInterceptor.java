package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.signatures.AESSignature;
import xdi2.core.features.signatures.RSASignature;
import xdi2.core.features.signatures.Signature;
import xdi2.core.security.validate.AESSignatureValidator;
import xdi2.core.security.validate.RSASignatureValidator;
import xdi2.core.security.validate.SignatureValidator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2AuthenticationException;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor looks for a signature on an incoming XDI message,
 * and invokes an instance of SignatureValidater to authenticate the message.
 */
public class AuthenticationSignatureInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<AuthenticationSignatureInterceptor> {

	private static Logger log = LoggerFactory.getLogger(AuthenticationSignatureInterceptor.class.getName());

	private SignatureValidator<? extends Signature> signatureValidator;

	/*
	 * Prototype
	 */

	@Override
	public AuthenticationSignatureInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		AuthenticationSignatureInterceptor interceptor = new AuthenticationSignatureInterceptor();

		// set the signature validator

		interceptor.setSignatureValidator(this.getSignatureValidator());

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look for signature on the message

		ReadOnlyIterator<Signature> signatures = message.getSignatures();
		if (! signatures.hasNext()) return InterceptorResult.DEFAULT;

		// validate signatures

		SignatureValidator<? extends Signature> signatureValidator = this.getSignatureValidator();

		if (log.isDebugEnabled()) log.debug("Validating via " + signatureValidator.getClass().getSimpleName());

		boolean validated = true;

		for (Signature signature : signatures) {

			XDIAddress signerXDIAddress = message.getSenderXDIAddress();

			try {

				// TODO: find a way to not have to enumerate this

				if (signatureValidator instanceof RSASignatureValidator && signature instanceof RSASignature) {

					validated &= ((RSASignatureValidator) signatureValidator).validateSignature((RSASignature) signature, signerXDIAddress);
					continue;
				}

				if (signatureValidator instanceof AESSignatureValidator && signature instanceof AESSignature) {

					validated &= ((AESSignatureValidator) signatureValidator).validateSignature((AESSignature) signature, signerXDIAddress);
					continue;
				}
			} catch (GeneralSecurityException ex) {

				throw new Xdi2MessagingException("Unable to validate signature via " + signatureValidator.getClass().getSimpleName() + ": " + ex.getMessage(), ex, executionContext);
			}

			validated = false;
			break;
		}

		// signature is valid?

		XdiAttribute signatureValidXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SIGNATURE_VALID));
		LiteralNode signatureValidLiteral = signatureValidXdiAttribute.setLiteralBoolean(Boolean.valueOf(validated));

		if (log.isDebugEnabled()) log.debug("Valid: " + signatureValidLiteral.getStatement());

		if (! validated) throw new Xdi2AuthenticationException("Invalid signature.", null, executionContext);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public SignatureValidator<? extends Signature> getSignatureValidator() {

		return this.signatureValidator;
	}

	public void setSignatureValidator(SignatureValidator<? extends Signature> signatureValidator) {

		this.signatureValidator = signatureValidator;
	}
}
