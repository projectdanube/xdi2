package xdi2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.exceptions.Xdi2ParseException;

class XDIStatementsReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	protected static final String FORMAT_TYPE = "STATEMENTS";
	protected static final String[] MIME_TYPES = new String[] { "text/plain" };
	protected static final String DEFAULT_FILE_EXTENSION = "xdi";

	XDIStatementsReader() { }

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException {

		String statement;
		int lineNr = 0;

		while ((statement = bufferedReader.readLine()) != null) {

			lineNr++;

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

	public String getFormat() {

		return FORMAT_TYPE;
	}

	public String[] getMimeTypes() {

		return MIME_TYPES;
	}

	public String getDefaultFileExtension() {

		return DEFAULT_FILE_EXTENSION;
	}
}
