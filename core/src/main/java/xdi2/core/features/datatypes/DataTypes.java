package xdi2.core.features.datatypes;

import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeAddressIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() { }

	public static final XDIAddress XRI_DATATYPE_XSD = XDIAddress.create("" + XDIConstants.CS_CLASS_UNRESERVED + XDIConstants.CS_CLASS_RESERVED + "xsd");
	public static final XDIAddress XRI_DATATYPE_JSON = XDIAddress.create("" + XDIConstants.CS_CLASS_UNRESERVED + XDIConstants.CS_CLASS_RESERVED + "json");
	public static final XDIAddress XRI_DATATYPE_MIME = XDIAddress.create("" + XDIConstants.CS_CLASS_UNRESERVED + XDIConstants.CS_CLASS_RESERVED + "mime");

	/*
	 * Methods for booleans
	 */

	public static XDIAddress booleanToAddress(Boolean b) {

		if (Boolean.TRUE.equals(b)) return XDIConstants.XDI_ADD_TRUE;
		if (Boolean.FALSE.equals(b)) return XDIConstants.XDI_ADD_FALSE;

		return null;
	}

	public static Boolean xriToBoolean(XDIAddress xri) {

		if (XDIConstants.XDI_ADD_TRUE.equals(xri)) return Boolean.TRUE;
		if (XDIConstants.XDI_ADD_FALSE.equals(xri)) return Boolean.FALSE;

		return null;
	}

	/*
	 * Methods for data types of context nodes
	 */

	/**
	 * Set a $is+ datatype associated with a context node
	 * 
	 * @param contextNode
	 * @param dataTypeAddress
	 */
	public static void setDataType(ContextNode contextNode, XDIAddress dataTypeAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, dataTypeAddress);
	}

	/**
	 * Get all $is+ datatypes associated with a context node
	 * 
	 * @param contextNode
	 * @return list of datatypes
	 */
	public static List<XDIAddress> getDataTypes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return new IteratorListMaker<XDIAddress> (new MappingRelationTargetContextNodeAddressIterator(relations)).list();
	}

	/**
	 * Get a $is+ datatype associated with a context node
	 * 
	 * @param contextNode
	 * @return datatype
	 */
	public static XDIAddress getDataType(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return relation == null ? null : relation.getTargetContextNodeAddress();
	}

	/*
	 * Methods for data type XRIs
	 */

	/**
	 * Returns XDIAddress for a xsd datatype string.
	 * 
	 * @param xsdType
	 * @return a xsd datatype XDIAddress
	 */
	public static XDIAddress dataTypeAddressFromXsdType(String xsdType) {

		return XDIAddress.create("" + XRI_DATATYPE_XSD + XDIConstants.CS_CLASS_RESERVED + xsdType);
	}

	/**
	 * Returns datatype in xsd format for a given XRI segment.
	 * 
	 * @param dataTypeAddress
	 * @return a string of xsd datatype
	 */
	public static String xsdTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		String dataTypeAddressString = dataTypeAddress.toString();

		return getDataTypeFromXRISegment(dataTypeAddressString, true);
	}

	/**
	 * Returns XDIAddress for a json datatype string.
	 * 
	 * @param jsonType
	 * @return a json datatype XDIAddress
	 */
	public static XDIAddress dataTypeAddressFromJsonType(String jsonType) {

		return XDIAddress.create("" + XRI_DATATYPE_JSON + XDIConstants.CS_CLASS_RESERVED + jsonType);
	}

	/**
	 * Returns datatype in json format for a given XRI segment.
	 * 
	 * @param dataTypeAddress
	 * @return a string of json datatype
	 */
	public static String jsonTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		String dataTypeJSONString = dataTypeAddress.toString();

		return getDataTypeFromXRISegment(dataTypeJSONString, false);
	}

	/**
	 * Returns XDIAddress for a mime datatype string.
	 * 
	 * @param mimeType
	 * @return a mime datatype XDIAddress.
	 */
	public static XDIAddress dataTypeAddressFromMimeType(String mimeType) {

		String[] parts;
		XDIAddress xri = null;

		try {

			parts = mimeType.split("/");
			xri = XDIAddress.create("" + XRI_DATATYPE_MIME + XDIConstants.CS_CLASS_RESERVED + parts[0] + XDIConstants.CS_CLASS_UNRESERVED + parts[1]);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Invalid MIME Type ", ex);
		}

		return xri;
	}

	/**
	 * Returns datatype in mime format for a given XRI segment.
	 * 
	 * @param dataTypeAddress
	 * @return a string of mime datatype
	 */
	public static String mimeTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		String dataTypeMIMEString = dataTypeAddress.toString();

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

			throw new Xdi2RuntimeException("Invalid XDIAddress ", ex);
		}

		return buffer.toString();
	}
}
