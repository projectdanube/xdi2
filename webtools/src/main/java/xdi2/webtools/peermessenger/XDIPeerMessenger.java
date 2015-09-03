package xdi2.webtools.peermessenger;

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

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.local.XDILocalAgentRouter;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.readers.AutoReader;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.FromInterceptor;
import xdi2.messaging.target.interceptor.impl.MessagePolicyInterceptor;
import xdi2.messaging.target.interceptor.impl.ReadOnlyInterceptor;
import xdi2.messaging.target.interceptor.impl.RefInterceptor;
import xdi2.messaging.target.interceptor.impl.ToInterceptor;
import xdi2.messaging.target.interceptor.impl.VariablesInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.target.interceptor.impl.push.BasicPushGateway;
import xdi2.messaging.target.interceptor.impl.push.PushGateway;
import xdi2.messaging.target.interceptor.impl.push.PushOutInterceptor;
import xdi2.webtools.util.OutputCache;

/**
 * Servlet implementation class for Servlet: XDIPeerMessenger
 *
 */
public class XDIPeerMessenger extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -3045990823228900171L;

	private static Logger log = LoggerFactory.getLogger(XDIPeerMessenger.class);

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleCategories;
	private static List<List<String>> sampleInputs1;
	private static List<List<String>> sampleInputs2;
	private static List<List<String>> sampleMessages1;
	private static List<List<String>> sampleMessages2;
	private static List<List<String>> sampleTooltips;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleCategories = new ArrayList<String> ();
		sampleInputs1 = new ArrayList<List<String>> ();
		sampleInputs2 = new ArrayList<List<String>> ();
		sampleMessages1 = new ArrayList<List<String>> ();
		sampleMessages2 = new ArrayList<List<String>> ();
		sampleTooltips = new ArrayList<List<String>> ();

		int i;

		while (true) {

			InputStream inputStream1 = XDIPeerMessenger.class.getResourceAsStream("category" + (sampleCategories.size() + 1) + ".txt");
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();

			try {

				while ((i = inputStream1.read()) != -1) outputStream1.write(i);
				sampleCategories.add(new String(outputStream1.toByteArray()));
				sampleInputs1.add(new ArrayList<String> ());
				sampleInputs2.add(new ArrayList<String> ());
				sampleMessages1.add(new ArrayList<String> ());
				sampleMessages2.add(new ArrayList<String> ());
				sampleTooltips.add(new ArrayList<String> ());
			} catch (Exception ex) {

				break;
			} finally {

				try { inputStream1.close(); } catch (Exception ex) { }
				try { outputStream1.close(); } catch (Exception ex) { }
			}

			for (int c=0; c<sampleCategories.size(); c++) {

				while (true) {

					InputStream inputStream2 = XDIPeerMessenger.class.getResourceAsStream("graph" + (c + 1) + "_" + (sampleInputs1.get(c).size() + 1) + "_1.xdi");
					InputStream inputStream3 = XDIPeerMessenger.class.getResourceAsStream("graph" + (c + 1) + "_" + (sampleInputs2.get(c).size() + 1) + "_2.xdi");
					InputStream inputStream4 = XDIPeerMessenger.class.getResourceAsStream("message" + (c + 1) + "_" + (sampleMessages1.get(c).size() + 1) + "_1.xdi");
					InputStream inputStream5 = XDIPeerMessenger.class.getResourceAsStream("message" + (c + 1) + "_" + (sampleMessages2.get(c).size() + 1) + "_2.xdi");
					InputStream inputStream6 = XDIPeerMessenger.class.getResourceAsStream("tooltip" + (c + 1) + "_" + (sampleTooltips.get(c).size() + 1) + ".txt");
					ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
					ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
					ByteArrayOutputStream outputStream4 = new ByteArrayOutputStream();
					ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
					ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();

					try {

						while ((i = inputStream2.read()) != -1) outputStream2.write(i);
						while ((i = inputStream3.read()) != -1) outputStream3.write(i);
						while ((i = inputStream4.read()) != -1) outputStream4.write(i);
						while ((i = inputStream5.read()) != -1) outputStream5.write(i);
						while ((i = inputStream6.read()) != -1) outputStream6.write(i);
						sampleInputs1.get(c).add(new String(outputStream2.toByteArray()).trim());
						sampleInputs2.get(c).add(new String(outputStream3.toByteArray()).trim());
						sampleMessages1.get(c).add(new String(outputStream4.toByteArray()).trim());
						sampleMessages2.get(c).add(new String(outputStream5.toByteArray()).trim());
						sampleTooltips.get(c).add(new String(outputStream6.toByteArray()).trim());
					} catch (Exception ex) {

						break;
					} finally {

						try { inputStream2.close(); } catch (Exception ex) { }
						try { inputStream3.close(); } catch (Exception ex) { }
						try { inputStream4.close(); } catch (Exception ex) { }
						try { inputStream5.close(); } catch (Exception ex) { }
						try { inputStream6.close(); } catch (Exception ex) { }
						try { outputStream2.close(); } catch (Exception ex) { }
						try { outputStream3.close(); } catch (Exception ex) { }
						try { outputStream4.close(); } catch (Exception ex) { }
						try { outputStream5.close(); } catch (Exception ex) { }
						try { outputStream6.close(); } catch (Exception ex) { }
					}
				}
			}
		}
	}


	public XDIPeerMessenger() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String category = request.getParameter("category");
		if (category == null) category = "1";

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleCategories", sampleCategories);
		request.setAttribute("sampleInputs1", sampleInputs1);
		request.setAttribute("sampleInputs2", sampleInputs2);
		request.setAttribute("sampleMessages1", sampleMessages1);
		request.setAttribute("sampleMessages2", sampleMessages2);
		request.setAttribute("sampleTooltips", sampleTooltips);
		request.setAttribute("category", category);
		request.setAttribute("sample", sample);
		request.setAttribute("resultFormat", XDIWriterRegistry.getDefault().getFormat());
		request.setAttribute("writeImplied", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writePretty", null);
		request.setAttribute("useFromInterceptor", null);
		request.setAttribute("useToInterceptor", "on");
		request.setAttribute("useVariablesInterceptor", null);
		request.setAttribute("useRefInterceptor", "on");
		request.setAttribute("useReadOnlyInterceptor", null);
		request.setAttribute("useMessagePolicyInterceptor", null);
		request.setAttribute("useLinkContractInterceptor", null);
		request.setAttribute("usePushCommandInterceptor", "on");
		request.setAttribute("input1", sampleInputs1.get(Integer.parseInt(category) - 1).get(Integer.parseInt(sample) - 1));
		request.setAttribute("input2", sampleInputs2.get(Integer.parseInt(category) - 1).get(Integer.parseInt(sample) - 1));
		request.setAttribute("message1", sampleMessages1.get(Integer.parseInt(category) - 1).get(Integer.parseInt(sample) - 1));
		request.setAttribute("message2", sampleMessages2.get(Integer.parseInt(category) - 1).get(Integer.parseInt(sample) - 1));

		request.getRequestDispatcher("/XDIPeerMessenger.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String category = request.getParameter("category");
		String sample = request.getParameter("sample");
		String resultFormat = request.getParameter("resultFormat");
		String writeImplied = request.getParameter("writeImplied");
		String writeOrdered = request.getParameter("writeOrdered");
		String writePretty = request.getParameter("writePretty");
		String useFromInterceptor = request.getParameter("useFromInterceptor");
		String useToInterceptor = request.getParameter("useToInterceptor");
		String useVariablesInterceptor = request.getParameter("useVariablesInterceptor");
		String useRefInterceptor = request.getParameter("useRefInterceptor");
		String useReadOnlyInterceptor = request.getParameter("useReadOnlyInterceptor");
		String useMessagePolicyInterceptor = request.getParameter("useMessagePolicyInterceptor");
		String useLinkContractInterceptor = request.getParameter("useLinkContractInterceptor");
		String usePushCommandInterceptor = request.getParameter("usePushCommandInterceptor");
		String input1 = request.getParameter("input1");
		String input2 = request.getParameter("input2");
		String message1 = request.getParameter("message1");
		String message2 = request.getParameter("message2");
		String output1 = "";
		String output2 = "";
		String outputId1 = "";
		String outputId2 = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		XDIWriter xdiInputWriter1;
		XDIWriter xdiInputWriter2;
		XDIWriter xdiResultWriter1 = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);
		XDIWriter xdiResultWriter2 = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);
		MessageEnvelope messageEnvelope1 = null;
		MessageEnvelope messageEnvelope2 = null;
		MessagingResponse messagingResponse1 = null;
		MessagingResponse messagingResponse2 = null;
		Graph graphInput1 = graphFactory.openGraph();
		Graph graphInput2 = graphFactory.openGraph();

		long start = System.currentTimeMillis();

		try {

			// parse the input graph and remember its format

			xdiReader.read(graphInput1, new StringReader(input1));
			String inputFormat1 = ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat();

			xdiReader.read(graphInput2, new StringReader(input2));
			String inputFormat2 = ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat();

			// parse the message envelope

			messageEnvelope1 = new MessageEnvelope();
			messageEnvelope2 = new MessageEnvelope();

			xdiReader.read(messageEnvelope1.getGraph(), new StringReader(message1));
			xdiReader.read(messageEnvelope2.getGraph(), new StringReader(message2));

			// prepare the messaging target

			GraphMessagingTarget messagingTarget1 = new GraphMessagingTarget();
			GraphMessagingTarget messagingTarget2 = new GraphMessagingTarget();

			messagingTarget1.setGraph(graphInput1);
			messagingTarget2.setGraph(graphInput2);

			if ("on".equals(useFromInterceptor)) {

				FromInterceptor fromInterceptor1 = new FromInterceptor();
				FromInterceptor fromInterceptor2 = new FromInterceptor();
				messagingTarget1.getInterceptors().addInterceptor(fromInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(fromInterceptor2);
			}

			if ("on".equals(useToInterceptor)) {

				ToInterceptor toInterceptor1 = new ToInterceptor();
				ToInterceptor toInterceptor2 = new ToInterceptor();
				messagingTarget1.getInterceptors().addInterceptor(toInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(toInterceptor2);
			}

			if ("on".equals(useVariablesInterceptor)) {

				VariablesInterceptor variablesInterceptor1 = new VariablesInterceptor();
				VariablesInterceptor variablesInterceptor2 = new VariablesInterceptor();
				messagingTarget1.getInterceptors().addInterceptor(variablesInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(variablesInterceptor2);
			}

			if ("on".equals(useRefInterceptor)) {

				RefInterceptor refInterceptor1 = new RefInterceptor();
				RefInterceptor refInterceptor2 = new RefInterceptor();
				messagingTarget1.getInterceptors().addInterceptor(refInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(refInterceptor2);
			}

			if ("on".equals(useReadOnlyInterceptor)) {

				ReadOnlyInterceptor readOnlyInterceptor1 = new ReadOnlyInterceptor();
				ReadOnlyInterceptor readOnlyInterceptor2 = new ReadOnlyInterceptor();
				readOnlyInterceptor1.setReadOnlyAddresses(new XDIAddress[] { XDIAddress.create("") });
				readOnlyInterceptor2.setReadOnlyAddresses(new XDIAddress[] { XDIAddress.create("") });
				messagingTarget1.getInterceptors().addInterceptor(readOnlyInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(readOnlyInterceptor2);
			}

			if ("on".equals(useMessagePolicyInterceptor)) {

				MessagePolicyInterceptor messagePolicyInterceptor1 = new MessagePolicyInterceptor();
				MessagePolicyInterceptor messagePolicyInterceptor2 = new MessagePolicyInterceptor();
				messagePolicyInterceptor1.setMessagePolicyGraph(graphInput1);
				messagePolicyInterceptor2.setMessagePolicyGraph(graphInput2);
				messagingTarget1.getInterceptors().addInterceptor(messagePolicyInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(messagePolicyInterceptor2);
			}

			if ("on".equals(useLinkContractInterceptor)) {

				LinkContractInterceptor linkContractInterceptor1 = new LinkContractInterceptor();
				LinkContractInterceptor linkContractInterceptor2 = new LinkContractInterceptor();
				linkContractInterceptor1.setLinkContractsGraph(graphInput1);
				linkContractInterceptor2.setLinkContractsGraph(graphInput2);
				messagingTarget1.getInterceptors().addInterceptor(linkContractInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(linkContractInterceptor2);
			}

			if ("on".equals(usePushCommandInterceptor)) {

				List<XDIAgentRouter<?, ?>> agentRouters = new ArrayList<XDIAgentRouter<?, ?>> ();
				agentRouters.add(new XDILocalAgentRouter(messagingTarget1));
				agentRouters.add(new XDILocalAgentRouter(messagingTarget2));

				XDIAgent xdiAgent = new XDIBasicAgent(agentRouters);

				PushGateway pushCommandExecutor = new BasicPushGateway(xdiAgent, null);

				PushOutInterceptor pushCommandInterceptor1 = new PushOutInterceptor();
				PushOutInterceptor pushCommandInterceptor2 = new PushOutInterceptor();
				pushCommandInterceptor1.setPushLinkContractsGraph(graphInput1);
				pushCommandInterceptor2.setPushLinkContractsGraph(graphInput2);
				pushCommandInterceptor1.setPushGateway(pushCommandExecutor);
				pushCommandInterceptor2.setPushGateway(pushCommandExecutor);
				messagingTarget1.getInterceptors().addInterceptor(pushCommandInterceptor1);
				messagingTarget2.getInterceptors().addInterceptor(pushCommandInterceptor2);
			}

			messagingTarget1.init();
			messagingTarget2.init();

			// send the message envelope and read result

			XDILocalClient client1 = new XDILocalClient(messagingTarget1);
			XDILocalClient client2 = new XDILocalClient(messagingTarget2);

			messagingResponse1 = client1.send(messageEnvelope1);
			messagingResponse2 = client2.send(messageEnvelope2);

			// output the modified input graph

			xdiInputWriter1 = XDIWriterRegistry.forFormat(inputFormat1, null);
			xdiInputWriter2 = XDIWriterRegistry.forFormat(inputFormat2, null);

			StringWriter writer1 = new StringWriter();
			StringWriter writer2 = new StringWriter();
			xdiInputWriter1.write(graphInput1, writer1);
			xdiInputWriter2.write(graphInput2, writer2);
			input1 = StringEscapeUtils.escapeHtml(writer1.getBuffer().toString());
			input2 = StringEscapeUtils.escapeHtml(writer2.getBuffer().toString());

			// output the message result

			StringWriter resultWriter1 = new StringWriter();
			StringWriter resultWriter2 = new StringWriter();
			xdiResultWriter1.write(messagingResponse1.getGraph(), resultWriter1);
			xdiResultWriter2.write(messagingResponse2.getGraph(), resultWriter1);
			output1 = StringEscapeUtils.escapeHtml(resultWriter1.getBuffer().toString());
			output2 = StringEscapeUtils.escapeHtml(resultWriter2.getBuffer().toString());

			outputId1 = UUID.randomUUID().toString();
			outputId2 = UUID.randomUUID().toString();
			OutputCache.put(outputId1, messagingResponse1.getGraph());
			OutputCache.put(outputId2, messagingResponse2.getGraph());
		} catch (Exception ex) {

			if (ex instanceof Xdi2ClientException) {

				messagingResponse1 = ((Xdi2ClientException) ex).getMessagingResponse();

				// output the message result

				if (messagingResponse1 != null ) {

					StringWriter resultWriter1 = new StringWriter();
					xdiResultWriter1.write(messagingResponse1.getGraph(), resultWriter1);
					output1 = StringEscapeUtils.escapeHtml(resultWriter1.getBuffer().toString());

					outputId1 = UUID.randomUUID().toString();
					OutputCache.put(outputId1, messagingResponse1.getGraph());
				}
			}

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		} finally {

			graphInput1.close();
			graphInput2.close();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";
		if (messageEnvelope1 != null) stats += Long.toString(messageEnvelope1.getMessageCount()) + " message(s). ";
		if (messageEnvelope2 != null) stats += Long.toString(messageEnvelope2.getMessageCount()) + " message(s). ";
		if (messageEnvelope1 != null) stats += Long.toString(messageEnvelope1.getOperationCount()) + " operation(s). ";
		if (messageEnvelope2 != null) stats += Long.toString(messageEnvelope2.getOperationCount()) + " operation(s). ";
		if (messagingResponse1 != null) stats += Long.toString(messagingResponse1.getGraph().getRootContextNode(true).getAllStatementCount()) + " result statement(s). ";
		if (messagingResponse2 != null) stats += Long.toString(messagingResponse2.getGraph().getRootContextNode(true).getAllStatementCount()) + " result statement(s). ";

		// display results

		request.setAttribute("sampleCategories", sampleCategories);
		request.setAttribute("sampleInputs1", sampleInputs1);
		request.setAttribute("sampleInputs2", sampleInputs2);
		request.setAttribute("sampleMessages1", sampleMessages1);
		request.setAttribute("sampleMessages2", sampleMessages2);
		request.setAttribute("sampleTooltips", sampleTooltips);
		request.setAttribute("category", category);
		request.setAttribute("sample", sample);
		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeImplied", writeImplied);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("useFromInterceptor", useFromInterceptor);
		request.setAttribute("useToInterceptor", useToInterceptor);
		request.setAttribute("useVariablesInterceptor", useVariablesInterceptor);
		request.setAttribute("useRefInterceptor", useRefInterceptor);
		request.setAttribute("useReadOnlyInterceptor", useReadOnlyInterceptor);
		request.setAttribute("useMessagePolicyInterceptor", useMessagePolicyInterceptor);
		request.setAttribute("useLinkContractInterceptor", useLinkContractInterceptor);
		request.setAttribute("usePushCommandInterceptor", usePushCommandInterceptor);
		request.setAttribute("input1", input1);
		request.setAttribute("input2", input2);
		request.setAttribute("message1", message1);
		request.setAttribute("message2", message2);
		request.setAttribute("output1", output1);
		request.setAttribute("output2", output2);
		request.setAttribute("outputId1", outputId1);
		request.setAttribute("outputId2", outputId2);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIPeerMessenger.jsp").forward(request, response);
	}   	  	    
}
