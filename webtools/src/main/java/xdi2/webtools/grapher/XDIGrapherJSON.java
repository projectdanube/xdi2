package xdi2.webtools.grapher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XDIGrapherJSON extends HttpServlet {

	private static final long serialVersionUID = 5381568312723605483L;

	public XDIGrapherJSON() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String graphId = request.getParameter("graphId");
		String json = JSONCache.get(graphId);

		response.setContentType("application/json");
		response.getWriter().write(json);
		response.getWriter().flush();
		response.getWriter().close();
	}
}
