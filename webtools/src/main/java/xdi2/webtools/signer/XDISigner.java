package xdi2.webtools.signer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.signatures.Signature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.xri3.XDI3Segment;

/**
 * Servlet implementation class for Servlet: XDISigner
 *
 */
public class XDISigner extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 572272568648798655L;

	private static Logger log = LoggerFactory.getLogger(XDISigner.class);

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;
	private static List<String> sampleKeys;
	private static List<String> sampleAddresses;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();
		sampleKeys = new ArrayList<String> ();
		sampleAddresses = new ArrayList<String> ();

		while (true) {

			InputStream inputStream1 = XDISigner.class.getResourceAsStream("graph" + (sampleInputs.size() + 1) + ".xdi");
			InputStream inputStream2 = XDISigner.class.getResourceAsStream("key" + (sampleKeys.size() + 1));
			InputStream inputStream3 = XDISigner.class.getResourceAsStream("address" + (sampleAddresses.size() + 1));
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
			ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
			int i;

			try {

				while ((i = inputStream1.read()) != -1) outputStream1.write(i);
				while ((i = inputStream2.read()) != -1) outputStream2.write(i);
				while ((i = inputStream3.read()) != -1) outputStream3.write(i);
				sampleInputs.add(new String(outputStream1.toByteArray()));
				sampleKeys.add(new String(outputStream2.toByteArray()));
				sampleAddresses.add(new String(outputStream3.toByteArray()));
			} catch (Exception ex) {

				break;
			} finally {

				try {

					inputStream1.close();
					inputStream2.close();
					inputStream3.close();
					outputStream1.close();
					outputStream2.close();
					outputStream3.close();
				} catch (Exception ex) {

				}
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", XDIDisplayWriter.FORMAT_NAME);
		request.setAttribute("writeImplied", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writeInner", "on");
		request.setAttribute("writePretty", null);
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));
		request.setAttribute("key", sampleKeys.get(Integer.parseInt(sample) - 1));
		request.setAttribute("address", sampleAddresses.get(Integer.parseInt(sample) - 1));

		request.getRequestDispatcher("/XDISigner.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String resultFormat = request.getParameter("resultFormat");
		String writeImplied = request.getParameter("writeImplied");
		String writeOrdered = request.getParameter("writeOrdered");
		String writeInner = request.getParameter("writeInner");
		String writePretty = request.getParameter("writePretty");
		String input = request.getParameter("input");
		String key = request.getParameter("key");
		String address = request.getParameter("address");
		String submit = request.getParameter("submit");
		String output = "";
		String output2 = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "on".equals(writeInner) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiResultWriter = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);

		Graph graph = null;
		Key k = null;
		Signature signature = null;
		Boolean valid = null;

		long start = System.currentTimeMillis();

		try {

			// parse the graph

			graph = MemoryGraphFactory.getInstance().openGraph();

			xdiReader.read(graph, new StringReader(input));

			// find the context node

			ContextNode contextNode = graph.getDeepContextNode(XDI3Segment.create(address));
			if (contextNode == null) throw new RuntimeException("No context node found at address " + address);

			// sign or validate

			if ("Create RSA Signature!".equals(submit)) {

				signature = Signatures.getSignature(contextNode, true);

				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				k = keyFactory.generatePrivate(keySpec);

				signature.createSignature((PrivateKey) k);
			} else if ("Validate RSA Signature!".equals(submit)) {

				signature = Signatures.getSignature(contextNode, false);
				if (signature == null) throw new RuntimeException("No signature found at address " + address);

				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				k = keyFactory.generatePublic(keySpec);

				valid = Boolean.valueOf(signature.validateSignature((PublicKey) k));
			} else if ("Create AES HMAC!".equals(submit)) {

				signature = Signatures.getSignature(contextNode, true);

				k = new SecretKeySpec(Base64.decodeBase64(key), "AES");

				signature.createHMAC((SecretKey) k);
			} else if ("Validate AES HMAC!".equals(submit)) {

				signature = Signatures.getSignature(contextNode, false);
				if (signature == null) throw new RuntimeException("No HMAC found at address " + address);

				k = new SecretKeySpec(Base64.decodeBase64(key), "AES");

				valid = Boolean.valueOf(signature.validateHMAC((SecretKey) k));
			}

			// output the graph or result

			if (valid == null) {

				StringWriter writer = new StringWriter();

				xdiResultWriter.write(graph, writer);

				output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());
			} else {

				output = "Valid: " + valid.toString();
			}
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		if (signature != null) {

			output2 = signature.getNormalizedSerialization();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";
		if (k != null) stats += "Key algorithm: " + k.getAlgorithm() + ". ";
		if (k != null) stats += "Key format: " + k.getFormat() + ". ";
		if (k != null) stats += "Key encoded length: " + k.getEncoded().length + ". ";
		if (graph != null) stats += Long.toString(graph.getRootContextNode().getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeImplied", writeImplied);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writeInner", writeInner);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("input", input);
		request.setAttribute("key", key);
		request.setAttribute("address", address);
		request.setAttribute("output", output);
		request.setAttribute("output2", output2);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDISigner.jsp").forward(request, response);
	}
}
