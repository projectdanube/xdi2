package xdi2.transport.impl.http.impl.servlet;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

/**
 * The XDI endpoint servlet.
 * 
 * It reads and installs all XDI messaging targets from a Spring application context.
 * 
 * @author markus
 */
public final class EndpointServlet extends HttpServlet implements ApplicationContextAware {

	private static final long serialVersionUID = -5653921904489832762L;

	private static final Logger log = LoggerFactory.getLogger(EndpointServlet.class);

	private UriMessagingTargetRegistry uriMessagingTargetRegistry;
	private HttpTransport httpTransport;

	public EndpointServlet() {

		super();

		this.uriMessagingTargetRegistry = new UriMessagingTargetRegistry();
		this.httpTransport = new HttpTransport(this.uriMessagingTargetRegistry);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isInfoEnabled()) log.info("Setting application context.");

		this.uriMessagingTargetRegistry = (UriMessagingTargetRegistry) applicationContext.getBean("UriMessagingTargetRegistry");
		if (this.uriMessagingTargetRegistry == null) throw new NoSuchBeanDefinitionException("Required bean 'UriMessagingTargetRegistry' not found.");

		this.httpTransport = (HttpTransport) applicationContext.getBean("HttpTransport");
		if (this.httpTransport == null) throw new NoSuchBeanDefinitionException("Required bean 'HttpTransport' not found.");
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		if (applicationContext != null) this.setApplicationContext(applicationContext);
	}

	@Override
	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

		// execute the transport

		HttpTransportRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpTransportResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.execute(request, response);
	}

	/*
	 * Getters and setters
	 */

	public UriMessagingTargetRegistry getUriMessagingTargetRegistry() {

		return this.uriMessagingTargetRegistry;
	}

	public void setUriMessagingTargetRegistry(UriMessagingTargetRegistry uriMessagingTargetRegistry) {

		this.uriMessagingTargetRegistry = uriMessagingTargetRegistry;
	}

	public HttpTransport getHttpTransport() {

		return this.httpTransport;
	}

	public void setHttpTransport(HttpTransport httpTransport) {

		this.httpTransport = httpTransport;
	}
}
