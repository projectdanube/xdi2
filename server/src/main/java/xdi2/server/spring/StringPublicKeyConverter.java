package xdi2.server.spring;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.convert.converter.Converter;

import xdi2.core.exceptions.Xdi2RuntimeException;

public class StringPublicKeyConverter implements Converter<String, PublicKey> {

	@Override
	public PublicKey convert(String string) {

		PublicKey publicKey;

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(string));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			publicKey = keyFactory.generatePublic(keySpec);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2RuntimeException("Invalid RSA public key " + string + ": " + ex.getMessage(), ex);
		}

		return publicKey;
	}
}
