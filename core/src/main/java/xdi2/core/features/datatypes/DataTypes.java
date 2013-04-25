package xdi2.core.features.datatypes;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
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
	 * Returns datatype of literal in xsd format for a given XRI segment.
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
	 * Returns datatype of literal in json format for a given XRI segment.
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
	 * Returns datatype of literal in mime format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of mime datatype
	 */
	public static String mimeTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeMIMEString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeMIMEString, false);
	}

	/*
	 * Methods for data types of literals
	 */

	/**
	 * Create a "$is" relation for a literal and datatype XRI segment
	 * 
	 * @param literal
	 * @param dataTypeXri
	 */
	public static void setLiteralDataType(Literal literal, XDI3Segment dataTypeXri) {

		ContextNode contextNode = literal.getContextNode().getContextNode();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, dataTypeXri);
	}

	/**
	 * Get all datatypes associated with a literal
	 * 
	 * @param literal
	 * @return list of datatypes as XDI3Segment
	 */
	public static List<XDI3Segment> getLiteralDataType(Literal literal) {

		return getLiteralDatatypes(literal);
	}

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

	private static List<XDI3Segment> getLiteralDatatypes(Literal literal) {

		List<XDI3Segment> dataTypes;

		try {

			ReadOnlyIterator<Relation> relations = literal.getContextNode().getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);

			dataTypes = new ArrayList<XDI3Segment>();

			while (relations.hasNext()) {

				Relation relation = relations.next();
				dataTypes.add((XDI3Segment) relation.getStatement().getObject());
			}
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Invalid XDI3Segment ", ex);
		}

		return dataTypes;
	}
}
