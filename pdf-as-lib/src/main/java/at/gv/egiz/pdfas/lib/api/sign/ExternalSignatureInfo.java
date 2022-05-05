package at.gv.egiz.pdfas.lib.api.sign;

import javax.annotation.Nonnull;

import iaik.asn1.structures.AlgorithmID;

// TODO[PDFAS-114]: Add javadoc

public interface ExternalSignatureInfo {

	@Nonnull
	AlgorithmID getDigestAlgorithm();
	
	@Nonnull
	AlgorithmID getSignatureAlgorithm();

	@Nonnull
	byte[] getDigestValue();
	
	@Nonnull
	byte[] getSignatureData();

}
