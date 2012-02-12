package xdi2.webtools.localmessenger;

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

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.AutoReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.server.ExecutionContext;
import xdi2.server.impl.graph.GraphMessagingTarget;

/**
 * Servlet implementation class for Servlet: XDILocalMessenger
 *
 */
public class XDILocalMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -3840753270326755062L;

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;
	private static List<String> sampleMessages;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();
		sampleMessages = new ArrayList<String> ();

		while (true) {

			InputStream inputStream1 = XDILocalMessenger.class.getResourceAsStream("test" + (sampleInputs.size() + 1) + ".graph");
			InputStream inputStream2 = XDILocalMessenger.class.getResourceAsStream("message" + (sampleMessages.size() + 1) + ".graph");
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
			int i;

			try {

				while ((i = inputStream1.read()) != -1) outputStream1.write(i);
				while ((i = inputStream2.read()) != -1) outputStream2.write(i);
				sampleInputs.add(new String(outputStream1.toByteArray()));
				sampleMessages.add(new String(outputStream2.toByteArray()));
			} catch (Exception ex) {

				break;
			} finally {

				try {

					inputStream1.close();
					inputStream2.close();
					outputStream1.close();
					outputStream2.close();
				} catch (Exception ex) {

				}
			}
		}
	}


	public XDILocalMessenger() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));
		request.setAttribute("message", sampleMessages.get(Integer.parseInt(sample) - 1));
		request.getRequestDispatcher("/XDILocalMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String versioningSupport = request.getParameter("versioningSupport");
		String linkContractSupport = request.getParameter("linkContractSupport");
		String to = request.getParameter("to");
		String input = request.getParameter("input");
		String message = request.getParameter("message");
		String output = "";
		String stats = "-1";
		String error = null;

		AutoReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiInputWriter;
		XDIWriter xdiResultWriter = XDIWriterRegistry.forFormat(to);
		MessageEnvelope messageEnvelope = null;
		MessageResult messageResult = null;
		Graph graphInput = graphFactory.openGraph();

		try {

			// parse the input graph and remember its format

			xdiReader.read(graphInput, new StringReader(input), null);
			String inputFormat = input.trim().equals("") ? "X3 Simple" : xdiReader.getLastSuccessfulReader().getFormat();

			// parse the message envelope

			messageEnvelope = MessageEnvelope.newInstance();

			xdiReader.read(messageEnvelope.getGraph(), new StringReader(message), null);

			// apply the message envelope and read result

			GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
			messagingTarget.setGraph(graphInput);

			if ("on".equals(versioningSupport))
//				messagingTarget.getOperationInterceptors().add(new SubjectVersioningOperationInterceptor());

			if ("on".equals(linkContractSupport)) {

/*				LinkContractAddressInterceptor linkContractAddressInterceptor = new LinkContractAddressInterceptor();
				linkContractAddressInterceptor.setLinkContractGraph(graphInput);
				messagingTarget.getAddressInterceptors().add(linkContractAddressInterceptor);*/
			}

			messagingTarget.init(null);

			messageResult = MessageResult.newInstance();
			messagingTarget.execute(messageEnvelope, messageResult, new ExecutionContext());

			// output the modified input graph

			xdiInputWriter = XDIWriterRegistry.forFormat(inputFormat);
			if (xdiInputWriter == null) xdiInputWriter = XDIWriterRegistry.forFormat("X3 Simple");

			StringWriter writer1 = new StringWriter();
			xdiInputWriter.write(graphInput, writer1, null);
			input = StringEscapeUtils.escapeHtml(writer1.getBuffer().toString());

			// output the message result

			StringWriter writer2 = new StringWriter();
			xdiResultWriter.write(messageResult.getGraph(), writer2, null);
			output = StringEscapeUtils.escapeHtml(writer2.getBuffer().toString());
		} catch (Exception ex) {

			ex.printStackTrace(System.err);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		stats = "";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getMessageCount()) + " message(s). ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getOperationCount()) + " operation(s). ";
		if (messageResult != null) stats += Integer.toString(messageResult.getGraph().getRootContextNode().getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("versioningSupport", versioningSupport);
		request.setAttribute("linkContractSupport", linkContractSupport);
		request.setAttribute("to", to);
		request.setAttribute("input", input);
		request.setAttribute("message", message);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDILocalMessenger.jsp").forward(request, response);
	}   	  	    
}
