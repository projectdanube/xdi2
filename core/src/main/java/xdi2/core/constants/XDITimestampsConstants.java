package xdi2.core.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * A class that provides various constants for XDI timestamps.
 * 
 * @author markus
 */
public final class XDITimestampsConstants {

	public static final XRI3SubSegment XRI_SS_T = Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("$t"));

	public static final DateFormat[] FORMATS_TIMESTAMP = new SimpleDateFormat[] {

		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),    // <-- default format
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	};

	private XDITimestampsConstants() { }
}
