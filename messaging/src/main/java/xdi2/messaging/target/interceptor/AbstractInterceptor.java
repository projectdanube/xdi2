package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.AbstractDecorator;

public abstract class AbstractInterceptor extends AbstractDecorator implements Interceptor {

	public AbstractInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractInterceptor() {

		super();
	}
}
