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
 * The EndpointFilter examines which messaging target a request applies to.
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		log.info("Initializing...");

		log.info("Initializing complete.");
	}

	@Override
	public void destroy() {

		log.info("Shutting down.");

		log.info("Shutting down complete.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		EndpointRegistry endpointRegistry = this.getEndpointServlet().getEndpointRegistry();

		// check which messaging target this request applies to

		String requestPath = findRequestPath((HttpServletRequest) request);
		String messagingTargetPath = endpointRegistry.findMessagingTargetPath(requestPath);
		MessagingTarget messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);

		log.debug("requestPath=" + requestPath + ", messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? "null" : messagingTarget.getClass().getSimpleName()));

		// if we don't have one, try to find a messaging target factory

		if (messagingTarget == null) {

			log.debug("No messaging target found. Looking for messaging target factory.");

			String messagingTargetFactoryPath = endpointRegistry.findMessagingTargetFactoryPath(requestPath);
			MessagingTargetFactory messagingTargetFactory = messagingTargetFactoryPath == null ? null : endpointRegistry.getMessagingTargetFactory(messagingTargetFactoryPath);

			log.debug("messagingTargetFactoryPath=" + messagingTargetFactoryPath + ", messagingTargetFactory=" + (messagingTargetFactory == null ? "null" : messagingTargetFactory.getClass().getSimpleName()));

			if (messagingTargetFactory != null) {

				try {

					messagingTargetFactory.mountMessagingTarget(endpointRegistry, messagingTargetFactoryPath, requestPath);
				} catch (Exception ex) {

					log.error("Unexpected exception: " + ex.getMessage(), ex);
					handleInternalException(request, response, ex);
					return;
				}
			}

			messagingTargetPath = endpointRegistry.findMessagingTargetPath(requestPath);
			messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);
		}

		// set attributes

		request.setAttribute("requestPath", requestPath);
		request.setAttribute("messagingTargetPath", messagingTargetPath);
		request.setAttribute("MessagingTarget", messagingTarget);

		// done

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

	protected static String findRequestPath(HttpServletRequest request) {

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String servletPath = request.getServletPath();
		String requestPath = requestUri.substring(contextPath.length() + servletPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.debug("requestUri: " + requestUri);
		log.debug("contextPath: " + contextPath);
		log.debug("servletPath: " + servletPath);
		log.debug("requestPath: " + requestPath);

		return requestPath;
	}
}
