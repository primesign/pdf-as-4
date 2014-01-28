package at.gv.egiz.pdfas.web.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class DigestHelper {

	public static final String SHA1 = "SHA-1";
	public static final String SHA224 = "SHA-224";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";
	
	public static String DefaulAlgorithm = SHA256;
	
	public static String getHexEncodedHash(byte[] data) throws NoSuchAlgorithmException {
		return getHexEncodedHash(data, DefaulAlgorithm);
	}
	
	public static String getHexEncodedHash(byte[] data, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		byte[] hash = md.digest(data);
		return Hex.encodeHexString(hash);
	}
}
