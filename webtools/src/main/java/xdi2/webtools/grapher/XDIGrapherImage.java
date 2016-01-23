package xdi2.webtools.grapher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XDIGrapherImage extends HttpServlet {

	private static final long serialVersionUID = 123784683432874632L;

	public XDIGrapherImage() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String graphId = request.getParameter("graphId");
		byte[] image = ImageCache.get(graphId);

		response.setContentType("image/png");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Cache-Control, Expires, X-Cache, X-HTTP-Method-Override, Accept");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
		response.getOutputStream().write(image);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
