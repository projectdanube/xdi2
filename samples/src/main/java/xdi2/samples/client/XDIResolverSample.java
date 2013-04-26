package xdi2.samples.client;

import xdi2.client.http.XDIHttpClient;
import xdi2.discovery.XDIResolutionResult;
import xdi2.discovery.XDIResolver;

public class XDIResolverSample {

	public static void main(String[] args) throws Exception {

		XDIResolver resolver = new XDIResolver();
		resolver.setXdiClient(new XDIHttpClient("http://xri2xdi.net/"));    // this is the default

		XDIResolutionResult result = resolver.resolve("=markus");

		System.out.println("I-Number: " + result.getCloudnumber());    // [=]!91F2.8153.F600.AE24
		System.out.println("URI: " + result.getEndpointUri());             // https://xdi.fullxri.com/[=]!91F2.8153.F600.AE24/
	}
}
