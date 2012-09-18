package xdi2.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.interceptor.EndpointServletInterceptor;

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

	public void  executeEndpointServletInterceptorsInit(EndpointServlet endpointServlet) throws ServletException {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (init).");

			endpointServletInterceptor.init(endpointServlet);
		}
	}

	public void  executeEndpointServletInterceptorsDestroy(EndpointServlet endpointServlet) {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (destroy).");

			endpointServletInterceptor.destroy(endpointServlet);
		}
	}

	public boolean executeEndpointServletInterceptorsGet(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (GET).");

			if (endpointServletInterceptor.processGetRequest(endpointServlet, request, response, requestInfo, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": GET request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeEndpointServletInterceptorsPut(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (PUT).");

			if (endpointServletInterceptor.processPutRequest(endpointServlet, request, response, requestInfo, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": PUT request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeEndpointServletInterceptorsPost(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (POST).");

			if (endpointServletInterceptor.processPostRequest(endpointServlet, request, response, requestInfo, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": POST request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public boolean executeEndpointServletInterceptorsDelete(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.findEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (DELETE).");

			if (endpointServletInterceptor.processDeleteRequest(endpointServlet, request, response, requestInfo, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": DELETE request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * Methods for finding interceptors
	 */

	public Iterator<EndpointServletInterceptor> findEndpointServletInterceptors() {

		return new SelectingClassIterator<Interceptor, EndpointServletInterceptor> (this.iterator(), EndpointServletInterceptor.class);
	}
}
