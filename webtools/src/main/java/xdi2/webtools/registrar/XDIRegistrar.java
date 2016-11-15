package xdi2.webtools.registrar;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.DID;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.ddo.DDO;
import xdi2.webtools.util.OutputCache;

/**
 * Servlet implementation class for Servlet: XDIRegistrar
 *
 */
public class XDIRegistrar extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = -2647638447991715387L;

	private static Logger log = LoggerFactory.getLogger(XDIRegistrar.class);

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;
	private static String sampleType;
	private static String sampleControl;
	private static String sampleEquivalent;
	private static String sampleGuardian;
	private static String sampleServices;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = Collections.singletonList("=!:did:sov:21tDAKCERh95uGgKbJNHYp");

		sampleType = "=";

		sampleControl = "{$self} =!:did:sov:bsAdB81oHKaCmLTsgajtp9 =!:did:sov:JwnauseVWxVwUJj3oWiagX";

		sampleEquivalent = "=!:did:sov:LbQkpEBuFHYW1KBWTQrPop =!:did:btc1:794856-624 =!:did:uport:0xa9be82e93628abaac5ab557a9b3b02f711c0151c";

		sampleGuardian = "=!:did:sov:8uQhQMGzWxR8vw5P3UWH1j";

		sampleServices = "$xdi=https://xdi.example.com/123 #openid=https://openid.example.com/456";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", XDIWriterRegistry.getDefault().getFormat());
		request.setAttribute("writeImplied", null);
		request.setAttribute("writeOrdered", "on");
		request.setAttribute("writePretty", null);
		request.setAttribute("input", sampleInputs.get(0));
		request.setAttribute("type", sampleType);
		request.setAttribute("control", sampleControl);
		request.setAttribute("equivalent", sampleEquivalent);
		request.setAttribute("guardian", sampleGuardian);
		request.setAttribute("services", sampleServices);

		if (request.getParameter("input") != null) {

			request.setAttribute("input", request.getParameter("input"));
		}

		if (request.getParameter("type") != null) {

			request.setAttribute("type", request.getParameter("type"));
		}

		if (request.getParameter("control") != null) {

			request.setAttribute("control", request.getParameter("control"));
		}

		if (request.getParameter("equivalent") != null) {

			request.setAttribute("equivalent", request.getParameter("equivalent"));
		}

		if (request.getParameter("guardian") != null) {

			request.setAttribute("guardian", request.getParameter("guardian"));
		}

		if (request.getParameter("services") != null) {

			request.setAttribute("services", request.getParameter("services"));
		}

		request.getRequestDispatcher("/XDIRegistrar.jsp").forward(request, response);
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
		String type = request.getParameter("type");
		String control = request.getParameter("control");
		String equivalent = request.getParameter("equivalent");
		String guardian = request.getParameter("guardian");
		String services = request.getParameter("services");
		String output = "";
		String outputId = "";
		String stats = "-1";
		String error = null;

		Properties xdiResultWriterParameters = new Properties();

		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "on".equals(writeImplied) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "on".equals(writeOrdered) ? "1" : "0");
		xdiResultWriterParameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "on".equals(writePretty) ? "1" : "0");

		XDIWriter xdiResultWriter = XDIWriterRegistry.forFormat(resultFormat, xdiResultWriterParameters);

		long start = System.currentTimeMillis();

		try {

			// create DID

			DID did = DID.create(input);

			// create DDO info

			XDIAddress typeXDIAddress = type != null ? XDIAddress.create(type) : null;
			String[] controlStrings = control.contains(" ") ? control.split(" ") : new String[0];
			String[] equivalentStrings = equivalent.contains(" ") ? equivalent.split(" ") : new String[0];

			DID guardianDID = guardian != null ? DID.create(guardian) : null;

			String[] servicesStrings = services.contains(" ") ? services.split(" ") : new String[0];
			Map<String, String> servicesMap = new HashMap<String, String> ();

			for (String servicesString : servicesStrings) {

				if (! servicesString.contains("=")) continue;
				servicesMap.put(servicesString.split("=")[0], servicesString.split("=")[1]);
			}

			// create DDO

			DDO ddo = DDO.create(did);
			if (typeXDIAddress != null) ddo.setType(typeXDIAddress);
			for (String controlString : controlStrings) ddo.addControl(XDIAddress.create(controlString));
			for (String equivalentString : equivalentStrings) ddo.addEquivalentDID(DID.create(equivalentString));
			if (guardianDID != null) ddo.setGuardian(guardianDID);
			for (Map.Entry<String, String> serviceEntry : servicesMap.entrySet()) ddo.addService(XDIAddress.create("<" + serviceEntry.getKey() + ">"), URI.create(serviceEntry.getValue()));

			// output DID and DDO

			StringWriter writer = new StringWriter();

			writer.write("DID: " + did.getXDIAddress() + "\n");
			writer.write("\n");

			writer.write("DDO:\n\n");
			xdiResultWriter.write(ddo.getContextNode().getGraph(), writer);

			// output result

			output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());

			outputId = UUID.randomUUID().toString();
			OutputCache.put(outputId, ddo.getContextNode().getGraph());
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("resultFormat", resultFormat);
		request.setAttribute("writeImplied", writeImplied);
		request.setAttribute("writeOrdered", writeOrdered);
		request.setAttribute("writePretty", writePretty);
		request.setAttribute("input", input);
		request.setAttribute("type", type);
		request.setAttribute("control", control);
		request.setAttribute("equivalent", equivalent);
		request.setAttribute("guardian", guardian);
		request.setAttribute("services", services);
		request.setAttribute("output", output);
		request.setAttribute("outputId", outputId);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIRegistrar.jsp").forward(request, response);
	}
}
