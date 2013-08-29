package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.keyvalue.KeyValueGraph;
import xdi2.core.impl.keyvalue.map.MapFactory;
import xdi2.core.impl.keyvalue.map.MapKeyValueGraphFactory;
import xdi2.core.impl.keyvalue.map.MapKeyValueStore;
import xdi2.core.impl.keyvalue.map.SetFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;

public class XDIKeyValueWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = 4377123541696335486L;

	private static final Logger log = LoggerFactory.getLogger(XDIKeyValueWriter.class);

	public static final String FORMAT_NAME = "KEYVALUE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private boolean writeOrdered;

	public XDIKeyValueWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));

		if (log.isTraceEnabled()) log.trace("Parameters: writeOrdered=" + this.writeOrdered);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		MapKeyValueGraphFactory mapGraphFactory = new MapKeyValueGraphFactory();

		if (this.writeOrdered) {

			mapGraphFactory.setMapFactory(new MapFactory() {

				@Override
				public Map<String, Set<String>> newMap() {

					return new TreeMap<String, Set<String>> ();
				}
			});

			mapGraphFactory.setSetFactory(new SetFactory() {

				@Override
				public Set<String> newSet(String key) {

					return new TreeSet<String> ();
				}
			});
		}

		KeyValueGraph mapGraph = (KeyValueGraph) mapGraphFactory.openGraph();
		MapKeyValueStore mapKeyValueStore = (MapKeyValueStore) mapGraph.getKeyValueStore();
		Map<String, Set<String>> map = mapKeyValueStore.getMap();

		CopyUtil.copyGraph(graph, mapGraph, null);

		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {

			String key = entry.getKey();

			for (String value : entry.getValue()) {

				writer.write(key + " ---> " + value + "\n");
			}
		}

		writer.flush();

		return writer;
	}
}
