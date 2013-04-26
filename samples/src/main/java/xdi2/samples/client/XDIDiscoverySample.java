package xdi2.samples.client;

import xdi2.client.http.XDIHttpClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.discovery.XDIDiscovery;

public class XDIDiscoverySample {

	public static void main(String[] args) throws Exception {

		XDIDiscovery discovery = new XDIDiscovery();
		discovery.setXdiClient(new XDIHttpClient("http://localhost:12220/"));    // this is the default

		XDIDiscoveryResult result = discovery.discover("=markus");

		System.out.println("Cloud Number: " + result.getCloudNumber());    // [=]!91F2.8153.F600.AE24
		System.out.println("URI: " + result.getEndpointUri());             // https://xdi.fullxri.com/[=]!91F2.8153.F600.AE24/
	}
}
