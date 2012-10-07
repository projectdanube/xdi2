package xdi2.core.impl.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractGraph;
import xdi2.core.impl.memory.MemoryContextNode;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIWriter;

public class FileGraph extends AbstractGraph implements Graph {

	private static final long serialVersionUID = 8979035878235290607L;

	private static final Logger log = LoggerFactory.getLogger(FileGraph.class);

	private String path;
	private String mimeType;
	private XDIReader xdiReader;
	private XDIWriter xdiWriter;
	private MemoryGraph memoryGraph;

	FileGraph(FileGraphFactory graphFactory, String path, String mimeType, XDIReader xdiReader, XDIWriter xdiWriter, MemoryGraph memoryGraph) {

		super(graphFactory);
		
		this.path = path;
		this.mimeType = mimeType;
		this.xdiReader = xdiReader;
		this.xdiWriter = xdiWriter;
		this.memoryGraph = memoryGraph;

		this.load();
	}

	@Override
	public ContextNode getRootContextNode() {

		MemoryContextNode memoryContextNode = (MemoryContextNode) this.memoryGraph.getRootContextNode();

		return new FileContextNode(this, null, memoryContextNode);
	}

	@Override
	public void close() {

		this.save();
	}

	@Override
	public boolean supportsTransactions() {

		return false;
	}

	@Override
	public void beginTransaction() {

	}

	@Override
	public void commitTransaction() {

		this.save();
	}

	@Override
	public void rollbackTransaction() {

	}

	public String getPath() {

		return this.path;
	}

	public String getMimeType() {

		return this.mimeType;
	}

	private void load() {

		this.memoryGraph.clear();

		try {

			File file = new File(this.path);
			if (! file.exists()) {

				if (log.isDebugEnabled()) log.debug("File " + file.getAbsolutePath() + " does not exist. Not loading.");
				return;
			}

			if (log.isDebugEnabled()) log.debug("Loading file " + file.getAbsolutePath());

			Reader reader = new FileReader(file);

			this.xdiReader.read(this.memoryGraph, reader);
			reader.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot load file at " + this.path, ex);
		}
	}

	private void save() {

		try {

			File file = new File(this.path);
			if (! file.exists()) {

				if (log.isDebugEnabled()) log.debug("File " + file.getAbsolutePath() + " does not exist. Creating file.");
				file.createNewFile();
			}

			if (log.isDebugEnabled()) log.debug("Saving file " + file.getAbsolutePath());

			Writer writer = new FileWriter(this.path);

			this.xdiWriter.write(this.memoryGraph, writer);
			writer.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot save file at " + this.path, ex);
		}
	}
}
