package xdi2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.exceptions.Xdi2ParseException;

public class XDIStatementsReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	public static final String FORMAT_NAME = "STATEMENTS";
	public static final String MIME_TYPE = "text/plain";
	public static final String DEFAULT_FILE_EXTENSION = "xdi";

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
