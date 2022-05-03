package at.gv.egiz.pdfas.lib.api.sign;

import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.activation.DataSource;
import javax.annotation.Nonnull;

import iaik.asn1.structures.AlgorithmID;

//TODO[PDFAS-114]: Add javadoc

public class ExternalSignatureContext {

	private DigestInfo digestInfo;
	private AlgorithmID signingAlgorithm;
	private ZonedDateTime signingTime = ZonedDateTime.now();
	private X509Certificate signingCertificate;
	private DataSource digestInputData;
	private DataSource preparedSignedDocument;

	@Nonnull
	public ZonedDateTime getSigningTime() {
		return signingTime;
	}

	public Optional<DigestInfo> getDigestInfo() {
		return Optional.ofNullable(digestInfo);
	}

	public void setDigestInfo(@Nonnull DigestInfo digestInfo) {
		this.digestInfo = Objects.requireNonNull(digestInfo, "'digestInfo' must not be null.");
	}

	public Optional<DataSource> getDigestInputData() {
		return Optional.ofNullable(digestInputData);
	}
	
	public void setDigestInputData(@Nonnull DataSource digestInputData) {
		this.digestInputData = Objects.requireNonNull(digestInputData, "'digestInputData' must not be null.");
	}
	
	public Optional<DataSource> getPreparedSignedDocument() {
		return Optional.ofNullable(preparedSignedDocument);
	}

	public void setPreparedSignedDocument(@Nonnull DataSource preparedSignedDocument) {
		this.preparedSignedDocument = Objects.requireNonNull(preparedSignedDocument, "'preparedSignedDocument' must not be null.");
	}

	public Optional<X509Certificate> getSigningCertificate() {
		return Optional.ofNullable(signingCertificate);
	}

	public void setSigningCertificate(@Nonnull X509Certificate signingCertificate) {
		this.signingCertificate = Objects.requireNonNull(signingCertificate, "'signingCertificate' is required.");
	}
	

}
