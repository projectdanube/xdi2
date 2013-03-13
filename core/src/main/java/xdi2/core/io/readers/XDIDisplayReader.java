package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.XDI3Statement;

public class XDIDisplayReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIDisplayReader.class);

	public static final String FORMAT_NAME = "XDI DISPLAY";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType MIME_TYPE = new MimeType("text/xdi");

	public XDIDisplayReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException {

		String line;
		int lineNr = 0;

		while ((line = bufferedReader.readLine()) != null) {

			lineNr++;

			if (line.trim().isEmpty()) continue;

			XDI3Statement statementXri;

			try {

				statementXri = XDI3Statement.create(line);
			} catch (Exception ex) {

				throw new Xdi2ParseException("XRI parser problem at line " + lineNr + ": " + ex.getMessage(), ex);
			}

			// ignore implied statements

			Statement statement = graph.findStatement(statementXri);

			if (statement != null && statement.isImplied()) {

				if (log.isTraceEnabled()) log.trace("Skipping implied statement: " + statementXri);

				continue;
			}

			// add the statement to the graph

			graph.createStatement(statementXri);
		}
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		read(graph, new BufferedReader(reader));

		return reader;
	}
}
