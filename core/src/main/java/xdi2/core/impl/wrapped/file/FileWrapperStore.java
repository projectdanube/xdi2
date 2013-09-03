package xdi2.core.impl.wrapped.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.impl.wrapped.WrapperStore;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIWriter;

public class FileWrapperStore implements WrapperStore {

	private static final Logger log = LoggerFactory.getLogger(FileWrapperStore.class);

	private String path;
	private String mimeType;
	private XDIReader xdiReader;
	private XDIWriter xdiWriter;

	public FileWrapperStore(String path, String mimeType, XDIReader xdiReader, XDIWriter xdiWriter) {

		this.path = path;
		this.mimeType = mimeType;
		this.xdiReader = xdiReader;
		this.xdiWriter = xdiWriter;
	}

	@Override
	public void load(MemoryGraph memoryGraph) {

		memoryGraph.clear();

		try {

			File file = new File(this.path);
			if (! file.exists()) {

				if (log.isDebugEnabled()) log.debug("File " + file.getAbsolutePath() + " does not exist. Not loading.");
				return;
			}

			if (log.isDebugEnabled()) log.debug("Loading file " + file.getAbsolutePath());

			Reader reader = new FileReader(file);

			this.xdiReader.read(memoryGraph, reader);
			reader.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot load file at " + this.path, ex);
		}
	}

	@Override
	public void save(MemoryGraph memoryGraph) {

		try {

			File file = new File(this.path);
			if (! file.exists()) {

				if (log.isDebugEnabled()) log.debug("File " + file.getAbsolutePath() + " does not exist. Creating file.");
				file.createNewFile();
			}

			if (log.isDebugEnabled()) log.debug("Saving file " + file.getAbsolutePath());

			Writer writer = new FileWriter(this.path);

			this.xdiWriter.write(memoryGraph, writer);
			writer.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot save file at " + this.path, ex);
		}
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

	public XDIReader getXdiReader() {

		return this.xdiReader;
	}

	public void setXdiReader(XDIReader xdiReader) {

		this.xdiReader = xdiReader;
	}

	public XDIWriter getXdiWriter() {

		return this.xdiWriter;
	}

	public void setXdiWriter(XDIWriter xdiWriter) {

		this.xdiWriter = xdiWriter;
	}

	/*
	 * Helper methods
	 */

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith("xdi2-graph.") && name.endsWith(".xdi");
			}
		});

		for (File file : files) file.delete();
	}
}
