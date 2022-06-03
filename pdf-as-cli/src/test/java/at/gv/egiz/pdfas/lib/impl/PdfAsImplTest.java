package at.gv.egiz.pdfas.lib.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.activation.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.lib.api.sign.SigningTimeSource;
import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;

public class PdfAsImplTest {

	private final File pdfasConfigFolder = new File(PdfAsImplTest.class.getResource("/pdfas-config").getFile());
	private final PdfAs pdfas = PdfAsFactory.createPdfAs(pdfasConfigFolder);
	
	private final IMocksControl ctrl = EasyMock.createControl();
	
	private final PAdESSignerKeystore signer;
	
	private final DataSource inputDataSource;
	
	private final Certificate signingCertificate;
	
	public PdfAsImplTest() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, PDFASError {
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream in = PdfAsImplTest.class.getResourceAsStream("/test.p12")) {
			keyStore.load(in, "123456".toCharArray());
		}
		signer = new PAdESSignerKeystore(keyStore, "ecc_test", "123456");
		
		try (InputStream in = getClass().getResourceAsStream("/rotate_090.pdf")) {
			inputDataSource = new ByteArrayDataSource(IOUtils.toByteArray(in));
		}
		
		signingCertificate = keyStore.getCertificate("ecc_test");
		
	}
	
	@Test
	public void test_sign_useOfSigningTimeSource() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		signParameter.setDataSource(inputDataSource);
		
		ctrl.reset();
		
		SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
		signParameter.setSigningTimeSource(signingTimeSource);
		
		Calendar otherDay = Calendar.getInstance();
		otherDay.setTime(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
		
		expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(otherDay);
		
		ctrl.replay();
		
		SignResult result = pdfas.sign(signParameter);
		
		ctrl.verify();
		
		assertThat(result.getSigningDate(), is(otherDay));

	}
	
	@Test
	public void test_sign_resultProvidesSigningTime() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		signParameter.setDataSource(inputDataSource);
		
		SignResult result = pdfas.sign(signParameter);
		
		assertThat(result.getSigningDate().getTime()).isInSameMinuteWindowAs(new Date());
		
	}
	
	@Test
	public void test_stepwise_sign_resultProvidesSigningTime() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, PdfAsException {
		
		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		signParameter.setDataSource(inputDataSource);
		
		StatusRequest statusRequest = pdfas.startSign(signParameter);
		
		statusRequest.setCertificate(signingCertificate.getEncoded());
		statusRequest = pdfas.process(statusRequest);

		// since we need a valid signature value we perform a signature (outside the startSign -> process -> process -> finishSign path).

		ctrl.reset();
		
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		OperationStatus operationStatus = ctrl.createMock(OperationStatus.class);
		expect(requestedSignature.getStatus()).andReturn(operationStatus);
		expect(operationStatus.getMetaInformations()).andReturn(new HashMap<>());

		ctrl.replay();
		
		byte[] signature = signer.sign(statusRequest.getSignatureData(), statusRequest.getSignatureDataByteRange(), signParameter, requestedSignature);
		
		// now we have a signature value for the next process step
		
		statusRequest.setSigature(signature);
		statusRequest = pdfas.process(statusRequest);
		
		SignResult result = pdfas.finishSign(statusRequest);
		
		assertThat(result.getSigningDate().getTime()).isInSameMinuteWindowAs(new Date());

	}
	
	@Test
	public void test_stepwise_sign_useOfSigningTimeSource() throws PDFASError, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, PdfAsException {

		SignParameter signParameter = PdfAsFactory.createSignParameter(pdfas.getConfiguration(), inputDataSource, NullOutputStream.NULL_OUTPUT_STREAM);
		signParameter.setPlainSigner(signer);
		signParameter.setDataSource(inputDataSource);
		
		StatusRequest statusRequest = pdfas.startSign(signParameter);
		
		ctrl.reset();
		
		SigningTimeSource signingTimeSource = ctrl.createMock(SigningTimeSource.class);
		signParameter.setSigningTimeSource(signingTimeSource);
		
		Calendar otherDay = Calendar.getInstance();
		otherDay.setTime(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
		
		expect(signingTimeSource.getSigningTime(anyObject(RequestedSignature.class))).andReturn(otherDay);
		
		ctrl.replay();
		
		statusRequest.setCertificate(signingCertificate.getEncoded());
		statusRequest = pdfas.process(statusRequest);
		
		ctrl.verify();
		
		
		// since we need a valid signature value we perform a signature (outside the startSign -> process -> process -> finishSign path).

		ctrl.reset();
		
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		OperationStatus operationStatus = ctrl.createMock(OperationStatus.class);
		expect(requestedSignature.getStatus()).andReturn(operationStatus);
		expect(operationStatus.getMetaInformations()).andReturn(new HashMap<>());

		ctrl.replay();
		
		byte[] signature = signer.sign(statusRequest.getSignatureData(), statusRequest.getSignatureDataByteRange(), signParameter, requestedSignature);
		
		// now we have a signature value for the next process step
		
		statusRequest.setSigature(signature);
		statusRequest = pdfas.process(statusRequest);
		
		SignResult result = pdfas.finishSign(statusRequest);
		
		assertThat(result.getSigningDate(), is(otherDay));

	}
	
}
