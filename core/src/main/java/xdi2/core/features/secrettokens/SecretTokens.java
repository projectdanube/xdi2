package xdi2.core.features.secrettokens;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiContext;

public class SecretTokens {

	public static String PREFIX_XDI2_DIGEST = "xdi2-digest";

	private SecretTokens() { }

	/**
	 * Given a context, get a secret token.
	 * @param graph The graph.
	 * @return A secret token.
	 */
	public static String getLocalSaltAndDigestSecretToken(XdiContext<?> xdiContext) {

		// find local salt and digest secret token

		XdiAttribute localSaltAndDigestSecretTokenXdiAttribute = xdiContext.getXdiAttribute(XDISecurityConstants.XDI_ADD_DIGEST_SECRET_TOKEN, false);
		localSaltAndDigestSecretTokenXdiAttribute = localSaltAndDigestSecretTokenXdiAttribute == null ? null : localSaltAndDigestSecretTokenXdiAttribute.dereference();

		LiteralNode localSaltAndDigestSecretTokenLiteral = localSaltAndDigestSecretTokenXdiAttribute == null ? null : localSaltAndDigestSecretTokenXdiAttribute.getLiteralNode();
		String localSaltAndDigestSecretToken = localSaltAndDigestSecretTokenLiteral == null ? null : localSaltAndDigestSecretTokenLiteral.getLiteralDataString();

		// done

		return localSaltAndDigestSecretToken;
	}

	/*
	 * Helper methods
	 */

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt) {

		String localSalt = randomSalt();

		return localSaltAndDigestSecretToken(secretToken, globalSalt, localSalt);
	}

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt, String localSalt) {

		String digestSecretToken = digestSecretToken(secretToken, globalSalt, localSalt);

		return PREFIX_XDI2_DIGEST + ":" + localSalt + ":" + digestSecretToken;
	}

	public static String digestSecretToken(String secretToken, String globalSalt, String localSalt) {

		if (! isValidSalt(globalSalt)) throw new Xdi2RuntimeException("Invalid global salt.");
		if (! isValidSalt(localSalt)) throw new Xdi2RuntimeException("Invalid local salt.");

		return sha512HexString(globalSalt + ":" + localSalt + ":" + sha512HexString(globalSalt + ":" + new String(Base64.encodeBase64(secretToken.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"))));
	}

	public static String randomSalt() {

		return UUID.randomUUID().toString();
	}

	public static boolean isValidSalt(String salt) {

		try {

			UUID.fromString(salt);
		} catch (IllegalArgumentException ex) {

			return false;
		}

		if (salt.length() != 36) return false;

		return true;
	}

	private static String sha512HexString(String string) {

		MessageDigest digest;

		try {

			digest = MessageDigest.getInstance("SHA-512");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		digest.reset();
		digest.update(string.getBytes(Charset.forName("UTF-8")));

		return new String(Hex.encodeHex(digest.digest()));
	}
}
