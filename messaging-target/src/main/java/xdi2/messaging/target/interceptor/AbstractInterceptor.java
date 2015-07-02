package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.impl.AbstractExtension;

public abstract class AbstractInterceptor <CONTAINER >extends AbstractExtension<CONTAINER> implements Interceptor<CONTAINER> {

	public AbstractInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractInterceptor() {

		super();
	}
}
