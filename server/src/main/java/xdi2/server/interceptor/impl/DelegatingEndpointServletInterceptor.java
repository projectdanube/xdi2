package xdi2.server.interceptor.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;
import xdi2.server.interceptor.AbstractEndpointServletInterceptor;

public class DelegatingEndpointServletInterceptor extends AbstractEndpointServletInterceptor {

	private static final Logger log = LoggerFactory.getLogger(DelegatingEndpointServletInterceptor.class);

	private Servlet delegationServlet;
	private boolean delegateGet;
	private boolean delegatePost;
	private boolean delegatePut;
	private boolean delegateDelete;

	public DelegatingEndpointServletInterceptor() {

		super();

		this.delegateGet = true;
		this.delegatePost = true;
		this.delegatePut = true;
		this.delegateDelete = true;
	}

	@Override
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! this.getDelegateGet()) return false;

		if (log.isDebugEnabled()) log.debug("Delegating GET to " + this.getDelegationServlet().getClass().getCanonicalName());

		this.getDelegationServlet().service(request, response);

		return true;
	}

	@Override
	public boolean processPostRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! this.getDelegatePost()) return false;

		if (log.isDebugEnabled()) log.debug("Delegating POST to " + this.getDelegationServlet().getClass().getCanonicalName());

		this.getDelegationServlet().service(request, response);

		return true;
	}

	@Override
	public boolean processPutRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! this.getDelegatePut()) return false;

		if (log.isDebugEnabled()) log.debug("Delegating PUT to " + this.getDelegationServlet().getClass().getCanonicalName());

		this.getDelegationServlet().service(request, response);

		return true;
	}

	@Override
	public boolean processDeleteRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! this.getDelegateDelete()) return false;

		if (log.isDebugEnabled()) log.debug("Delegating DELETE to " + this.getDelegationServlet().getClass().getCanonicalName());

		this.getDelegationServlet().service(request, response);

		return true;
	}

	public Servlet getDelegationServlet() {

		return this.delegationServlet;
	}

	public void setDelegationServlet(Servlet delegationServlet) {

		this.delegationServlet = delegationServlet;
	}

	public boolean getDelegateGet() {

		return this.delegateGet;
	}

	public void setDelegateGet(boolean delegateGet) {

		this.delegateGet = delegateGet;
	}

	public boolean getDelegatePost() {

		return this.delegatePost;
	}

	public void setDelegatePost(boolean delegatePost) {

		this.delegatePost = delegatePost;
	}

	public boolean getDelegatePut() {

		return this.delegatePut;
	}

	public void setDelegatePut(boolean delegatePut) {

		this.delegatePut = delegatePut;
	}

	public boolean getDelegateDelete() {

		return this.delegateDelete;
	}

	public void setDelegateDelete(boolean delegateDelete) {

		this.delegateDelete = delegateDelete;
	}
}
