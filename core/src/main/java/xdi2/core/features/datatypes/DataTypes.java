package xdi2.core.features.datatypes;

import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeXriIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() { }

	public static final XDI3Segment XRI_DATATYPE_XSD = XDI3Segment.create("" + XDIConstants.CS_PLUS + XDIConstants.CS_DOLLAR + "xsd");
	public static final XDI3Segment XRI_DATATYPE_JSON = XDI3Segment.create("" + XDIConstants.CS_PLUS + XDIConstants.CS_DOLLAR + "json");
	public static final XDI3Segment XRI_DATATYPE_MIME = XDI3Segment.create("" + XDIConstants.CS_PLUS + XDIConstants.CS_DOLLAR + "mime");

	/*
	 * Methods for data types of context nodes
	 */

	/**
	 * Set a $is+ datatype associated with a context node
	 * 
	 * @param contextNode
	 * @param dataTypeXri
	 */
	public static void setDataType(ContextNode contextNode, XDI3Segment dataTypeXri) {

		contextNode.setRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, dataTypeXri);
	}

	/**
	 * Get all $is+ datatypes associated with a context node
	 * 
	 * @param contextNode
	 * @return list of datatypes
	 */
	public static List<XDI3Segment> getDataTypes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);

		return new IteratorListMaker<XDI3Segment> (new MappingRelationTargetContextNodeXriIterator(relations)).list();
	}

	/**
	 * Get a $is+ datatype associated with a context node
	 * 
	 * @param contextNode
	 * @return datatype
	 */
	public static XDI3Segment getDataType(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS_TYPE);

		return relation == null ? null : relation.getTargetContextNodeXri();
	}

	/*
	 * Methods for data type XRIs
	 */

	/**
	 * Returns XDI3Segment for a xsd datatype string.
	 * 
	 * @param xsdType
	 * @return a xsd datatype XDI3Segment
	 */
	public static XDI3Segment dataTypeXriFromXsdType(String xsdType) {

		return XDI3Segment.create("" + XRI_DATATYPE_XSD + XDIConstants.CS_DOLLAR + xsdType);
	}

	/**
	 * Returns datatype in xsd format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of xsd datatype
	 */
	public static String xsdTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeXriString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeXriString, true);
	}

	/**
	 * Returns XDI3Segment for a json datatype string.
	 * 
	 * @param jsonType
	 * @return a json datatype XDI3Segment
	 */
	public static XDI3Segment dataTypeXriFromJsonType(String jsonType) {

		return XDI3Segment.create("" + XRI_DATATYPE_JSON + XDIConstants.CS_DOLLAR + jsonType);
	}

	/**
	 * Returns datatype in json format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of json datatype
	 */
	public static String jsonTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeJSONString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeJSONString, false);
	}

	/**
	 * Returns XDI3Segment for a mime datatype string.
	 * 
	 * @param mimeType
	 * @return a mime datatype XDI3Segment.
	 */
	public static XDI3Segment dataTypeXriFromMimeType(String mimeType) {

		String[] parts;
		XDI3Segment xri = null;

		try {

			parts = mimeType.split("/");
			xri = XDI3Segment.create("" + XRI_DATATYPE_MIME + XDIConstants.CS_DOLLAR + parts[0] + XDIConstants.CS_DOLLAR + parts[1]);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Invalid MIME Type ", ex);
		}

		return xri;
	}

	/**
	 * Returns datatype in mime format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of mime datatype
	 */
	public static String mimeTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeMIMEString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeMIMEString, false);
	}

	/*
	 * Helper methods
	 */

	/**
	 * Generic method to get datatype from a string of xri segment.
	 * 
	 * @param xriSegment
	 * @param isXSDType
	 * @return a string of datatype
	 */
	private static String getDataTypeFromXRISegment(String xriSegment, boolean isXSDType) {

		StringBuilder buffer = new StringBuilder();

		try {

			String[] split = xriSegment.substring(xriSegment.indexOf("$") + 1).split("[$]");

			for (int i = 0; i < split.length; i++) {

				if (!isXSDType && i == 0) continue;

				buffer.append(split[i]);

				if (i != split.length - 1) {

					if (!isXSDType)
						buffer.append("/");
					else
						buffer.append(":");
				}
			}
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Invalid XDI3Segment ", ex);
		}

		return buffer.toString();
	}
}
