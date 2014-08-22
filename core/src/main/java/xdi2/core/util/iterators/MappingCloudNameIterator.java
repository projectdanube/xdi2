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

	public MappingCloudNameIterator(Iterator<XDIAddress> addresses) {

		super(addresses);
	}

	@Override
	public CloudName map(XDIAddress address) {

		CloudName cloudName = CloudName.fromAddress(address);
		if (cloudName == null) cloudName = CloudName.fromPeerRootArc(address);
		if (cloudName == null) return null;
		
		return cloudName;
	}
}
