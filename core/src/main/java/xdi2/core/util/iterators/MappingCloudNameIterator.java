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

	public MappingCloudNameIterator(Iterator<XDIAddress> XDIaddresses) {

		super(XDIaddresses);
	}

	@Override
	public CloudName map(XDIAddress XDIaddress) {

		CloudName cloudName = CloudName.fromXDIAddress(XDIaddress);
		if (cloudName == null) cloudName = CloudName.fromPeerRootXDIarc(XDIaddress);
		if (cloudName == null) return null;
		
		return cloudName;
	}
}
