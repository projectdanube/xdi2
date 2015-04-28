package xdi2.core.io.writers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.MimeType;

public class XDIDisplaySTARShiftWriter extends XDIDisplayWriter {

	private static final long serialVersionUID = -2311824958666389216L;

	public static final String FORMAT_NAME = "XDI DISPLAY STARSHIFT";
	public static final String FILE_EXTENSION = "xdistarshift";
	public static final MimeType MIME_TYPE = new MimeType("text/xdistarshift");

	public XDIDisplaySTARShiftWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		super.init();

		// check parameters

		if (this.isWriteImplied()) throw new Xdi2RuntimeException("implied=1 not support with this format");
		if (this.isWritePretty()) throw new Xdi2RuntimeException("pretty=1 not support with this format");
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		StringWriter string = new StringWriter();
		super.write(graph, string);

		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		BufferedReader bufferedReader = new BufferedReader(new StringReader(string.getBuffer().toString()));
		String line;

		while ((line = bufferedReader.readLine()) != null) {

			line = line.replace("!", "*!");
			line = line.replace("[=]*!", "=!");
			line = line.replace("[+]*!", "+!");

			bufferedWriter.write(line + "\n");
		}

		bufferedWriter.flush();
		writer.flush();

		return writer;
	}
}
