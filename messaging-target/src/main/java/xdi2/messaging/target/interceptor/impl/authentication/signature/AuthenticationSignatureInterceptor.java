package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.signatures.Signature;
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

	private List<SignatureValidator<Signature>> signatureValidators;

	/*
	 * Prototype
	 */

	@Override
	public AuthenticationSignatureInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		AuthenticationSignatureInterceptor interceptor = new AuthenticationSignatureInterceptor();

		// set the signature validator

		interceptor.setSignatureValidators(this.getSignatureValidators());

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// look for signature on the message

		ReadOnlyIterator<Signature> signatures = message.getSignatures();
		if (! signatures.hasNext()) return InterceptorResult.DEFAULT;

		// validate signatures

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();

		boolean validated = false;

		for (Signature signature : signatures) {

			boolean validatedSignature = false;

			for (SignatureValidator<Signature> signatureValidator : this.getSignatureValidators()) {

				if (log.isDebugEnabled()) log.debug("Validating " + signature.getClass().getSimpleName() + " for " + senderXDIAddress + " via " + signatureValidator.getClass().getSimpleName());

				try {

					boolean canValidate = signatureValidator.canValidate(signature.getClass());
					if (log.isDebugEnabled()) log.debug("Signature validator " + signatureValidator.getClass().getSimpleName() + " can validate signature " + signature.getClass().getSimpleName() + "? " + canValidate);
					if (! canValidate) continue;

					validatedSignature |= signatureValidator.validateSignature(signature, senderXDIAddress);
					if (log.isDebugEnabled()) log.debug("Validated " + signature.getClass().getSimpleName() + " for " + senderXDIAddress + " via " + signatureValidator.getClass().getSimpleName() + ": " + validatedSignature);
					if (validatedSignature) break;
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Unable to validate signature for " + senderXDIAddress + " via " + signatureValidator.getClass().getSimpleName() + ": " + ex.getMessage(), ex, executionContext);
				}
			}

			validated = validatedSignature;
			if (! validated) break;
		}

		// signature is valid?

		XdiAttribute signatureValidXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SIGNATURE_VALID));
		LiteralNode signatureValidLiteral = signatureValidXdiAttribute.setLiteralBoolean(Boolean.valueOf(validated));

		if (log.isDebugEnabled()) log.debug("Valid for " + senderXDIAddress + ": " + signatureValidLiteral.getStatement());

		if (! validated) throw new Xdi2AuthenticationException("Invalid signature.", null, executionContext);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public List<SignatureValidator<Signature>> getSignatureValidators() {

		return this.signatureValidators;
	}

	public void setSignatureValidators(List<SignatureValidator<Signature>> signatureValidators) {

		this.signatureValidators = signatureValidators;
	}
}
