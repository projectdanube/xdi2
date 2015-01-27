package xdi2.transport.impl.http.impl.servlet;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import xdi2.transport.impl.http.HttpRequest;
import xdi2.transport.impl.http.HttpResponse;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;
import xdi2.transport.impl.websocket.endpoint.WebSocketEndpoint;

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

	private HttpTransport httpTransport;

	public EndpointServlet() {

		super();

		this.httpTransport = new HttpTransport(new HttpMessagingTargetRegistry());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isInfoEnabled()) log.info("Setting application context.");

		// find bean

		this.httpTransport = (HttpTransport) applicationContext.getBean("HttpTransport");
		if (this.httpTransport == null) throw new NoSuchBeanDefinitionException("Required bean 'HttpTransport' not found.");
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		// find application context

		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		if (applicationContext != null) this.setApplicationContext(applicationContext);

		// install websocket endpoint

		try {

			WebSocketEndpoint.install(servletConfig.getServletContext(), "/");
		} catch (DeploymentException ex) {

			throw new ServletException("Cannot install websocket endpoint: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		httpServletRequest.setCharacterEncoding("UTF-8");
		httpServletResponse.setCharacterEncoding("UTF-8");

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

	@Override
	protected void doOptions(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		HttpRequest request = ServletHttpRequest.fromHttpServletRequest(httpServletRequest);
		HttpResponse response = ServletHttpResponse.fromHttpServletResponse(httpServletResponse);

		this.httpTransport.doOptions(request, response);
	}

	/*
	 * Getters and setters
	 */

	public HttpTransport getHttpTransport() {

		return this.httpTransport;
	}

	public void setHttpTransport(HttpTransport httpTransport) {

		this.httpTransport = httpTransport;
	}
}
