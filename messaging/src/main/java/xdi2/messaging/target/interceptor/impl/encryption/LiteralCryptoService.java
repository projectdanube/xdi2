package xdi2.messaging.target.interceptor.impl.encryption;

public interface LiteralCryptoService {

	public void init() throws Exception;
	public void shutdown() throws Exception;
	public String encryptLiteralDataString(String literalDataString) throws Exception;
	public String decryptLiteralDataString(String encryptedLiteralDataString) throws Exception;
}
