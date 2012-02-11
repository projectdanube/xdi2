package xdi2.webtools.localmessenger;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import xdi2.server.impl.graph.SubjectVersioningOperationInterceptor;
import xdi2.server.interceptor.impl.RoutingMessageInterceptor;
import xdi2.server.interceptor.impl.authn.SignatureAuthenticationMessageInterceptor;
import xdi2.server.interceptor.impl.authz.LinkContractAddressInterceptor;

/**
 * Servlet implementation class for Servlet: XDILocalMessenger
 *
 */
public class XDILocalMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -3840753270326755062L;

	private static final Log log = LogFactory.getLog(XDILocalMessenger.class);

	private static MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	static {

		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);
	}

	private String sampleInput;
	private String sampleMessage;

	public XDILocalMessenger() {

		super();

		InputStream inputStream1 = this.getClass().getResourceAsStream("input.graph");
		InputStream inputStream2 = this.getClass().getResourceAsStream("message.graph");
		ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		int i;

		try {

			while ((i = inputStream1.read()) != -1) outputStream1.write(i);
			while ((i = inputStream2.read()) != -1) outputStream2.write(i);
			sampleInput = new String(outputStream1.toByteArray());
			sampleMessage = new String(outputStream2.toByteArray());
		} catch (Exception ex) {

			sampleInput = "[Error: Can't read sample data: " + ex.getMessage();
			sampleMessage = "[Error: Can't read sample data: " + ex.getMessage();
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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setAttribute("input", sampleInput);
		request.setAttribute("message", sampleMessage);
		request.getRequestDispatcher("/XDILocalMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String versioningSupport = request.getParameter("versioningSupport");
		String linkContractSupport = request.getParameter("linkContractSupport");
		String senderVerification = request.getParameter("senderVerification");
		String routing = request.getParameter("routing");
		String to = request.getParameter("to");
		String input = request.getParameter("input");
		String message = request.getParameter("message");
		String output = "";
		String stats = "-1";
		String error = null;

		AutoReader xdiReader = (AutoReader) XDIReaderRegistry.getAuto();
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
				messagingTarget.getOperationInterceptors().add(new SubjectVersioningOperationInterceptor());

			if ("on".equals(linkContractSupport)) {

				LinkContractAddressInterceptor linkContractAddressInterceptor = new LinkContractAddressInterceptor();
				linkContractAddressInterceptor.setLinkContractGraph(graphInput);
				messagingTarget.getAddressInterceptors().add(linkContractAddressInterceptor);
			}

			if ("on".equals(senderVerification))
				messagingTarget.getMessageInterceptors().add(new SignatureAuthenticationMessageInterceptor());

			if ("on".equals(routing)) 
				messagingTarget.getMessageInterceptors().add(new RoutingMessageInterceptor());

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

			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
			log.warn(error, ex);
		}

		stats = "";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getMessageCount()) + " message(s). ";
		if (messageEnvelope != null) stats += Integer.toString(messageEnvelope.getOperationCount()) + " operation(s). ";
		if (messageResult != null) stats += Integer.toString(messageResult.getGraph().getRootContextNode().getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("versioningSupport", versioningSupport);
		request.setAttribute("linkContractSupport", linkContractSupport);
		request.setAttribute("senderVerification", senderVerification);
		request.setAttribute("routing", routing);
		request.setAttribute("to", to);
		request.setAttribute("input", input);
		request.setAttribute("message", message);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDILocalMessenger.jsp").forward(request, response);
	}   	  	    
}
