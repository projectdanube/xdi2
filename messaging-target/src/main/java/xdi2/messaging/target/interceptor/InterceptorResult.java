package xdi2.messaging.target.interceptor;

import java.io.Serializable;

public class InterceptorResult implements Serializable {

	private static final long serialVersionUID = -1768104415596751188L;

	public static final InterceptorResult DEFAULT = new InterceptorResult(false, false);
	public static final InterceptorResult SKIP_SIBLING_INTERCEPTORS = new InterceptorResult(true, true);
	public static final InterceptorResult SKIP_MESSAGING_TARGET = new InterceptorResult(false, true);
	public static final InterceptorResult SKIP_SIBLING_INTERCEPTORS_AND_MESSAGING_TARGET = new InterceptorResult(true, true);

	private boolean skipSiblingInterceptors;
	private boolean skipMessagingTarget;

	public InterceptorResult(boolean skipSiblingInterceptors, boolean skipMessagingTarget) {

		this.skipSiblingInterceptors = skipSiblingInterceptors;
		this.skipMessagingTarget = skipMessagingTarget;
	}

	public boolean isSkipSiblingInterceptors() {

		return this.skipSiblingInterceptors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public InterceptorResult or(InterceptorResult interceptorResult) {

		boolean skipSiblingInterceptors = this.skipSiblingInterceptors || interceptorResult.skipSiblingInterceptors;
		boolean skipMessagingTarget = this.skipMessagingTarget || interceptorResult.skipMessagingTarget;

		return new InterceptorResult(skipSiblingInterceptors, skipMessagingTarget);
	}

	@Override
	public String toString() {

		return "[skipSiblingInterceptors:" + this.skipSiblingInterceptors + ",skipMessagingTarget:" + this.skipMessagingTarget + "]";
	}
}
