package xdi2.messaging.target.interceptor.impl.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class StaticLiteralCryptoService extends AbstractLiteralCryptoService implements LiteralCryptoService {

	private String secretKeyString;

	private SecretKey secretKey;

	/*
	 * Instance methods
	 */

	@Override
	public void init() throws Exception {

		if (this.getSecretKeyString() == null) throw new NullPointerException("No secret key string.");

		this.secretKey = new SecretKeySpec(Base64.decodeBase64(this.getSecretKeyString()), "AES");
	}

	@Override
	public void shutdown() throws Exception {

		this.secretKey = null;
	}

	@Override
	public String encryptLiteralDataString(String literalDataString) throws Exception {

		String encryptedLiteralDataString;

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		byte[] encryptedLiteralDataBytes = cipher.doFinal(literalDataString.getBytes("UTF-8"));
		encryptedLiteralDataString = Base64.encodeBase64String(encryptedLiteralDataBytes);

		return encryptedLiteralDataString;
	}

	@Override
	public String decryptLiteralDataString(String encryptedLiteralDataString) throws Exception {

		String literalDataString;

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		byte[] literalDataBytes = cipher.doFinal(Base64.decodeBase64(encryptedLiteralDataString));
		literalDataString = new String(literalDataBytes, "UTF-8");

		return literalDataString;
	}

	/*
	 * Getters and setters
	 */

	public String getSecretKeyString() {

		return this.secretKeyString;
	}

	public void setSecretKeyString(String secretKeyString) {

		this.secretKeyString = secretKeyString;
	}
}
