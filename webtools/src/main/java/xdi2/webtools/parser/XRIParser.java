package xdi2.webtools.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Deque;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.impl.parser.CountVisitor;
import xdi2.core.xri3.impl.parser.DequesVisitor;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserRules;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.TreeDisplayer;
import xdi2.core.xri3.impl.parser.XmlDisplayer;

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

		request.setAttribute("rules", ParserRules.rules);
		request.setAttribute("rulename", "xdi-statement");
		request.setAttribute("input", sampleInput);
		request.getRequestDispatcher("/XRIParser.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String rulename = request.getParameter("rulename");
		String input = request.getParameter("input");
		String output1 = "";
		String output2 = "";
		String output3 = "";
		String output4 = "";
		String error = null;

		try {

			ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer3 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer4 = new ByteArrayOutputStream();
			PrintStream stream1 = new PrintStream(buffer1);
			PrintStream stream2 = new PrintStream(buffer2);
			PrintStream stream3 = new PrintStream(buffer3);
			PrintStream stream4 = new PrintStream(buffer4);

			Rule rule = Parser.parse(rulename, input);

			// tree

			rule.accept(new TreeDisplayer(stream1));
			output1 = html(new String(buffer1.toByteArray(), "UTF-8"));

			// xml

			PrintStream out = System.out;
			System.setOut(stream2);
			rule.accept(new XmlDisplayer());
			System.setOut(out);
			output2 = html(new String(buffer2.toByteArray(), "UTF-8"));

			// count

			DequesVisitor dequesVisitor = new DequesVisitor();
			rule.accept(dequesVisitor);
			stream3.println("<table border='1' cellpadding='5'><tr>");
			for (Deque<String> deque : dequesVisitor.getDeques()) {
				String terminal = deque.peekLast();
				StringBuffer stack = new StringBuffer();
				String stackentry;
				while ((stackentry = deque.pollFirst()) != null) {
					stack.append(stackentry + "<br>");
				}
				stream3.println("<td onmouseover=\"document.getElementById('stack').innerHTML='" + stack.toString() + "';\" style='cursor:default;font-size:13pt;font-weight:bold;'>" + terminal + "</td>");
			}
			stream3.println("</tr></table>");
			stream3.println("<div id='stack'></div>");
			output3 = new String(buffer3.toByteArray(), "UTF-8");

			// count

			CountVisitor countVisitor = new CountVisitor();
			rule.accept(countVisitor);
			for (Entry<String, Integer> entry : countVisitor.getCount().entrySet()) stream4.println(entry.getKey() + ": " + entry.getValue());
			output4 = html(new String(buffer4.toByteArray(), "UTF-8"));
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// display results

		request.setAttribute("rules", ParserRules.rules);
		request.setAttribute("rulename", rulename);
		request.setAttribute("input", input);
		request.setAttribute("output1", output1);
		//request.setAttribute("output2", output2);
		request.setAttribute("output3", output3);
		request.setAttribute("output4", output4);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XRIParser.jsp").forward(request, response);
	}

	private static String html(String string) {

		return string.replace("<", "&lt;").replace(">", "&gt;");
	}
}
