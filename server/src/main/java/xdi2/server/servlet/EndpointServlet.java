package xdi2.server.servlet;


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

import xdi2.server.registry.HttpEndpointRegistry;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

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

	private HttpEndpointRegistry httpEndpointRegistry;
	private HttpTransport httpTransport;

	public EndpointServlet() {

		super();

		this.httpEndpointRegistry = new HttpEndpointRegistry();
		this.httpTransport = new HttpTransport(this.httpEndpointRegistry);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isDebugEnabled()) log.debug("Setting application context.");

		this.httpEndpointRegistry = (HttpEndpointRegistry) applicationContext.getBean("HttpEndpointRegistry");
		if (this.httpEndpointRegistry == null) throw new NoSuchBeanDefinitionException("Required bean 'HttpEndpointRegistry' not found.");

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
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doPost(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doPut(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doDelete(request, response);
	}

	/*
	 * Getters and setters
	 */

	public HttpEndpointRegistry getHttpEndpointRegistry() {

		return this.httpEndpointRegistry;
	}

	public void setHttpEndpointRegistry(HttpEndpointRegistry httpEndpointRegistry) {

		this.httpEndpointRegistry = httpEndpointRegistry;
	}

	public HttpTransport getHttpTransport() {

		return this.httpTransport;
	}

	public void setHttpTransport(HttpTransport httpTransport) {

		this.httpTransport = httpTransport;
	}
}
