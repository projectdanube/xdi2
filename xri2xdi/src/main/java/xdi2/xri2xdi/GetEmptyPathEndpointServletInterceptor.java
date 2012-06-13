package xdi2.xri2xdi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.interceptor.AbstractEndpointServletInterceptor;

public class GetEmptyPathEndpointServletInterceptor extends AbstractEndpointServletInterceptor {

	@Override
	public boolean processGetRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (path.isEmpty()) {

			response.sendRedirect("/index.html");
			return true;
		}

		return false;
	}
}
