package xdi2.webtools.messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

/**
 * Servlet implementation class for Servlet: XDIMessenger
 *
 */
public class XDIMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -8317705299355338065L;

	private static Logger log = LoggerFactory.getLogger(XDIMessenger.class);

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;
	private static String sampleEndpoint;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();

		while (true) {

			InputStream inputStream = XDIMessenger.class.getResourceAsStream("message" + (sampleInputs.size() + 1) + ".xdi");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int i;

			try {

				while ((i = inputStream.read()) != -1) outputStream.write(i);
				sampleInputs.add(new String(outputStream.toByteArray()));
			} catch (Exception ex) {

				break;
			} finally {

				try {

					inputStream.close();
					outputStream.close();
				} catch (Exception ex) {

				}
			}
		}

		sampleEndpoint = "/xdi/mem-graph/"; 
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("writeContexts", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writePretty", "on");
		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));
		request.setAttribute("endpoint", request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")) + sampleEndpoint);
		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String resultFormat = request.getParameter("resultFormat");
		String writeContexts = request.getParameter("writeContexts");
		String writeOrdered = request.getParameter("writeOrdered");
		String writePretty = request.getParameter("writePretty");
		String input = request.getParameter("input");
		String endpoint = request.getParameter("endpoint");
		String output = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		if ("on".equals(writeContexts)) xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, "1");
		if ("on".equals(writeOrdered)) xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		if ("on".equals(writePretty)) xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "1");

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiResultWriter = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);

		MessageEnvelope messageEnvelope = null;
		MessageResult messageResult = null;

		long start = System.currentTimeMillis();

		try {

			// parse the message envelope

			messageEnvelope = new MessageEnvelope();

			xdiReader.read(messageEnvelope.getGraph(), new StringReader(input));

			// send the message envelope and read result

			XDIClient client = new XDIHttpClient(endpoint);

			messageResult = client.send(messageEnvelope, null);

			// output the message result

			StringWriter writer = new StringWriter();

			xdiResultWriter.write(messageResult.getGraph(), writer);

			output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());
		} catch (Exception ex) {

			if (ex instanceof Xdi2ClientException) {

				messageResult = ((Xdi2ClientException) ex).getErrorMessageResult();

				// output the message result

				if (messageResult != null) {

					StringWriter writer2 = new StringWriter();
					xdiResultWriter.write(messageResult.getGraph(), writer2);
					output = StringEscapeUtils.escapeHtml(writer2.getBuffer().toString());
				}
			}

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getMessageCount()) + " message(s). ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getOperationCount()) + " operation(s). ";
		if (messageResult != null) stats += Integer.toString(messageResult.getGraph().getRootContextNode().getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeContexts", writeContexts);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("input", input);
		request.setAttribute("endpoint", endpoint);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}
}
