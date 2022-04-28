package at.gv.egiz.pdfas.lib.api.sign;

import iaik.asn1.structures.AlgorithmID;

// TODO[PDFAS-114]: Add javadoc

public interface DigestInfo {

	AlgorithmID getAlgorithm();

	byte[] getValue();

}
