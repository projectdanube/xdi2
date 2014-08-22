package xdi2.core.util;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Various utility methods for working with XRI 2.0 syntax.
 * 
 * @author markus
 */
public final class XRI2Util {

	private static final Logger log = LoggerFactory.getLogger(XRI2Util.class);

	private XRI2Util() { }

	/**
	 * Maps an XRI 2.0 I-Number to a Cloud Number.
	 */
	public static CloudNumber iNumberToCloudNumber(String iNumber) {

		if (log.isTraceEnabled()) log.trace("iNumberToCloudNumber(" + iNumber + ")");

		if (iNumber.startsWith("xri://")) iNumber = iNumber.substring("xri://".length());

		Character cs = Character.valueOf(iNumber.charAt(0));
		if ((! XDIConstants.CS_AUTHORITY_PERSONAL.equals(cs)) && (! XDIConstants.CS_AUTHORITY_LEGAL.equals(cs))) return null;

		iNumber = iNumber.substring(2).toLowerCase();

		String[] parts = iNumber.split("\\.");
		if (parts.length != 4) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			while (parts[i].length() < 4) parts[i] = "0" + parts[i];
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[" + cs + "]" + XDIConstants.CS_MEMBER_UNORDERED + ":uuid:");
		builder.append(parts[0]);
		builder.append(parts[1]);
		builder.append("-");
		builder.append(parts[2]);
		builder.append("-");
		builder.append(parts[3]);
		builder.append("-");
		builder.append(parts[0]);
		builder.append("-");
		builder.append(parts[1]);
		builder.append(parts[2]);
		builder.append(parts[3]);

		return CloudNumber.create(builder.toString());
	}

	/**
	 * Maps a Cloud Number to an XRI 2.0 I-Number.
	 */
	public static String cloudNumberToINumber(CloudNumber cloudNumber) {

		if (log.isTraceEnabled()) log.trace("cloudNumberToINumber(" + cloudNumber + ")");

		XDIAddress xri = cloudNumber.getAddress();

		if (xri.getNumArcs() != 2) return null;

		if (! xri.getFirstArc().isClassXs()) return null;
		if (xri.getFirstArc().hasLiteral()) return null;
		if (xri.getFirstArc().hasXRef()) return null;

		char cs = xri.getFirstArc().getCs().charValue();

		if (! XDIConstants.CS_MEMBER_UNORDERED.equals(xri.getLastArc().getCs())) return null;
		if (! xri.getLastArc().hasLiteral()) return null;
		if (xri.getLastArc().hasXRef()) return null;
		if (! xri.getLastArc().getLiteral().startsWith(":uuid")) return null;
		if (xri.getLastArc().getLiteral().length() != 42) return null;

		String[] parts = new String[4];
		parts[0] = xri.getLastArc().getLiteral().substring(6, 10);
		parts[1] = xri.getLastArc().getLiteral().substring(10, 14);
		parts[2] = xri.getLastArc().getLiteral().substring(15, 19);
		parts[3] = xri.getLastArc().getLiteral().substring(20, 24);

		if (! parts[0].equals(xri.getLastArc().getLiteral().substring(25, 29))) return null;
		if (! parts[1].equals(xri.getLastArc().getLiteral().substring(30, 34))) return null;
		if (! parts[2].equals(xri.getLastArc().getLiteral().substring(34, 38))) return null;
		if (! parts[3].equals(xri.getLastArc().getLiteral().substring(38, 42))) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			if (parts[i].indexOf('-') != -1) return null;
			parts[i] = parts[i].toUpperCase();
			while (parts[i].charAt(0) == '0') parts[i] = parts[i].substring(1);
		}

		StringBuilder builder = new StringBuilder();

		builder.append("" + cs + XDIConstants.CS_MEMBER_UNORDERED);
		builder.append(parts[0]);
		builder.append(".");
		builder.append(parts[1]);
		builder.append(".");
		builder.append(parts[2]);
		builder.append(".");
		builder.append(parts[3]);

		return builder.toString();
	}

	/**
	 * Maps an XRI 2.0 service type to an XDI arc.
	 */
	public static XDIArc typeToXdiarc(String type) {

		if (log.isTraceEnabled()) log.trace("typeToXdiarc(" + type + ")");

		if (type.startsWith("xri://")) type = type.substring(6);
		type = type.replace('+' , '#');

		XDIArc xdiarc = null;

		try { xdiarc = XDIArc.create(type); } catch (Exception ex) { xdiarc = null; }
		if (xdiarc == null) try { xdiarc = XDIArc.create("#(" + type + ")"); } catch (Exception ex) { xdiarc = null; }
		if (xdiarc == null) try { xdiarc = XDIArc.create("#(" + URLEncoder.encode(type, "UTF-8") + ")"); } catch (Exception ex) { xdiarc = null; }

		if (xdiarc == null) return null;

		if (! XdiAttributeSingleton.isValidarc(xdiarc)) xdiarc = XdiAttributeSingleton.createarc(xdiarc);

		return xdiarc;
	}
}
