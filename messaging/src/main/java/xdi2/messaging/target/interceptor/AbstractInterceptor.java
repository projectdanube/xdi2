package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.AbstractExtension;

public abstract class AbstractInterceptor extends AbstractExtension implements Interceptor {

	public AbstractInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractInterceptor() {

		super();
	}
}
