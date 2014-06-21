package xdi2.webtools.operator;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
import xdi2.client.util.XDIClientUtil;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.linkcontracts.GenericLinkContract;
import xdi2.core.features.linkcontracts.PublicLinkContract;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.util.iterators.MappingStatementXriIterator;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

/**
 * Servlet implementation class for Servlet: XDIOperator
 *
 */
public class XDIOperator extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -3912797900351698765L;

	private static Logger log = LoggerFactory.getLogger(XDIOperator.class);

	private static MemoryGraphFactory graphFactory;
	private static String sampleInput;
	private static String sampleEndpoint;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInput = "=alice";

		sampleEndpoint = XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT.getEndpointUri().toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setAttribute("resultFormat", XDIDisplayWriter.FORMAT_NAME);
		request.setAttribute("writeImplied", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writeInner", "on");
		request.setAttribute("writePretty", null);
		request.setAttribute("input", sampleInput);
		request.setAttribute("endpoint", sampleEndpoint);

		if (request.getParameter("input") != null) {

			request.setAttribute("input", request.getParameter("input"));
		}

		if (request.getParameter("endpoint") != null) {

			request.setAttribute("endpoint", request.getParameter("endpoint"));
		}

		request.getRequestDispatcher("/XDIOperator.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if ("login".equals(request.getParameter("cmd"))) 
			login(request, response);
		else if ("logout".equals(request.getParameter("cmd"))) 
			logout(request, response);
		else if ("buildPlain".equals(request.getParameter("cmd"))) 
			buildPlain(request, response);
		else if ("buildRootLinkContract".equals(request.getParameter("cmd"))) 
			buildRootLinkContract(request, response);
		else if ("buildPublicLinkContract".equals(request.getParameter("cmd"))) 
			buildPublicLinkContract(request, response);
		else if ("buildGenericLinkContract".equals(request.getParameter("cmd"))) 
			buildGenericLinkContract(request, response);
		else if ("message".equals(request.getParameter("cmd"))) 
			message(request, response);
	}

	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String input = request.getParameter("cloudName");
		String secretToken = request.getParameter("secretToken");
		String endpoint = request.getParameter("endpoint");
		String error = null;

		XDIDiscoveryResult discoveryResult = null;

		try {

			// start discovery

			XDIDiscoveryClient discoveryClient = new XDIDiscoveryClient(endpoint);

			discoveryClient.setRegistryCache(null);
			discoveryClient.setAuthorityCache(null);

			// discover

			discoveryResult = discoveryClient.discover(XDI3Segment.create(input), null);

			if (discoveryResult == null) throw new RuntimeException("No discovery result");
			if (discoveryResult.getCloudNumber() == null) throw new RuntimeException("No cloud number");
			if (discoveryResult.getXdiEndpointUri() == null) throw new RuntimeException("No XDI endpoint URI");

			// check result

			CloudNumber cloudNumber = discoveryResult.getCloudNumber();
			String xdiEndpointUri = discoveryResult.getXdiEndpointUri();

			// authenticate

			XDIClientUtil.authenticateSecretToken(cloudNumber, xdiEndpointUri, secretToken);

			// login

			request.getSession().setAttribute("sessionInput", input);
			request.getSession().setAttribute("sessionSecretToken", secretToken);
			request.getSession().setAttribute("sessionCloudNumber", cloudNumber);
			request.getSession().setAttribute("sessionXdiEndpointUri", xdiEndpointUri);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// display results

		request.setAttribute("input", input);
		request.setAttribute("secretToken", secretToken);
		request.setAttribute("endpoint", endpoint);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIOperator.jsp").forward(request, response);
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String error = null;

		try {

			// logout

			request.getSession().removeAttribute("sessionInput");
			request.getSession().removeAttribute("sessionSecretToken");
			request.getSession().removeAttribute("sessionCloudNumber");
			request.getSession().removeAttribute("sessionXdiEndpointUri");
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// display results

		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIOperator.jsp").forward(request, response);
	}

	private void buildPlain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String error = null;

		String secretToken = (String) request.getSession().getAttribute("sessionSecretToken");
		CloudNumber cloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber");

		StringWriter output = new StringWriter();

		try {

			// build

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.createMessage(cloudNumber.getXri());
			message.setToPeerRootXri(cloudNumber.getPeerRootXri());
			message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
			message.setSecretToken(secretToken);
			message.createGetOperation(XDIConstants.XRI_S_ROOT);

			XDIWriterRegistry.getDefault().write(messageEnvelope.getGraph(), output);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// return result

		response.getWriter().write(output.getBuffer().toString());
	}

	private void buildRootLinkContract(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String error = null;

		String secretToken = (String) request.getSession().getAttribute("sessionSecretToken");
		CloudNumber cloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber");

		StringWriter output = new StringWriter();

		try {

			// build

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.createMessage(cloudNumber.getXri());
			message.setToPeerRootXri(cloudNumber.getPeerRootXri());
			message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
			message.setSecretToken(secretToken);
			message.createGetOperation(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));

			XDIWriterRegistry.getDefault().write(messageEnvelope.getGraph(), output);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// return result

		response.getWriter().write(output.getBuffer().toString());
	}

	private void buildPublicLinkContract(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String error = null;

		String secretToken = (String) request.getSession().getAttribute("sessionSecretToken");
		CloudNumber cloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber");

		StringWriter output = new StringWriter();

		try {

			// build

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.createMessage(cloudNumber.getXri());
			message.setToPeerRootXri(cloudNumber.getPeerRootXri());
			message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
			message.setSecretToken(secretToken);
			message.createGetOperation(PublicLinkContract.createPublicLinkContractXri(cloudNumber.getXri()));

			XDIWriterRegistry.getDefault().write(messageEnvelope.getGraph(), output);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// return result

		response.getWriter().write(output.getBuffer().toString());
	}

	private void buildGenericLinkContract(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestingAuthority = request.getParameter("requestingAuthority");
		String error = null;

		String secretToken = (String) request.getSession().getAttribute("sessionSecretToken");
		CloudNumber cloudNumber = (CloudNumber) request.getSession().getAttribute("sessionCloudNumber");

		StringWriter output = new StringWriter();

		try {

			// build

			Graph graph = graphFactory.openGraph();
			GenericLinkContract.findGenericLinkContract(graph, cloudNumber.getXri(), XDI3Segment.create(requestingAuthority), null, true);

			MessageEnvelope messageEnvelope = new MessageEnvelope();
			Message message = messageEnvelope.createMessage(cloudNumber.getXri());
			message.setToPeerRootXri(cloudNumber.getPeerRootXri());
			message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
			message.setSecretToken(secretToken);
			message.createSetOperation(new MappingStatementXriIterator(graph.getRootContextNode().getAllStatements()));

			XDIWriterRegistry.getDefault().write(messageEnvelope.getGraph(), output);
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		// return result

		response.getWriter().write(output.getBuffer().toString());
	}

	private void message(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String resultFormat = request.getParameter("resultFormat");
		String writeImplied = request.getParameter("writeImplied");
		String writeOrdered = request.getParameter("writeOrdered");
		String writeInner = request.getParameter("writeInner");
		String writePretty = request.getParameter("writePretty");
		String input = request.getParameter("input");
		String endpoint = request.getParameter("endpoint");
		String output = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "on".equals(writeInner) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

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
		if (messageEnvelope != null) stats += Long.toString(messageEnvelope.getMessageCount()) + " message(s). ";
		if (messageEnvelope != null) stats += Long.toString(messageEnvelope.getOperationCount()) + " operation(s). ";
		if (messageResult != null) stats += Long.toString(messageResult.getGraph().getRootContextNode(true).getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeImplied", writeImplied);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writeInner", writeInner);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("input", input);
		request.setAttribute("endpoint", endpoint);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}
}
