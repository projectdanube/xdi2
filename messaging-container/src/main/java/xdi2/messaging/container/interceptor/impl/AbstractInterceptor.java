package xdi2.messaging.container.interceptor.impl;

import xdi2.messaging.container.impl.AbstractExtension;
import xdi2.messaging.container.interceptor.Interceptor;

public abstract class AbstractInterceptor <CONTAINER >extends AbstractExtension<CONTAINER> implements Interceptor<CONTAINER> {

	public AbstractInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractInterceptor() {

		super();
	}
}
