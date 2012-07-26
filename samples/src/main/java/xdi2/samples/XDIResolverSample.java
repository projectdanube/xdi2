package xdi2.samples;

import xdi2.client.http.XDIHttpClient;
import xdi2.resolution.XDIResolutionResult;
import xdi2.resolution.XDIResolver;

public class XDIResolverSample {

	public static void main(String[] args) throws Exception {

		XDIResolver resolver = new XDIResolver();
		resolver.setXdiClient(new XDIHttpClient("http://xri2xdi.net/"));    // this is the default

		XDIResolutionResult result = resolver.resolve("=markus");

		System.out.println("I-Number: " + result.getInumber());    // =!91F2.8153.F600.AE24
		System.out.println("URI: " + result.getUri());             // https://xdi.fullxri.com/=!91F2.8153.F600.AE24/
	}
}
