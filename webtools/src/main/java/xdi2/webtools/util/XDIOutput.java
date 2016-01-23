package xdi2.webtools.util;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.Graph;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;

public class XDIOutput extends HttpServlet {

	private static final long serialVersionUID = 1717417990243607701L;

	private static XDIWriter xdiWriter;

	static {

		Properties xdiWriterParameters = new Properties();

		xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "0");
		xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "0");
		xdiWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "0");

		xdiWriter = XDIWriterRegistry.forFormat("XDI DISPLAY", xdiWriterParameters);
	}

	public XDIOutput() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String outputId = request.getParameter("outputId");
		Graph graph = OutputCache.get(outputId);

		if (graph == null) {

			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		response.setContentType("text/plain");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Cache-Control, Expires, X-Cache, X-HTTP-Method-Override, Accept");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
		xdiWriter.write(graph, response.getWriter());
		response.getWriter().flush();
		response.getWriter().close();
	}
}
