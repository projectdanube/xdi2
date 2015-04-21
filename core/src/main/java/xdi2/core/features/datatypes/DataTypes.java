package xdi2.core.features.datatypes;

import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() { }

	public static final XDIAddress XDI_ADD_DATATYPE_XSD = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "xsd");
	public static final XDIAddress XDI_ADD_DATATYPE_JSON = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "json");
	public static final XDIAddress XDI_ADD_DATATYPE_MIME = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "mime");

	/*
	 * Methods for booleans
	 */

	public static XDIAddress booleanToAddress(Boolean b) {

		if (Boolean.TRUE.equals(b)) return XDIConstants.XDI_ADD_TRUE;
		if (Boolean.FALSE.equals(b)) return XDIConstants.XDI_ADD_FALSE;

		return null;
	}

	public static Boolean addressToBoolean(XDIAddress XDIaddress) {

		if (XDIConstants.XDI_ADD_TRUE.equals(XDIaddress)) return Boolean.TRUE;
		if (XDIConstants.XDI_ADD_FALSE.equals(XDIaddress)) return Boolean.FALSE;

		return null;
	}

	/*
	 * Methods for data types of context nodes
	 */

	/**
	 * Set a $is# datatype associated with a context node
	 * 
	 * @param contextNode
	 * @param dataTypeXDIAddress
	 */
	public static void setDataType(ContextNode contextNode, XDIAddress dataTypeXDIAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, dataTypeXDIAddress);
	}

	/**
	 * Get all $is# datatypes associated with a context node
	 * 
	 * @param contextNode
	 * @return list of datatypes
	 */
	public static List<XDIAddress> getDataTypes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return new IteratorListMaker<XDIAddress> (new MappingRelationTargetXDIAddressIterator(relations)).list();
	}

	/**
	 * Get a $is# datatype associated with a context node
	 * 
	 * @param contextNode
	 * @return datatype
	 */
	public static XDIAddress getDataType(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return relation == null ? null : relation.getTargetXDIAddress();
	}

	/*
	 * Methods for data type addresses
	 */

	/**
	 * Returns an XDI address for an XSD data type.
	 */
	public static XDIAddress dataTypeXDIAddressFromXsdType(String xsdType) {

		XDIAddress xsdTypeXDIAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + xsdType.replace(":", XDIConstants.CS_CLASS_RESERVED.toString()));

		return XDIAddressUtil.concatXDIAddresses(XDI_ADD_DATATYPE_XSD, xsdTypeXDIAddress);
	}

	/**
	 * Returns an XDI address for a JSON data type.
	 */
	public static XDIAddress dataTypeXDIAddressFromJsonType(String jsonType) {

		XDIAddress jsonTypeXDIAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + jsonType);

		return XDIAddressUtil.concatXDIAddresses(XDI_ADD_DATATYPE_JSON, jsonTypeXDIAddress);
	}

	/**
	 * Returns an XDI address for a MIME data type.
	 */
	public static XDIAddress dataTypeXDIAddressFromMimeType(String mimeType) {

		XDIAddress mimeTypeXDIAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + mimeType.replace("/", XDIConstants.CS_CLASS_RESERVED.toString()));

		return XDIAddressUtil.concatXDIAddresses(XDI_ADD_DATATYPE_MIME, mimeTypeXDIAddress);
	}

	/**
	 * Returns an XSD data type for an XDI address.
	 */
	public static String xsdTypeFromDataTypeXDIAddress(XDIAddress dataTypeXDIAddress) {

		if (XDIAddressUtil.startsWithXDIAddress(dataTypeXDIAddress, XDI_ADD_DATATYPE_XSD) == null) throw new Xdi2RuntimeException("Invalid XSD data type address: " + dataTypeXDIAddress);

		XDIAddress xsdTypeXDIAddress = dataTypeXDIAddress;

		return xsdTypeXDIAddress.toString().substring(1).replace(XDIConstants.CS_CLASS_RESERVED.toString(), ":");
	}

	/**
	 * Returns an XSD data type for a JSON data type.
	 */
	public static String jsonTypeFromDataTypeXDIAddress(XDIAddress dataTypeXDIAddress) {

		if (XDIAddressUtil.startsWithXDIAddress(dataTypeXDIAddress, XDI_ADD_DATATYPE_JSON) == null) throw new Xdi2RuntimeException("Invalid JSON data type address: " + dataTypeXDIAddress);

		XDIAddress jsonTypeXDIAddress = XDIAddressUtil.localXDIAddress(dataTypeXDIAddress, - XDI_ADD_DATATYPE_JSON.getNumXDIArcs());

		return jsonTypeXDIAddress.toString().substring(1);
	}

	/**
	 * Returns a MIME data type for an XDI address.
	 */
	public static String mimeTypeFromDataTypeXDIAddress(XDIAddress dataTypeXDIAddress) {

		if (XDIAddressUtil.startsWithXDIAddress(dataTypeXDIAddress, XDI_ADD_DATATYPE_MIME) == null) throw new Xdi2RuntimeException("Invalid MIME data type address: " + dataTypeXDIAddress);

		XDIAddress mimeTypeXDIAddress = XDIAddressUtil.localXDIAddress(dataTypeXDIAddress, - XDI_ADD_DATATYPE_MIME.getNumXDIArcs());

		return mimeTypeXDIAddress.toString().substring(1).replace(XDIConstants.CS_CLASS_RESERVED.toString(), "/");
	}
}
