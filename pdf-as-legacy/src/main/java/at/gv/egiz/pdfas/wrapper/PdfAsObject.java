package at.gv.egiz.pdfas.wrapper;

import java.io.File;
import java.util.List;

import com.sun.org.apache.xml.internal.utils.UnImplNode;

import at.gv.egiz.pdfas.api.PdfAs;
import at.gv.egiz.pdfas.api.analyze.AnalyzeParameters;
import at.gv.egiz.pdfas.api.analyze.AnalyzeResult;
import at.gv.egiz.pdfas.api.commons.DynamicSignatureLifetimeEnum;
import at.gv.egiz.pdfas.api.commons.DynamicSignatureProfile;
import at.gv.egiz.pdfas.api.exceptions.ErrorCode;
import at.gv.egiz.pdfas.api.exceptions.PdfAsException;
import at.gv.egiz.pdfas.api.sign.SignParameters;
import at.gv.egiz.pdfas.api.sign.SignResult;
import at.gv.egiz.pdfas.api.sign.SignatureDetailInformation;
import at.gv.egiz.pdfas.api.verify.VerifyAfterAnalysisParameters;
import at.gv.egiz.pdfas.api.verify.VerifyAfterReconstructXMLDsigParameters;
import at.gv.egiz.pdfas.api.verify.VerifyParameters;
import at.gv.egiz.pdfas.api.verify.VerifyResults;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigAfterAnalysisParameters;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigParameters;
import at.gv.egiz.pdfas.api.xmldsig.ReconstructXMLDsigResult;

public class PdfAsObject implements PdfAs {

	public SignResult sign(SignParameters signParameters) throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public SignResult sign(SignParameters signParameters,
			SignatureDetailInformation signatureDetailInformation)
			throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public VerifyResults verify(VerifyParameters verifyParameters)
			throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public AnalyzeResult analyze(AnalyzeParameters analyzeParameters)
			throws PdfAsException {
		throw new PdfAsException(ErrorCode.FUNCTION_NOT_AVAILABLE,
				new RuntimeException());
	}

	public ReconstructXMLDsigResult reconstructXMLDSIG(
			ReconstructXMLDsigParameters reconstructXMLDsigParameters)
			throws PdfAsException {
		throw new PdfAsException(ErrorCode.FUNCTION_NOT_AVAILABLE,
				new RuntimeException());
	}

	public ReconstructXMLDsigResult reconstructXMLDSIG(
			ReconstructXMLDsigAfterAnalysisParameters reconstructXMLDsigParameters)
			throws PdfAsException {
		throw new PdfAsException(ErrorCode.FUNCTION_NOT_AVAILABLE,
				new RuntimeException());
	}

	public VerifyResults verify(
			VerifyAfterAnalysisParameters verifyAfterAnalysisParameters)
			throws PdfAsException {
		throw new PdfAsException(ErrorCode.FUNCTION_NOT_AVAILABLE,
				new RuntimeException());
	}

	public VerifyResults verify(
			VerifyAfterReconstructXMLDsigParameters verifyAfterReconstructXMLDsigParameters)
			throws PdfAsException {
		throw new PdfAsException(ErrorCode.FUNCTION_NOT_AVAILABLE,
				new RuntimeException());
	}

	public void reloadConfig() throws PdfAsException {
		// TODO Auto-generated method stub

	}

	public List getProfileInformation() throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public DynamicSignatureProfile createDynamicSignatureProfile(
			String parentProfile, DynamicSignatureLifetimeEnum mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public DynamicSignatureProfile createDynamicSignatureProfile(
			String myUniqueName, String parentProfile,
			DynamicSignatureLifetimeEnum mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public DynamicSignatureProfile createEmptyDynamicSignatureProfile(
			DynamicSignatureLifetimeEnum mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public DynamicSignatureProfile createEmptyDynamicSignatureProfile(
			String myUniqueName, DynamicSignatureLifetimeEnum mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public DynamicSignatureProfile loadDynamicSignatureProfile(
			String profileName) {
		// TODO Auto-generated method stub
		return null;
	}

	public SignatureDetailInformation prepareSign(SignParameters signParameters)
			throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public SignResult finishSign(SignParameters signParameters,
			SignatureDetailInformation signatureDetailInformation)
			throws PdfAsException {
		// TODO Auto-generated method stub
		return null;
	}

	public PdfAsObject(File workdirectory) {
		//TODO
	}

}
