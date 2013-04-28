package xdi2.samples.client;

import xdi2.client.http.XDIHttpClient;
import xdi2.core.xri3.XDI3Segment;
import xdi2.discovery.XDIDiscovery;
import xdi2.discovery.XDIDiscoveryResult;

public class XDIDiscoverySample {

	public static void main(String[] args) throws Exception {

		XDIDiscovery discovery = new XDIDiscovery();
		discovery.setRegistryXdiClient(new XDIHttpClient("http://localhost:12220/"));    // this is the default

		XDIDiscoveryResult result = discovery.discoverFromXri(XDI3Segment.create("=markus"));

		System.out.println("Cloud Number: " + result.getCloudNumber());    // [=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24
		System.out.println("URI: " + result.getEndpointUri());             // http://mycloud.neustar.biz/%5B%3D%5D!%3Auuid%3A91f28153-f600-ae24-91f2-8153f600ae24/
	}
}
