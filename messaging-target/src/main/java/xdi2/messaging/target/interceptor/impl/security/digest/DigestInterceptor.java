package xdi2.messaging.target.interceptor.impl.security.digest;

import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.digests.Digest;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.security.digest.validate.DigestValidator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2SecurityException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor looks for a digest on an incoming XDI message,
 * and invokes an instance of DigestValidator to validate it.
 */
public class DigestInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<DigestInterceptor> {

	private static Logger log = LoggerFactory.getLogger(DigestInterceptor.class.getName());

	private List<DigestValidator<Digest>> digestValidators;

	/*
	 * Prototype
	 */

	@Override
	public DigestInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		DigestInterceptor interceptor = new DigestInterceptor();

		// set the digest validators

		interceptor.setDigestValidators(this.getDigestValidators());

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// look for digest on the message

		ReadOnlyIterator<Digest> digests = message.getDigests();
		if (! digests.hasNext()) return InterceptorResult.DEFAULT;

		// validate digests

		boolean validated = false;

		for (Digest digest : digests) {

			boolean validatedDigest = false;

			for (DigestValidator<Digest> digestValidator : this.getDigestValidators()) {

				if (log.isDebugEnabled()) log.debug("Validating " + digest.getClass().getSimpleName() + " via " + digestValidator.getClass().getSimpleName());

				try {

					boolean canValidate = digestValidator.canValidate(digest.getClass());
					if (log.isDebugEnabled()) log.debug("Digest validator " + digestValidator.getClass().getSimpleName() + " can validate digest " + digest.getClass().getSimpleName() + "? " + canValidate);
					if (! canValidate) continue;

					validatedDigest |= digestValidator.validateDigest(digest);
					if (log.isDebugEnabled()) log.debug("Validated " + digest.getClass().getSimpleName() + " via " + digestValidator.getClass().getSimpleName() + ": " + validatedDigest);
					if (validatedDigest) break;
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Unable to validate digest via " + digestValidator.getClass().getSimpleName() + ": " + ex.getMessage(), ex, executionContext);
				}
			}

			validated = validatedDigest;
			if (! validated) break;
		}

		// digest is valid?

		XdiAttribute digestValidXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDISecurityConstants.XDI_ADD_DIGEST_VALID));
		LiteralNode digestValidLiteral = digestValidXdiAttribute.setLiteralBoolean(Boolean.valueOf(validated));

		if (log.isDebugEnabled()) log.debug("Valid: " + digestValidLiteral.getStatement());

		if (! validated) throw new Xdi2SecurityException("Invalid digest.", null, executionContext);

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

	public List<DigestValidator<Digest>> getDigestValidators() {

		return this.digestValidators;
	}

	public void setDigestValidators(List<DigestValidator<Digest>> digestValidators) {

		this.digestValidators = digestValidators;
	}
}
