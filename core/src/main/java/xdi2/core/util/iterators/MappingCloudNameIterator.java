package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI addresses to Cloud Names.
 * 
 * @author markus
 */
public class MappingCloudNameIterator extends MappingIterator<XDIAddress, CloudName> {

	public MappingCloudNameIterator(Iterator<XDIAddress> xris) {

		super(xris);
	}

	@Override
	public CloudName map(XDIAddress xri) {

		CloudName cloudName = CloudName.fromAddress(xri);
		if (cloudName == null) cloudName = CloudName.fromPeerRootArc(xri);
		if (cloudName == null) return null;
		
		return cloudName;
	}
}
