package xdi2.core.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XDI3SubSegment;

/**
 * Constants for XDI timestamps.
 * 
 * @author markus
 */
public final class XDITimestampsConstants {

	public static final XDI3SubSegment XRI_SS_T = Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("$t"));

	public static final DateFormat[] FORMATS_TIMESTAMP = new SimpleDateFormat[] {

		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),    // <-- default format
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	};

	private XDITimestampsConstants() { }
}
