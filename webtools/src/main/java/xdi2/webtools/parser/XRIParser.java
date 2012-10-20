package xdi2.webtools.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.TreeDisplayer;

/**
 * Servlet implementation class for Servlet: XRIParser
 *
 */
public class XRIParser extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1108625332231655077L;

	private static Logger log = LoggerFactory.getLogger(XRIParser.class);

	private static String sampleInput;

	static {

		InputStream inputStream = XRIParser.class.getResourceAsStream("sample.xri");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int i;

		try {

			while ((i = inputStream.read()) != -1) outputStream.write(i);
			sampleInput = new String(outputStream.toByteArray());
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		} finally {

			try {

				inputStream.close();
				outputStream.close();
			} catch (Exception ex) {

			}
		}
	}

	public XRIParser() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("rulename", "xri-reference");
		request.setAttribute("input", sampleInput);
		request.getRequestDispatcher("/XRIParser.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String rulename = request.getParameter("rulename");
		String input = request.getParameter("input");
		String output = "";
		String error = null;

		try {

			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			Rule rule = Parser.parse(rulename, input);

			new TreeDisplayer(new PrintStream(stream)).visit(rule);
			output = new String(stream.toByteArray(), "UTF-8");
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// display results

		request.setAttribute("rulename", rulename);
		request.setAttribute("input", input);
		request.setAttribute("output", output);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XRIParser.jsp").forward(request, response);
	}   	  	    
}
