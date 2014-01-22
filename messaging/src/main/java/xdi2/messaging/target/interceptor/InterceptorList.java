package xdi2.messaging.target.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.Prototype;

public class InterceptorList <CONTAINER> implements Iterable<Interceptor<CONTAINER>>, Prototype<InterceptorList<CONTAINER>>, Serializable {

	private static final long serialVersionUID = -2532712738486475044L;

	private List<Interceptor<CONTAINER>> interceptors;

	public InterceptorList() {

		super();

		this.interceptors = new ArrayList<Interceptor<CONTAINER>> ();
	}

	public void addInterceptor(Interceptor<CONTAINER> interceptor) {

		this.interceptors.add(interceptor);
	}

	@SuppressWarnings("unchecked")
	public <T extends Interceptor<?>> T getInterceptor(Class<T> clazz) {

		for (Interceptor<?> interceptor : this.interceptors) {

			if (clazz.isAssignableFrom(interceptor.getClass())) return (T) interceptor;
		}

		return null;
	}

	public void removeInterceptor(Interceptor<?> interceptor) {

		this.interceptors.remove(interceptor);
	}

	public boolean isEmpty() {

		return this.interceptors.isEmpty();
	}

	public int size() {

		return this.interceptors.size();
	}

	@Override
	public Iterator<Interceptor<CONTAINER>> iterator() {

		return this.interceptors.iterator();
	}

	public String stringList() {

		StringBuffer buffer = new StringBuffer();

		for (Interceptor<?> interceptor : this.interceptors) {

			if (buffer.length() > 0) buffer.append(",");
			buffer.append(interceptor.getClass().getSimpleName());
		}

		return buffer.toString();
	}

	public <T> Iterator<T> findInterceptors(Class<T> clazz) {

		return new SelectingClassIterator<Interceptor<CONTAINER>, T> (this.iterator(), clazz);
	}

	/*
	 * Prototype
	 */

	@SuppressWarnings("unchecked")
	@Override
	public InterceptorList<CONTAINER> instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor list

		InterceptorList<CONTAINER> interceptorList = new InterceptorList<CONTAINER>();

		// add interceptors

		for (Interceptor<?> interceptor : this.interceptors) {

			if (! (interceptor instanceof Prototype<?>)) {

				throw new Xdi2MessagingException("Cannot use interceptor " + interceptor.getClass().getSimpleName() + " as prototype.", null, null);
			}

			try {

				Prototype<? extends Interceptor<CONTAINER>> interceptorPrototype = (Prototype<? extends Interceptor<CONTAINER>>) interceptor;
				Interceptor<CONTAINER> prototypedInterceptor = prototypingContext.instanceFor(interceptorPrototype);

				interceptorList.addInterceptor(prototypedInterceptor);
			} catch (Xdi2MessagingException ex) {

				throw new Xdi2MessagingException("Cannot instantiate interceptor for prototype " + interceptor.getClass().getSimpleName() + ": " + ex.getMessage(), ex, null);
			}
		}

		// done

		return interceptorList;
	}
}
