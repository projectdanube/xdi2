package xdi2.messaging.target.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;

public class InterceptorList <CONTAINER> implements Iterable<Interceptor<CONTAINER>>, Prototype<InterceptorList<CONTAINER>>, Serializable {

	private static final long serialVersionUID = -2532712738486475044L;

	private List<Interceptor<CONTAINER>> interceptors;

	public InterceptorList(InterceptorList<CONTAINER> interceptorList) {

		this.interceptors = new ArrayList<Interceptor<CONTAINER>> (interceptorList.interceptors);
	}

	public InterceptorList(Collection<Interceptor<CONTAINER>> interceptors) {

		this.interceptors = new ArrayList<Interceptor<CONTAINER>> (interceptors);
	}

	public InterceptorList(Interceptor<CONTAINER>[] interceptors) {

		this.interceptors = new ArrayList<Interceptor<CONTAINER>> (Arrays.asList(interceptors));
	}

	public InterceptorList() {

		this.interceptors = new ArrayList<Interceptor<CONTAINER>> ();
	}

	public void addInterceptors(InterceptorList<CONTAINER> interceptorList) {

		this.interceptors.addAll(interceptorList.interceptors);
	}

	public void addInterceptors(Collection<Interceptor<CONTAINER>> interceptors) {

		this.interceptors.addAll(interceptors);
	}

	public void addInterceptors(Interceptor<CONTAINER>[] interceptors) {

		this.interceptors.addAll(Arrays.asList(interceptors));
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
	public ReadOnlyIterator<Interceptor<CONTAINER>> iterator() {

		return new ReadOnlyIterator<Interceptor<CONTAINER>> (this.interceptors.iterator());
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

	public <T> T findInterceptor(Class<T> clazz) {

		Iterator<T> interceptors = findInterceptors(clazz);
		if (! interceptors.hasNext()) return null;

		return interceptors.next();
	}

	public void clearDisabledForOperation(Operation operation) {

		for (Interceptor<CONTAINER> interceptor : this.iterator()) {

			interceptor.clearDisabledForOperation(operation);
		}
	}

	public void clearDisabledForMessage(Message message) {

		for (Interceptor<CONTAINER> interceptor : this.iterator()) {

			interceptor.clearDisabledForMessage(message);
		}
	}

	public void clearDisabledForMessageEnvelope(MessageEnvelope messageEnvelope) {

		for (Interceptor<CONTAINER> interceptor : this.iterator()) {

			interceptor.clearDisabledForMessageEnvelope(messageEnvelope);
		}
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
