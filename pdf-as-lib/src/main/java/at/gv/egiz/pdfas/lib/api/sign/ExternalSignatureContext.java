package at.gv.egiz.pdfas.lib.api.sign;

import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

//TODO[PDFAS-114]: Add javadoc

@ParametersAreNullableByDefault
public class ExternalSignatureContext {

	private String digestAlgorithmOid;
	private byte[] digestValue;

	private String signatureAlgorithmOid;
	private byte[] signatureData;
	private int[] signatureByteRange;

	private ZonedDateTime signingTime;
	private X509Certificate signingCertificate;

	private DataSource preparedSignedDocument;

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
	public ZonedDateTime getSigningTime() {
		return signingTime;
	}

	@Nullable
	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

	@Nullable
	public DataSource getPreparedSignedDocument() {
		return preparedSignedDocument;
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

	public void setSigningTime(ZonedDateTime signingTime) {
		this.signingTime = signingTime;
	}

	public void setSigningCertificate(X509Certificate signingCertificate) {
		this.signingCertificate = signingCertificate;
	}

	public void setPreparedSignedDocument(DataSource preparedSignedDocument) {
		this.preparedSignedDocument = preparedSignedDocument;
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
		builder.append("ExternalSignatureContext [");
		builder.append("digestAlgorithmOid=").append(digestAlgorithmOid);
		builder.append(", digestValue=").append(digestValue != null ? Base64.getEncoder().encodeToString(digestValue) : null);
		builder.append(", signatureAlgorithmOid=").append(signatureAlgorithmOid);
		builder.append(", signatureData=").append(signatureData != null ? "<set>" : null);
		builder.append(", signatureByteRange=").append(signatureByteRange != null ? Arrays.toString(signatureByteRange) : null);
		builder.append(", signingTime=").append(signingTime);
		builder.append(", signingCertificate=").append(signingCertificate != null ? "<set>" : null);
		builder.append(", preparedSignedDocument=").append(preparedSignedDocument);
		builder.append("]");
		return builder.toString();
	}
	
}
