package xdi2.core.impl.file;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;

/**
 * GraphFactory that creates file-based graphs.
 * 
 * @author markus
 */
public class FileGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_PATH = "xdi2-graph.properties";
	public static final String DEFAULT_MIMETYPE = null;

	private String path;
	private String mimeType;
	private MemoryGraphFactory memoryGraphFactory;

	public FileGraphFactory() { 

		super();

		this.path = DEFAULT_PATH;
		this.mimeType = DEFAULT_MIMETYPE;
		this.memoryGraphFactory = MemoryGraphFactory.getInstance();
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		// check identifier

		if (identifier != null) {

			this.setPath("xdi2-graph." + identifier + ".xdi");
		}

		// initialize graph

		XDIReader xdiReader = XDIReaderRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));
		XDIWriter xdiWriter = XDIWriterRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));

		MemoryGraph memoryGraph = this.memoryGraphFactory.openGraph();

		return new FileGraph(this.path, xdiReader, xdiWriter, memoryGraph);
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

	public MemoryGraphFactory getMemoryGraphFactory() {

		return this.memoryGraphFactory;
	}

	public void setMemoryGraphFactory(MemoryGraphFactory memoryGraphFactory) {

		this.memoryGraphFactory = memoryGraphFactory;
	}
}
