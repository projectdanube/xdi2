package xdi2.messaging.target.interceptor.impl.signing;

import java.security.GeneralSecurityException;

import xdi2.core.Graph;
import xdi2.core.features.signatures.Signature;
import xdi2.core.security.sign.SignatureCreator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractOperationInterceptor;

/**
 * This interceptor can sign an inner graph. 
 * Warning: This is experimental, do not use for serious applications.
 */
public class SigningInterceptor extends AbstractOperationInterceptor implements OperationInterceptor, Prototype<SigningInterceptor> {

	public static final XDIAddress XDI_ADD_DO_SIG = XDIAddress.create("$do$sig");

	private SignatureCreator<? extends Signature> signatureCreator;

	/*
	 * Prototype
	 */

	@Override
	public SigningInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		SigningInterceptor interceptor = new SigningInterceptor();

		// set the signature creator

		interceptor.setSignatureCreator(this.getSignatureCreator());

		// done

		return interceptor;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check parameters

		if (! XDI_ADD_DO_SIG.equals(operation.getOperationXDIAddress())) return InterceptorResult.DEFAULT;
		if (operation.getTargetXdiInnerRoot() == null) return InterceptorResult.DEFAULT;

		// get the inner graph

		Graph innerGraph = operation.getTargetXdiInnerRoot().getInnerGraph();

		// sign inner graph

		Signature signature;

		try {

			signature = this.getSignatureCreator().createSignature(innerGraph.getRootContextNode());
		} catch (GeneralSecurityException ex) {

			throw new Xdi2MessagingException("Could not create signature for operation " + operation + " via " + this.getSignatureCreator().getClass().getSimpleName() + ": " + ex.getMessage(), ex, executionContext);
		}

		// result graph

		CopyUtil.copyContextNode(signature.getBaseContextNode(), operationResultGraph, null);

		// done

		return InterceptorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Getters and setters
	 */

	public SignatureCreator<? extends Signature> getSignatureCreator() {

		return this.signatureCreator;
	}

	public void setSignatureCreator(SignatureCreator<? extends Signature> signatureCreator) {

		this.signatureCreator = signatureCreator;
	}
}
