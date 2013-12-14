package xdi2.messaging.target.interceptor;

import java.io.Serializable;

public class InterceptorResult implements Serializable {

	private static final long serialVersionUID = -1768104415596751188L;

	public static final InterceptorResult DEFAULT = new InterceptorResult(false, false);

	private boolean skipSiblingInterceptors;
	private boolean skipMessagingTarget;

	public InterceptorResult(boolean skipSiblingInterceptors, boolean skipMessagingTarget) {

		this.skipSiblingInterceptors = skipSiblingInterceptors;
		this.skipMessagingTarget = skipMessagingTarget;
	}

	public boolean isSkipSiblingInterceptors() {

		return this.skipSiblingInterceptors;
	}

	public void setSkipSiblingInterceptors(boolean skipSiblingInterceptors) {

		this.skipSiblingInterceptors = skipSiblingInterceptors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public void setSkipMessagingTarget(boolean skipMessagingTarget) {

		this.skipMessagingTarget = skipMessagingTarget;
	}

	public void or(InterceptorResult interceptorResult) {

		this.skipSiblingInterceptors = this.skipSiblingInterceptors || interceptorResult.skipSiblingInterceptors;
		this.skipMessagingTarget = this.skipMessagingTarget || interceptorResult.skipMessagingTarget;
	}

	@Override
	public String toString() {

		return "[skipSiblingInterceptors:" + this.isSkipSiblingInterceptors() + ",skipMessagingTarget:" + this.isSkipMessagingTarget() + "]";
	}
}
