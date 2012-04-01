package xdi2.core.io;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;

/**
 * A reader that will try all known other readers to parse data.
 * Note: This is an expensive operation since everything from the stream is read
 * and cached as opposed to parsing directly from the stream.
 * 
 * @author markus
 */
public class AutoReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1539206988520551698L;

	public static final String FORMAT_NAME = "AUTO";
	public static final String MIME_TYPE = null;
	public static final String DEFAULT_FILE_EXTENSION = null;

	private static String readerClassNames[] = {

		XDIJSONReader.class.getName(),
		XDIStatementsReader.class.getName()
	};

	private static List<XDIReader> readers;

	static {

		readers = new ArrayList<XDIReader> ();

		for (String readerClassName : readerClassNames) {

			try {

				Class<XDIReader> readerClass = forName(readerClassName);
				if (readerClass.equals(AutoReader.class)) continue;
				readers.add(readerClass.newInstance());
			} catch (Throwable ex) {

				throw new RuntimeException(ex);
			}
		}

		if (readers.isEmpty()) throw new RuntimeException("No XDI Readers could be registered.");
	}

	@SuppressWarnings("unchecked")
	private static Class<XDIReader> forName(String readerClassName) throws ClassNotFoundException {

		return (Class<XDIReader>) Class.forName(readerClassName);
	}

	private XDIReader lastSuccessfulReader;

	AutoReader() { }

	@Override
	public synchronized void read(Graph graph, String string, Properties parameters) throws IOException, Xdi2ParseException {

		for (XDIReader xdiReader : readers) {

			if (xdiReader instanceof AutoReader) continue;

			try {

				xdiReader.read(graph, new StringReader(string), parameters);
				this.lastSuccessfulReader = xdiReader;
				return;
			} catch(Exception ex) {

				continue;
			}
		}

		this.lastSuccessfulReader = null;

		throw new Xdi2ParseException("Unknown serialization format.");
	}

	public synchronized Reader read(Graph graph, Reader reader, Properties parameters) throws IOException, Xdi2ParseException {

		StringWriter stringWriter = new StringWriter();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;

		while ((line = bufferedReader.readLine()) != null) stringWriter.write(line + "\n");

		this.read(graph, stringWriter.getBuffer().toString(), parameters);
		
		return reader;
	}

	@Override
	public synchronized InputStream read(Graph graph, InputStream stream, Properties parameters) throws IOException, Xdi2ParseException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
		int b;

		while ((b = bufferedInputStream.read()) != -1) byteArrayOutputStream.write(b);

		this.read(graph, new String(byteArrayOutputStream.toByteArray()), parameters);
		
		return stream;
	}

	public XDIReader getLastSuccessfulReader() {

		return this.lastSuccessfulReader;
	}

	public String getLastSuccessfulFormat() {

		if (this.lastSuccessfulReader == null) return null;

		return this.lastSuccessfulReader.getFormat();
	}
}
