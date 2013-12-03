package xdi2.messaging.target.impl.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.readers.AutoReader;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * An XDI messaging target backed by a file in one of the serialization formats.
 * 
 * @author markus
 */
public class FileMessagingTarget extends GraphMessagingTarget {

	public static final String DEFAULT_PATH = "xdi2-graph.xdi";
	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private String path;
	private String mimeType;

	public FileMessagingTarget() {

		super();

		this.path = DEFAULT_PATH;
		this.mimeType = DEFAULT_MIMETYPE;
	}

	@Override
	public void init() throws Exception {

		super.init();

		this.setGraph(graphFactory.openGraph());
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	@Override
	public void execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.readGraph();

		super.execute(messageEnvelope, messageResult, executionContext);

		this.writeGraph();
	}

	private void readGraph() throws Xdi2MessagingException {

		XDIReader xdiReader = XDIReaderRegistry.forFormat(this.mimeType, null);
		if (xdiReader == null) throw new Xdi2MessagingException("Cannot read this format: " + this.mimeType, null, null);

		Graph graph = this.getGraph();
		graph.clear();

		FileReader reader = null;

		try {

			File file = new File(this.path);
			reader = new FileReader(file);
			xdiReader.read(graph, reader);
			reader.close();
		} catch (FileNotFoundException ex) {

		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot read file: " + ex.getMessage(), ex, null);
		} finally {

			if (reader != null) {

				try {

					reader.close();
				} catch (Exception ex) { }
			}
		}

		if (xdiReader instanceof AutoReader) this.mimeType = ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat();
		if (this.mimeType == null) this.mimeType = XDIWriterRegistry.getDefault().getFormat();
	}

	private void writeGraph() throws Xdi2MessagingException {

		XDIWriter xdiWriter = XDIWriterRegistry.forFormat(this.mimeType, null);
		if (xdiWriter == null) throw new Xdi2MessagingException("Cannot write this format: " + this.mimeType, null, null);

		Graph graph = this.getGraph();
		
		FileWriter writer = null;

		try {

			File file = new File(this.path);
			file.createNewFile();
			writer = new FileWriter(file);
			xdiWriter.write(graph, writer);
			writer.close();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot write file: " + ex.getMessage(), ex, null);
		} finally {

			try {

				if (writer != null) writer.close();
			} catch (Exception ex) { }
		}

		graph.close();
	}

	public String getPath() {

		return this.path;
	}

	public void setPath(String path) {

		this.path = path;
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public void setMimeType(String mimeType) {

		this.mimeType = mimeType;
	}
}
