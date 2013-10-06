package xdi2.tests.core.features.signatures;

import junit.framework.TestCase;
import xdi2.core.features.signatures.Signature;
import xdi2.core.xri3.XDI3Segment;

public class SignaturesTest extends TestCase {

	public void testAlgorithmAndLengthXris() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("$sha$256$rsa$2048");
		XDI3Segment xri2 = XDI3Segment.create("$sha$384$aes$256");

		assertEquals(Signature.getDigestAlgorithm(xri1), "sha");
		assertEquals(Signature.getDigestLength(xri1), Integer.valueOf(256));
		assertEquals(Signature.getKeyAlgorithm(xri1), "rsa");
		assertEquals(Signature.getKeyLength(xri1), Integer.valueOf(2048));

		assertEquals(Signature.getDigestAlgorithm(xri2), "sha");
		assertEquals(Signature.getDigestLength(xri2), Integer.valueOf(384));
		assertEquals(Signature.getKeyAlgorithm(xri2), "aes");
		assertEquals(Signature.getKeyLength(xri2), Integer.valueOf(256));
	}
}
