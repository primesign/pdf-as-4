package at.gv.egiz.pdfas.lib.api.sign;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

//TODO[PDFAS-114]: Add javadoc

@ParametersAreNullableByDefault
public class ExternalSignatureContext {

	private String digestAlgorithmOid;
	private byte[] digestValue;

	private String signatureAlgorithmOid;
	private byte[] signatureData;
	private int[] signatureByteRange;

	private X509Certificate signingCertificate;

	private DataSource preparedDocument;

	@Nullable
	public String getDigestAlgorithmOid() {
		return digestAlgorithmOid;
	}

	@Nullable
	public byte[] getDigestValue() {
		return digestValue;
	}

	@Nullable
	public String getSignatureAlgorithmOid() {
		return signatureAlgorithmOid;
	}

	@Nullable
	public byte[] getSignatureData() {
		return signatureData;
	}

	@Nullable
	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

	@Nullable
	public DataSource getPreparedDocument() {
		return preparedDocument;
	}

	public void setDigestAlgorithmOid(String digestAlgorithmOid) {
		this.digestAlgorithmOid = digestAlgorithmOid;
	}

	public void setDigestValue(byte[] digestValue) {
		this.digestValue = digestValue;
	}

	public void setSignatureAlgorithmOid(String signatureAlgorithmOid) {
		this.signatureAlgorithmOid = signatureAlgorithmOid;
	}

	public void setSignatureData(byte[] signatureData) {
		this.signatureData = signatureData;
	}

	public void setSigningCertificate(X509Certificate signingCertificate) {
		this.signingCertificate = signingCertificate;
	}

	public void setPreparedDocument(DataSource preparedDocument) {
		this.preparedDocument = preparedDocument;
	}
	
	public int[] getSignatureByteRange() {
		return signatureByteRange;
	}

	public void setSignatureByteRange(int[] signatureByteRange) {
		this.signatureByteRange = signatureByteRange;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (digestAlgorithmOid != null) {
			builder.append("digestAlgorithmOid=").append(digestAlgorithmOid);
		}
		if (digestValue != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("digestValue=").append(Base64.getEncoder().encodeToString(digestValue));
		}
		if (signatureAlgorithmOid != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureAlgorithmOid=").append(signatureAlgorithmOid);
		}
		if (signatureByteRange != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureByteRange=").append(Arrays.toString(signatureByteRange));
		}
		if (signingCertificate != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			try {
				builder.append("signingCertificate=").append(DigestUtils.sha1Hex(signingCertificate.getEncoded()));
			} catch (CertificateEncodingException e) {
				// should never occur
				throw new RuntimeException("Unable to encode certificate.", e);
			}
		}
		if (signatureData != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("signatureData=").append(StringUtils.abbreviate(Hex.encodeHexString(signatureData), 20));
		}
		if (preparedDocument != null) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append("preparedDocument=").append(preparedDocument);
		}
		builder.insert(0,  "ExternalSignatureContext [");
		builder.append("]");
		return builder.toString();
	}
	
}
