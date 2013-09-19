package xdi2.messaging.target.interceptor.impl.encryption;

/**
 * The purpose of this interface is to provide functionality for encrypting and
 * decrypting literals in string form. This is used by the
 * LiteralEncryptionInterceptor.
 */
public interface LiteralCryptoService {

	public void init() throws Exception;
	public void shutdown() throws Exception;

	/**
	 * Encrypts a literal in string form
	 */
	public String encryptLiteralDataString(String literalDataString) throws Exception;

	/**
	 * Decrypts a literal in string form
	 */
	public String decryptLiteralDataString(String encryptedLiteralDataString) throws Exception;
}
