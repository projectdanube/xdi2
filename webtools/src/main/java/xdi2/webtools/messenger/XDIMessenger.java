package xdi2.webtools.messenger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.request.RequestMessage;
import xdi2.messaging.request.RequestMessageEnvelope;
import xdi2.webtools.util.OutputCache;

/**
 * Servlet implementation class for Servlet: XDIMessenger
 *
 */
public class XDIMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -8317705299355338065L;

	public static final String DEFAULT_SENDER_STRING = "$anon";
	public static final String DEFAULT_LINKCONTRACT_STRING = "$do";
	public static final String DEFAULT_OPERATION_STRING = "$get";
	public static final String DEFAULT_TARGET_STRING = "";

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

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", XDIDisplayWriter.FORMAT_NAME);
		request.setAttribute("writeImplied", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writePretty", null);
		request.setAttribute("input", sampleInputs.get(0));
		request.setAttribute("endpoint", request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")) + sampleEndpoint);

		if (request.getParameter("sample") != null) {

			request.setAttribute("input", sampleInputs.get(Integer.parseInt(request.getParameter("sample")) - 1));
		}

		if (request.getParameter("recipient") != null) {

			String senderString = request.getParameter("sender");
			String recipientString = request.getParameter("recipient");
			String linkContractString = request.getParameter("linkContract");
			String operationString = request.getParameter("operation");
			String targetString = request.getParameter("target");
			String messageTypeString = request.getParameter("messageType");
			String secretTokenString = request.getParameter("secretToken");
			String signatureString = request.getParameter("signature");
			String signatureDigestAlgorithmString = request.getParameter("signatureDigestAlgorithm");
			String signatureDigestLengthString = request.getParameter("signatureDigestLength");
			String signatureKeyAlgorithmString = request.getParameter("signatureKeyAlgorithm");
			String signatureKeyLengthString = request.getParameter("signatureKeyLength");
			String endpointString;

			if (senderString == null || senderString.trim().isEmpty()) senderString = DEFAULT_SENDER_STRING;
			if (recipientString == null || recipientString.trim().isEmpty()) throw new ServletException("No recipient.");
			if (linkContractString == null || linkContractString.trim().isEmpty()) linkContractString = DEFAULT_LINKCONTRACT_STRING;
			if (operationString == null || operationString.trim().isEmpty()) operationString = DEFAULT_OPERATION_STRING;
			if (targetString == null || targetString.trim().isEmpty()) targetString = DEFAULT_TARGET_STRING;

			try {

				if (senderString.toLowerCase().startsWith("ote:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(senderString.substring("ote:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT));
					senderString = xdiDiscoveryResult.getCloudNumber().getXDIAddress().toString();
				} else if (senderString.toLowerCase().startsWith("prod:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(senderString.substring("prod:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT));
					senderString = xdiDiscoveryResult.getCloudNumber().getXDIAddress().toString();
				}
			} catch (Exception ex) {

				request.setAttribute("error", "Problem with discovery on " + senderString + ": " + ex.getMessage());

				request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
				return;
			}

			try {

				if (recipientString.toLowerCase().startsWith("ote:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(recipientString.substring("ote:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT));
					recipientString = xdiDiscoveryResult.getCloudNumber().getXDIAddress().toString();
					endpointString = xdiDiscoveryResult.getXdiEndpointUrl().toString();

					request.setAttribute("endpoint", endpointString);
				} else if (recipientString.toLowerCase().startsWith("prod:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(recipientString.substring("prod:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT));
					recipientString = xdiDiscoveryResult.getCloudNumber().getXDIAddress().toString();
					endpointString = xdiDiscoveryResult.getXdiEndpointUrl().toString();

					request.setAttribute("endpoint", endpointString);
				}
			} catch (Exception ex) {

				request.setAttribute("error", "Problem with discovery on " + recipientString + ": " + ex.getMessage());

				request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
				return;
			}

			XDIAddress sender = XDIAddress.create(senderString);
			XDIAddress recipient = XDIAddress.create(recipientString);
			XDIAddress linkContract = XDIAddress.create(linkContractString);
			XDIAddress operation = XDIAddress.create(operationString);
			String target = targetString;
			XDIAddress messageType = messageTypeString == null ? null : XDIAddress.create(messageTypeString);
			String secretToken = secretTokenString;
			String signature = signatureString;
			String signatureDigestAlgorithm = signatureDigestAlgorithmString;
			int signatureDigestLength = signatureDigestLengthString == null ? -1 : Integer.parseInt(signatureDigestLengthString);
			String signatureKeyAlgorithm = signatureKeyAlgorithmString;
			int signatureKeyLength = signatureKeyLengthString == null ? -1 :Integer.parseInt(signatureKeyLengthString);

			RequestMessage message = new RequestMessageEnvelope().createMessage(sender);

			message.setFromPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(sender));
			message.setToPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(recipient));
			message.setLinkContractXDIAddress(linkContract);
			if (messageType != null) message.setMessageType(messageType);
			if (secretToken != null) message.setSecretToken(secretToken);
			if (signature != null && signatureDigestAlgorithm != null && signatureDigestLength > 0 && signatureKeyAlgorithm != null && signatureKeyLength > 0) message.createSignature(signatureDigestAlgorithm, signatureDigestLength, signatureKeyAlgorithm, signatureKeyLength, true).setValue(signature);
			message.createOperation(operation, target);

			Properties parameters = new Properties();
			XDIWriter xdiWriter = XDIWriterRegistry.forFormat("XDI DISPLAY", parameters);
			StringWriter buffer = new StringWriter();
			xdiWriter.write(message.getMessageEnvelope().getGraph(), buffer);

			request.setAttribute("input", buffer.getBuffer().toString());
		}

		if (request.getParameter("endpoint") != null) {

			String endpointString = request.getParameter("endpoint");

			try {

				if (endpointString.toLowerCase().startsWith("ote:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(endpointString.substring("ote:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_OTE_DISCOVERY_XDI_CLIENT));
					endpointString = xdiDiscoveryResult.getXdiEndpointUrl().toString();
				} else if (endpointString.toLowerCase().startsWith("prod:")) {

					XDIDiscoveryResult xdiDiscoveryResult = discover(XDIAddress.create(endpointString.substring("prod:".length())), new XDIDiscoveryClient(XDIDiscoveryClient.NEUSTAR_PROD_DISCOVERY_XDI_CLIENT));
					endpointString = xdiDiscoveryResult.getXdiEndpointUrl().toString();
				}
			} catch (Exception ex) {

				request.setAttribute("error", "Problem with discovery on " + endpointString + ": " + ex.getMessage());

				request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
				return;
			}

			request.setAttribute("endpoint", endpointString);
		}

		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String resultFormat = request.getParameter("resultFormat");
		String writeImplied = request.getParameter("writeImplied");
		String writeOrdered = request.getParameter("writeOrdered");
		String writePretty = request.getParameter("writePretty");
		String input = request.getParameter("input");
		String endpoint = request.getParameter("endpoint");
		String output = "";
		String outputId = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiResultWriter = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);

		RequestMessageEnvelope messageEnvelope = null;
		Graph resultGraph = null;

		long start = System.currentTimeMillis();

		try {

			// parse the message envelope

			messageEnvelope = new RequestMessageEnvelope();

			xdiReader.read(messageEnvelope.getGraph(), new StringReader(input));

			// send the message envelope and read result

			XDIClient client = new XDIHttpClient(endpoint);

			resultGraph = client.send(messageEnvelope).getResultGraph();

			// output the message result

			StringWriter writer = new StringWriter();
			xdiResultWriter.write(resultGraph, writer);
			output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());

			outputId = UUID.randomUUID().toString();
			OutputCache.put(outputId, resultGraph);
		} catch (Exception ex) {

			if (ex instanceof Xdi2ClientException) {

				Graph errorGraph = ((Xdi2ClientException) ex).getErrorMessagingResponse().getErrorGraph();

				// output the error graph

				if (errorGraph != null) {

					StringWriter writer2 = new StringWriter();
					xdiResultWriter.write(errorGraph, writer2);
					output = StringEscapeUtils.escapeHtml(writer2.getBuffer().toString());

					outputId = UUID.randomUUID().toString();
					OutputCache.put(outputId, errorGraph);
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
		if (resultGraph != null) stats += Long.toString(resultGraph.getRootContextNode(true).getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeImplied", writeImplied);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("input", input);
		request.setAttribute("endpoint", endpoint);
		request.setAttribute("output", output);
		request.setAttribute("outputId", outputId);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIMessenger.jsp").forward(request, response);
	}

	private static XDIDiscoveryResult discover(XDIAddress XDIaddress, XDIDiscoveryClient xdiDiscoveryClient) throws Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResult;

		xdiDiscoveryResult = xdiDiscoveryClient.discoverFromRegistry(XDIaddress, null);
		if (xdiDiscoveryResult.getCloudNumber() == null) throw new RuntimeException("No Cloud Number for " + XDIaddress);
		if (xdiDiscoveryResult.getXdiEndpointUrl() == null) throw new RuntimeException("No XDI endpoint URI for " + XDIaddress);

		return xdiDiscoveryResult;
	}
}
