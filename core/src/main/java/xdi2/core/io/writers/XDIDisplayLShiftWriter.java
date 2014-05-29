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
import xdi2.core.io.MimeType;

public class XDIDisplayLShiftWriter extends XDIDisplayWriter {

	private static final long serialVersionUID = 4377123541696335486L;

	public static final String FORMAT_NAME = "XDI DISPLAY LSHIFT";
	public static final String FILE_EXTENSION = "xdilshift";
	public static final MimeType MIME_TYPE = new MimeType("text/xdilshift");

	public XDIDisplayLShiftWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		StringWriter string = new StringWriter();
		super.write(graph, string);

		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		BufferedReader bufferedReader = new BufferedReader(new StringReader(string.getBuffer().toString()));
		String line;

		Pattern pattern = Pattern.compile("^([^(]+)\\$to([^(]+)\\$from(.+)$");

		while ((line = bufferedReader.readLine()) != null) {

			Matcher matcher = pattern.matcher(line);

			if (matcher.matches()) {

				String group1 = matcher.group(1);
				String group2 = matcher.group(2);
				String group3 = matcher.group(3);

				if (group2.equals("$anon")) group2 = "$public";
				if (group3.startsWith("$public")) group3 = group3.substring(7);

				StringBuffer buffer = new StringBuffer();
				buffer.append(group1);
				buffer.append("/");
				buffer.append(group2);
				buffer.append("/");
				buffer.append("(");
				buffer.append(group3);
				buffer.append(")");

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
