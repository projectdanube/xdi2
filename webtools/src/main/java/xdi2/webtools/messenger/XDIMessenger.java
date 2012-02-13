package xdi2.webtools.messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AutoReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.webtools.converter.XDIConverter;

/**
 * Servlet implementation class for Servlet: XDIMessenger
 *
 */
public class XDIMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -8317705299355338065L;

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

		sampleEndpoint = "http://localhost:8080/mem-graph/"; 
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));
		request.setAttribute("endpoint", sampleEndpoint);
		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String input = request.getParameter("input");
		String endpoint = request.getParameter("endpoint");
		String output = "";
		String stats = "-1";
		String error = null;

		AutoReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiWriter = XDIWriterRegistry.forFormat("XDI/JSON");

		MessageEnvelope messageEnvelope = null;
		MessageResult messageResult = null;

		long start = System.currentTimeMillis();

		try {

			// parse the message envelope

			messageEnvelope = MessageEnvelope.newInstance();

			xdiReader.read(messageEnvelope.getGraph(), new StringReader(input), null);

			// send the message envelope and read result

			XDIClient client = new XDIHttpClient(endpoint);

			messageResult = client.send(messageEnvelope, null);

			// output the message result

			StringWriter writer = new StringWriter();

			xdiWriter.write(messageResult.getGraph(), writer, null);

			output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());
		} catch (Exception ex) {

			ex.printStackTrace(System.err);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + " (" + xdiReader.getLastSuccessfulReader().getFormat() + "). ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getMessageCount()) + " message(s). ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getOperationCount()) + " operation(s). ";
		if (messageResult != null) stats += Integer.toString(messageResult.getGraph().getRootContextNode().getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", input);
		request.setAttribute("endpoint", endpoint);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}   	  	    
}
