package xdi2.messaging.container.interceptor;

import java.io.Serializable;

public class InterceptorResult implements Serializable {

	private static final long serialVersionUID = -1768104415596751188L;

	public static final InterceptorResult DEFAULT = new InterceptorResult(false, false);
	public static final InterceptorResult SKIP_SIBLING_INTERCEPTORS = new InterceptorResult(true, true);
	public static final InterceptorResult SKIP_MESSAGING_CONTAINER = new InterceptorResult(false, true);
	public static final InterceptorResult SKIP_SIBLING_INTERCEPTORS_AND_MESSAGING_CONTAINER = new InterceptorResult(true, true);

	private boolean skipSiblingInterceptors;
	private boolean skipMessagingContainer;

	public InterceptorResult(boolean skipSiblingInterceptors, boolean skipMessagingContainer) {

		this.skipSiblingInterceptors = skipSiblingInterceptors;
		this.skipMessagingContainer = skipMessagingContainer;
	}

	public boolean isSkipSiblingInterceptors() {

		return this.skipSiblingInterceptors;
	}

	public boolean isSkipMessagingContainer() {

		return this.skipMessagingContainer;
	}

	public InterceptorResult or(InterceptorResult interceptorResult) {

		boolean skipSiblingInterceptors = this.skipSiblingInterceptors || interceptorResult.skipSiblingInterceptors;
		boolean skipMessagingContainer = this.skipMessagingContainer || interceptorResult.skipMessagingContainer;

		return new InterceptorResult(skipSiblingInterceptors, skipMessagingContainer);
	}

	@Override
	public String toString() {

		return "[skipSiblingInterceptors:" + this.skipSiblingInterceptors + ",skipMessagingContainer:" + this.skipMessagingContainer + "]";
	}
}
