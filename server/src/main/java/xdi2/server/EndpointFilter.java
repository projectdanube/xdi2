package xdi2.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.factory.MessagingTargetFactory;
import xdi2.server.registry.EndpointRegistry;

/**
 * The EndpointFilter examines the request path.
 * Based on this, it decides which messaging target a request applies to.
 * If no messaging target can be found at the request path, the EndpointFilter tries to find
 * a messaging target factory that can create one.
 * 
 * The messaging target plus path information is then stored as request attributes to be
 * used by the EndpointServlet.
 * 
 * @author markus
 */
public class EndpointFilter implements Filter {

	private static Logger log = LoggerFactory.getLogger(EndpointFilter.class.getName());

	private EndpointServlet endpointServlet;
	private boolean initialized;

	public EndpointFilter() {

		super();

		this.endpointServlet = null;
		this.initialized = false;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		if (this.isInitialized()) {

			log.debug("Already initialized.");
			return;
		}

		log.info("Initializing...");

		// done

		this.initialized = true;

		log.info("Initializing complete.");
	}

	@Override
	public void destroy() {

		if (! this.isInitialized()) {

			log.debug("Not initialized.");
			return;
		}

		log.info("Shutting down.");

		// done

		this.initialized = false;

		log.info("Shutting down complete.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		EndpointRegistry endpointRegistry = this.getEndpointServlet().getEndpointRegistry();

		// parse request info

		RequestInfo requestInfo = RequestInfo.parse((HttpServletRequest) request);

		// check which messaging target this request applies to

		String messagingTargetPath = endpointRegistry.findMessagingTargetPath(requestInfo.getRequestPath());
		MessagingTarget messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);

		log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? "null" : messagingTarget.getClass().getSimpleName()));

		// check which messaging target factory this request applies to

		String messagingTargetFactoryPath = endpointRegistry.findMessagingTargetFactoryPath(requestInfo.getRequestPath());
		MessagingTargetFactory messagingTargetFactory = messagingTargetFactoryPath == null ? null : endpointRegistry.getMessagingTargetFactory(messagingTargetFactoryPath);

		log.debug("messagingTargetFactoryPath=" + messagingTargetFactoryPath + ", messagingTargetFactory=" + (messagingTargetFactory == null ? "null" : messagingTargetFactory.getClass().getSimpleName()));

		if (messagingTargetFactory != null) {

			// if we don't have a messaging target, see if the messaging target factory can create one

			if (messagingTarget == null) {

				try {

					messagingTargetFactory.mountMessagingTarget(endpointRegistry, messagingTargetFactoryPath, requestInfo.getRequestPath());
				} catch (Exception ex) {

					log.error("Unexpected exception: " + ex.getMessage(), ex);
					handleInternalException(request, response, ex);
					return;
				}
			} else {

				// if we do have a messaging target, see if the messaging target factory wants to modify or remove it

				try {

					messagingTargetFactory.updateMessagingTarget(endpointRegistry, messagingTargetFactoryPath, requestInfo.getRequestPath(), messagingTarget);
				} catch (Exception ex) {

					log.error("Unexpected exception: " + ex.getMessage(), ex);
					handleInternalException(request, response, ex);
					return;
				}
			}

			// after the messaging target factory did its work, look for the messaging target again

			messagingTargetPath = endpointRegistry.findMessagingTargetPath(requestInfo.getRequestPath());
			messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);

			log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? "null" : messagingTarget.getClass().getSimpleName()));
		}

		// update request info

		requestInfo.setMessagingTargetPath(messagingTargetPath);

		// done

		request.setAttribute("requestInfo", requestInfo);
		request.setAttribute("messagingTarget", messagingTarget);

		chain.doFilter(request, response);
	}

	public EndpointServlet getEndpointServlet() {

		return this.endpointServlet;
	}

	public void setEndpointServlet(EndpointServlet endpointServlet) {

		this.endpointServlet = endpointServlet;
	}

	protected static void handleInternalException(ServletRequest request, ServletResponse response, Exception ex) throws IOException {

		if (! response.isCommitted()) {

			((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
		}
	}

	public boolean isInitialized() {

		return this.initialized;
	}
}
