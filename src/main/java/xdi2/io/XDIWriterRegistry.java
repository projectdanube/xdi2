/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides an appropriate XDIWriter for a given type.
 *
 * @author msabadello at parityinc dot net
 */
public final class XDIWriterRegistry {

	private static final Log log = LogFactory.getLog(XDIWriterRegistry.class);

	private static String writerClassNames[] = {

		"org.eclipse.higgins.xdi4j.io.X3StandardWriter",  // first one in the array is the default
		"org.eclipse.higgins.xdi4j.io.X3SimpleWriter",
		"org.eclipse.higgins.xdi4j.io.X3WhitespaceWriter",
		"org.eclipse.higgins.xdi4j.io.XDIXMLWriter",
		"org.eclipse.higgins.xdi4j.io.XTRIPLESWriter",
		"org.eclipse.higgins.xdi4j.io.X3JWriter",
		"org.eclipse.higgins.xdi4j.io.XDIJSONWriter",
		"org.eclipse.higgins.xdi4j.io.XRIWriter"
	};

	private static List<Class<XDIWriter> > writerClasses;

	private static Class<XDIWriter> defaultWriterClass;

	private static Map<String, Class<XDIWriter> > writerClassesByFormat;
	private static Map<String, Class<XDIWriter> > writerClassesByMimeType;
	private static Map<String, Class<XDIWriter> > writerClassesByDefaultFileExtension;

	static {

		writerClasses = new ArrayList<Class<XDIWriter> > ();
		writerClassesByFormat = new HashMap<String, Class<XDIWriter> >();
		writerClassesByMimeType = new HashMap<String, Class<XDIWriter> >();
		writerClassesByDefaultFileExtension = new HashMap<String, Class<XDIWriter> >();

		for (String writerClassName : writerClassNames) {

			try {

				Class<XDIWriter> writerClass = forName(writerClassName);
				writerClasses.add(writerClass);
			} catch (Throwable ex) {

				log.warn("Cannot instantiate XDI Writer " + writerClassName + ": " + ex.getMessage());
				continue;
			}
		}

		if (writerClasses.isEmpty()) throw new RuntimeException("No XDI Writers could be registered.");

		for (Class<XDIWriter> writerClass : writerClasses) {

			XDIWriter writer;

			try {

				writer = writerClass.newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}

			if (writer.getFormat() != null) writerClassesByFormat.put(writer.getFormat(), writerClass);
			for (String mimeType : writer.getMimeTypes()) writerClassesByMimeType.put(mimeType, writerClass);
			if (writer.getDefaultFileExtension() != null) writerClassesByDefaultFileExtension.put(writer.getDefaultFileExtension(), writerClass);
		}

		defaultWriterClass = writerClasses.get(0);
	}

	@SuppressWarnings("unchecked")
	private static Class<XDIWriter> forName(String writerClassName) throws ClassNotFoundException {

		return (Class<XDIWriter>) Class.forName(writerClassName);
	}

	private XDIWriterRegistry() { }

	/**
	 * Returns an XDIWriter for the specified format, e.g.
	 * <ul>
	 * <li>X3</li>
	 * <li>X-TRIPLES</li>
	 * <li>XDI/XML</li>
	 * </ul>
	 * @param format The desired format.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFormat(String format) {

		Class<XDIWriter> writerClass = writerClassesByFormat.get(format);
		if (writerClass == null) return null;

		try {

			return(writerClass.newInstance());
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified mime type, e.g.
	 * <ul>
	 * <li>text/xdi+x3</li>
	 * <li>text/plain</li>
	 * <li>application/xdi+xml</li>
	 * </ul>
	 * @param mimeType The desired mime type.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forMimeType(String mimeType) {

		Class<XDIWriter> writerClass = writerClassesByMimeType.get(mimeType);
		if (writerClass == null) return null;

		try {

			return(writerClass.newInstance());
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified file extension, e.g.
	 * <ul>
	 * <li>.xml</li>
	 * <li>.x3</li>
	 * <li>.txt</li>
	 * </ul>
	 * @param fileExtension The desired file extension.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFileExtension(String fileExtension) {

		Class<XDIWriter> writerClass = writerClassesByDefaultFileExtension.get(fileExtension);
		if (writerClass == null) return null;

		try {

			return(writerClass.newInstance());
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a list of all available XDIWriters.
	 * @return All XDIWriters this factory knows of.
	 */
	public static XDIWriter[] getWriters() {

		XDIWriter[] writers = new XDIWriter[writerClasses.size()];

		for (int i=0; i<writerClasses.size(); i++) {

			try {

				writers[i] = writerClasses.get(i).newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}
		}

		return(writers);
	}

	/**
	 * Returns an XDIWriter for the default format.
	 * @return An XDIWriter.
	 */
	public static XDIWriter getDefault() {

		Class<XDIWriter> writerClass = defaultWriterClass;

		try {

			return(writerClass.newInstance());
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns all formats for which XDIWriter implementations exist.
	 * @return A string array of formats.
	 */
	public static String[] getFormats() {

		return(writerClassesByFormat.keySet().toArray(new String[writerClassesByFormat.size()]));
	}

	/**
	 * Returns all mime types for which XDIWriter implementations exist.
	 * @return A string array of mime types.
	 */
	public static String[] getMimeTypes() {

		return(writerClassesByMimeType.keySet().toArray(new String[writerClassesByMimeType.size()]));
	}
}
