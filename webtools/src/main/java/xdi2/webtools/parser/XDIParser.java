package xdi2.webtools.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coasttocoastresearch.apg.Parser.Result;
import com.coasttocoastresearch.apg.Statistics;
import com.coasttocoastresearch.apg.Trace;

/**
 * Servlet implementation class for Servlet: XDIParser
 *
 */
public class XDIParser extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1108625332231655077L;

	private static Logger log = LoggerFactory.getLogger(XDIParser.class);

	private static String sampleInput;

	static {

		InputStream inputStream = XDIParser.class.getResourceAsStream("sample.xri");
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

	public XDIParser() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("rules", xdi2.core.xri3.parser.aparse.ParserRules.rules);
		request.setAttribute("rulename", "xdi-statement");
		request.setAttribute("parser", "aparse");
		request.setAttribute("input", sampleInput);
		request.getRequestDispatcher("/XDIParser.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String rulename = request.getParameter("rulename");
		String parser = request.getParameter("parser");
		String input = request.getParameter("input");
		String output1 = "";
		String output2 = "";
		String output3 = "";
		String output4 = "";
		String output5 = "";
		String output6 = "";
		String output7 = "";
		String error = null;

		try {

			ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer3 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer4 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer5 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer6 = new ByteArrayOutputStream();
			ByteArrayOutputStream buffer7 = new ByteArrayOutputStream();
			PrintStream stream1 = new PrintStream(buffer1);
			PrintStream stream2 = new PrintStream(buffer2);
			PrintStream stream3 = new PrintStream(buffer3);
			PrintStream stream4 = new PrintStream(buffer4);
			PrintStream stream5 = new PrintStream(buffer5);
			PrintStream stream6 = new PrintStream(buffer6);
			PrintStream stream7 = new PrintStream(buffer7);

			if ("aparse".equals(parser)) {

				List<Deque<String>> stackDeques;
				Set<Entry<String, Integer>> countEntrySet;

				xdi2.core.xri3.parser.aparse.Rule rule = xdi2.core.xri3.parser.aparse.Parser.parse(rulename, input);

				// tree

				rule.accept(new xdi2.core.xri3.parser.aparse.TreeDisplayer(stream1));
				output1 = html(new String(buffer1.toByteArray(), "UTF-8"));

				// stack

				xdi2.core.xri3.parser.aparse.DequesVisitor dequesVisitor = new xdi2.core.xri3.parser.aparse.DequesVisitor();
				rule.accept(dequesVisitor);
				stackDeques = dequesVisitor.getDeques();

				// xml

				PrintStream out = System.out;
				System.setOut(stream3);
				rule.accept(new xdi2.core.xri3.parser.aparse.XmlDisplayer());
				System.setOut(out);

				// count

				xdi2.core.xri3.parser.aparse.CountVisitor countVisitor = new xdi2.core.xri3.parser.aparse.CountVisitor();
				rule.accept(countVisitor);
				countEntrySet = countVisitor.getCount().entrySet();

				stream2.println("<table border='1' cellpadding='5'><tr>");
				for (Deque<String> deque : stackDeques) {

					String terminal = deque.peekLast().replace("\"", "&quot;");
					StringBuffer stack = new StringBuffer();
					String stackentry;
					while ((stackentry = deque.pollFirst()) != null) stack.append(stackentry.replace("\"", "&quot;") + "<br>");
					stream2.println("<td onmouseover=\"document.getElementById('stack').innerHTML='" + stack.toString() + "';\" style='cursor:default;font-size:13pt;font-weight:bold;'>" + terminal + "</td>");
				}
				stream2.println("</tr></table>");
				stream2.println("<div id='stack'></div>");
				output2 = new String(buffer2.toByteArray(), "UTF-8");

				// xml

				output3 = html(new String(buffer3.toByteArray(), "UTF-8"));

				// count

				for (Entry<String, Integer> entry : countEntrySet) stream4.println(entry.getKey() + ": " + entry.getValue());
				output4 = html(new String(buffer4.toByteArray(), "UTF-8"));
			} else if ("apg".equals(parser)) {

				com.coasttocoastresearch.apg.Grammar g;
				int r = -1;

				g = xdi2.core.xri3.parser.apg.XDI3Grammar.getInstance();
				for (xdi2.core.xri3.parser.apg.XDI3Grammar.RuleNames rule : xdi2.core.xri3.parser.apg.XDI3Grammar.RuleNames.values()) if (rule.ruleName().equals(rulename)) r = rule.ruleID();

				com.coasttocoastresearch.apg.Parser p = new com.coasttocoastresearch.apg.Parser(g);

				p.setStartRule(r);
				p.setInputString(input);

				Statistics statistics = p.enableStatistics(true);

				Trace trace = p.enableTrace(true);
				trace.setOut(stream7);

				Result result = p.parse();

				result.displayResult(stream5);
				output5 = html(new String(buffer5.toByteArray(), "UTF-8"));

				statistics.displayStats(stream6, "rules");
				output6 = html(new String(buffer6.toByteArray(), "UTF-8"));

				output7 = html(new String(buffer7.toByteArray(), "UTF-8"));
			}
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// display results

		request.setAttribute("rules", xdi2.core.xri3.parser.aparse.ParserRules.rules);
		request.setAttribute("rulename", rulename);
		request.setAttribute("parser", parser);
		request.setAttribute("input", input.replace("\"", "&quot;"));
		request.setAttribute("output1", output1);
		request.setAttribute("output2", output2);
		request.setAttribute("output3", output3);
		request.setAttribute("output4", output4);
		request.setAttribute("output5", output5);
		request.setAttribute("output6", output6);
		request.setAttribute("output7", output7);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIParser.jsp").forward(request, response);
	}

	private static String html(String string) {

		return string.replace("<", "&lt;").replace(">", "&gt;");
	}
}
