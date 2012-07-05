package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;

public class XDIStatementsReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	public static final String FORMAT_NAME = "STATEMENTS";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType[] MIME_TYPES = new MimeType[] { new MimeType("text/xdi"), new MimeType("text/xdi;contexts=0"), new MimeType("text/xdi;contexts=1") };

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException {

		String statement;
		int lineNr = 0;

		while ((statement = bufferedReader.readLine()) != null) {

			lineNr++;

			if (statement.trim().isEmpty()) continue;

			try {

				graph.addStatement(statement);
			} catch (Xdi2Exception ex) {

				throw new Xdi2ParseException("Problem at line " + lineNr + ": " + ex.getMessage(), ex);
			}
		}
	}

	public Reader read(Graph graph, Reader reader, Properties parameters) throws IOException, Xdi2ParseException {

		this.read(graph, new BufferedReader(reader));

		return reader;
	}
}
