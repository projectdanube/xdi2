package xdi2.server.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.interceptor.HttpTransportInterceptor;

public class InterceptorList extends ArrayList<Interceptor> {

	private static final long serialVersionUID = 8865002767079128342L;

	private static final Logger log = LoggerFactory.getLogger(InterceptorList.class);

	public InterceptorList() {

		super();
	}

	public void addInterceptor(Interceptor interceptor) {

		this.add(interceptor);
	}

	public void removeInterceptor(Interceptor interceptor) {

		this.remove(interceptor);
	}

	/*
	 * Methods for executing interceptors
	 */

	public void executeHttpTransportInterceptorsInit(HttpTransport httpTransport) throws Xdi2ServerException {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (init).");

			httpTransportInterceptor.init(httpTransport);
		}
	}

	public void executeHttpTransportInterceptorsDestroy(HttpTransport httpTransport) {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (destroy).");

			httpTransportInterceptor.destroy(httpTransport);
		}
	}

	public boolean executeHttpTransportInterceptorsGet(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (GET).");

			if (httpTransportInterceptor.processGetRequest(httpTransport, request, response, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": GET request has been fully handled by interceptor " + httpTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeHttpTransportInterceptorsPut(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (PUT).");

			if (httpTransportInterceptor.processPutRequest(httpTransport, request, response, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": PUT request has been fully handled by interceptor " + httpTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeHttpTransportInterceptorsPost(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (POST).");

			if (httpTransportInterceptor.processPostRequest(httpTransport, request, response, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": POST request has been fully handled by interceptor " + httpTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeHttpTransportInterceptorsDelete(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		for (Iterator<HttpTransportInterceptor> httpTransportInterceptors = this.findHttpTransportInterceptors(); httpTransportInterceptors.hasNext(); ) {

			HttpTransportInterceptor httpTransportInterceptor = httpTransportInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + httpTransportInterceptor.getClass().getSimpleName() + " (DELETE).");

			if (httpTransportInterceptor.processDeleteRequest(httpTransport, request, response, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": DELETE request has been fully handled by interceptor " + httpTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * Methods for finding interceptors
	 */

	public Iterator<HttpTransportInterceptor> findHttpTransportInterceptors() {

		return new SelectingClassIterator<Interceptor, HttpTransportInterceptor> (this.iterator(), HttpTransportInterceptor.class);
	}
}
