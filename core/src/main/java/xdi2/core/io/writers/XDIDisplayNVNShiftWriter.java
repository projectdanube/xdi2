package xdi2.core.io.writers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.MimeType;

public class XDIDisplayNVNShiftWriter extends XDIDisplayWriter {

	private static final long serialVersionUID = 1167832158634747377L;

	public static final String FORMAT_NAME = "XDI DISPLAY NVNSHIFT";
	public static final String FILE_EXTENSION = "xdinvnshift";
	public static final MimeType MIME_TYPE = new MimeType("text/xdinvnshift");

	public XDIDisplayNVNShiftWriter(Properties parameters) {

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

		Pattern pattern = Pattern.compile("^(.+)&/&/(.+)");

		while ((line = bufferedReader.readLine()) != null) {

			Matcher matcher = pattern.matcher(line);

			if (matcher.matches()) {

				String group1 = matcher.group(1);
				String group2 = matcher.group(2);

				StringBuffer buffer = new StringBuffer();
				buffer.append(group1);
				buffer.append("/&/");
				buffer.append(group2);

				bufferedWriter.write(buffer.toString() + "\n");
			} else {

				bufferedWriter.write(line + "\n");
			}
		}

		bufferedWriter.flush();
		writer.flush();

		return writer;
	}
}
