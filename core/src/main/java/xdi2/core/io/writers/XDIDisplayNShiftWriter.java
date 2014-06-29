package xdi2.core.io.writers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public class XDIDisplayNShiftWriter extends XDIDisplayWriter {

	private static final long serialVersionUID = -8788469386785079588L;

	public static final String FORMAT_NAME = "XDI DISPLAY NSHIFT";
	public static final String FILE_EXTENSION = "xdinshift";
	public static final MimeType MIME_TYPE = new MimeType("text/xdinshift");

	public XDIDisplayNShiftWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		this.parameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "0");

		super.init();
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		StringWriter string = new StringWriter();
		super.write(graph, string);

		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		BufferedReader bufferedReader = new BufferedReader(new StringReader(string.getBuffer().toString()));
		String line;
		XDI3Statement statement;

		while ((line = bufferedReader.readLine()) != null) {

			statement = XDI3Statement.create(line);

			XDI3Segment subject = statement.getSubject();
			XDI3Segment predicate = statement.getPredicate();
			Object object = statement.getObject();

			if (object instanceof XDI3Segment) {

				while (((XDI3Segment) object).getNumSubSegments() > 0 && 
						((XDI3Segment) object).getFirstSubSegment().hasXRef() && 
						((XDI3Segment) object).getFirstSubSegment().getXRef().hasPartialSubjectAndPredicate()) {

					object = XDI3Util.localXri((XDI3Segment) object, -1);
					if (object == null) object = XDIConstants.XRI_S_ROOT;
				}

				bufferedWriter.write(subject + "/" + predicate + "/" + object + "\n");
			} else {

				bufferedWriter.write(line + "\n");
			}
		}

		bufferedWriter.flush();
		writer.flush();

		return writer;
	}
}
