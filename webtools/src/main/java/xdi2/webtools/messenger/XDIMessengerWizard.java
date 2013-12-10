package xdi2.webtools.messenger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class XDIMessengerWizard extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -1480219686007433689L;

	public static final String DEFAULT_SENDER_STRING = "$anon";
	public static final String DEFAULT_LINKCONTRACT_STRING = "$do";
	public static final String DEFAULT_OPERATION_STRING = "$get";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String senderString = request.getParameter("sender");
		String recipientString = request.getParameter("recipient");
		String linkContractString = request.getParameter("linkContract");
		String messageTypeString = request.getParameter("messageType");
		String secretTokenString = request.getParameter("secretToken");
		String signatureString = request.getParameter("signature");
		String signatureDigestAlgorithmString = request.getParameter("signatureDigestAlgorithm");
		String signatureDigestLengthString = request.getParameter("signatureDigestLength");
		String signatureKeyAlgorithmString = request.getParameter("signatureKeyAlgorithm");
		String signatureKeyLengthString = request.getParameter("signatureKeyLength");
		String operationString = request.getParameter("operation");
		String targetString = request.getParameter("target");

		// set up parameters

		if (senderString == null || senderString.trim().isEmpty()) senderString = DEFAULT_SENDER_STRING;
		if (recipientString == null || recipientString.trim().isEmpty()) throw new ServletException("No recipient.");
		if (linkContractString == null || linkContractString.trim().isEmpty()) linkContractString = DEFAULT_LINKCONTRACT_STRING;
		if (operationString == null || operationString.trim().isEmpty()) operationString = DEFAULT_OPERATION_STRING;
		if (targetString == null || linkContractString.trim().isEmpty()) throw new ServletException("No target.");

		XDI3Segment sender = XDI3Segment.create(senderString);
		XDI3Segment recipient = XDI3Segment.create(recipientString);
		XDI3Segment linkContract = XDI3Segment.create(linkContractString);
		XDI3Segment messageType = messageTypeString == null ? null : XDI3Segment.create(messageTypeString);
		String secretToken = secretTokenString;
		String signature = signatureString;
		String signatureDigestAlgorithm = signatureDigestAlgorithmString;
		int signatureDigestLength = Integer.parseInt(signatureDigestLengthString);
		String signatureKeyAlgorithm = signatureKeyAlgorithmString;
		int signatureKeyLength = Integer.parseInt(signatureKeyLengthString);
		XDI3Segment operation = XDI3Segment.create(operationString);
		String target = targetString;

		// create message XDI

		Message message = new MessageEnvelope().createMessage(sender);

		message.setFromPeerRootXri(XdiPeerRoot.createPeerRootArcXri(sender));
		message.setToPeerRootXri(XdiPeerRoot.createPeerRootArcXri(recipient));
		message.setLinkContractXri(linkContract);
		if (messageType != null) message.setMessageType(messageType);
		if (secretToken != null) message.setSecretToken(secretToken);
		if (signature != null && signatureDigestAlgorithm != null && signatureDigestLength > 0 && signatureKeyAlgorithm != null && signatureKeyLength > 0) message.setSignature(signatureDigestAlgorithm, signatureDigestLength, signatureKeyAlgorithm, signatureKeyLength).setValue(signature);
		message.createOperation(operation, target);

		// output it

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "1");
		XDIJSONWriter xdiWriter = (XDIJSONWriter) XDIWriterRegistry.forFormat("XDI/JSON", parameters);
		StringWriter buffer = new StringWriter();
		xdiWriter.write(message.getMessageEnvelope().getGraph(), buffer);
		response.setContentType(XDIJSONWriter.MIME_TYPE.getMimeType());
		response.getWriter().write(buffer.getBuffer().toString());
	}   	  	    
}
