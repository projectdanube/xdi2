package xdi2.messaging.target.interceptor;

public abstract class AbstractInterceptor implements Interceptor {

	private boolean enabled;
	
	public AbstractInterceptor() {
		
		this.enabled = true;
	}

	@Override
	public boolean isEnabled() {
	
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
	
		this.enabled = enabled;
	}
}
