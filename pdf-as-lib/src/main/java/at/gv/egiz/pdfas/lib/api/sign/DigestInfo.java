package at.gv.egiz.pdfas.lib.api.sign;

import javax.annotation.Nonnull;

import iaik.asn1.structures.AlgorithmID;

// TODO[PDFAS-114]: Add javadoc

public interface DigestInfo {

	@Nonnull
	AlgorithmID getAlgorithm();

	@Nonnull
	byte[] getValue();
	
	@Nonnull
	byte[] getContextData();

}
