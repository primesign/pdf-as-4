package at.gv.egiz.pdfas.lib.impl.signing.pdfbox2;

import static at.gv.egiz.pdfas.lib.impl.signing.pdfbox2.LTVSupportImpl.toCOSStream;
import static at.gv.egiz.pdfas.lib.impl.signing.pdfbox2.LTVSupportImpl.toX509CRL;
import static at.gv.egiz.pdfas.lib.impl.signing.pdfbox2.LTVSupportImpl.toX509Certificate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.assertj.core.util.Lists;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMockBuilder;
import org.easymock.IMocksControl;
import org.junit.Test;

import at.gv.egiz.pdfas.lib.pki.spi.CertificateVerificationData;

public class LTVSupportImplTest {
	
	private final LTVSupportImpl cut = new LTVSupportImpl();
	
	/**
	 * Validates a provided extensions dictionary.
	 * <p>
	 * According to ETSI TS 102 778-4 v1.1.2, 4.4 and ISO 32000-1:2008, 7.12.2 the following dictionary is expected:
	 * </p>
	 * 
	 * <pre>
	 * <</Extensions
	 *   <</ADBE
	 *     <</BaseVersion/1.7
	 *       /ExtensionLevel 5
	 *     >>
	 *   >>
	 * >>
	 * </pre>
	 * 
	 * Further entries are allowed (and ignored).
	 * 
	 * @param extDictionary     The provided extensions dictionary. (required; must not be {@code null})
	 * @param expectDirtyExtDic {@code true} if modifications are expected within the /Extensions dictionary, {@code false}
	 *                          otherwise.
	 * @param expectDirtyExtDic {@code true} if modifications are expected within the /ADBE dictionary, {@code false}
	 *                          otherwise.
	 */
	private void validateExtensionsDictionary(@Nonnull COSDictionary extDictionary, boolean expectDirtyExtDic, boolean expectDirtyAdbeDic) {

		// expect direct objects (ISO 32000-1:2008, 7.12.1)
		assertTrue(extDictionary.isDirect());
		
		// when the extensions dictionary is expected to be modified, it should be marked dirty
		assertEquals(expectDirtyExtDic, extDictionary.isNeedToBeUpdated());
		
		COSDictionary adbeDic = (COSDictionary) extDictionary.getDictionaryObject("ADBE");
		assertNotNull(adbeDic);
		
		// spec requires direct object (ISO 32000-1:2008, 7.12.1)
		assertTrue(adbeDic.isDirect());
		
		// when the adbe dictionary is expected to be modified, it should be marked dirty
		assertEquals(expectDirtyAdbeDic, adbeDic.isNeedToBeUpdated());
		
		// expect /BaseVersion/1.7/ExtensionLevel 5 (ETSI TS 102 778-4 v1.1.2, 4.4)
		// with "1.7" being a COS name (ISO 32000-1:2008, 7.12.2)
		assertThat(adbeDic.getCOSName(COSName.getPDFName("BaseVersion")), is(COSName.getPDFName("1.7")));
		assertThat(adbeDic.getInt("ExtensionLevel"), is(5));
		
	}
	
	@Test
	public void test_addOrUpdateADBEExtension() {
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setDirect(true);

		/**
		 * <</Extensions
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);

		validateExtensionsDictionary(extDictionary, true, true);
		
	}

	@Test
	public void test_addOrUpdateADBEExtension_dictionary_already_exists() {
		
		COSDictionary adbeDic = new COSDictionary();
		adbeDic.setDirect(true);
		adbeDic.setItem("BaseVersion", COSName.getPDFName("1.7"));
		adbeDic.setInt("ExtensionLevel", 5);
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setItem("ADBE", adbeDic);
		extDictionary.setDirect(true);
		// add some other developer extension ("MYDEVEXT")
		COSDictionary devDic = new COSDictionary();
		extDictionary.setItem("MYDEVEXT", devDic);
		devDic.setDirect(true);
		devDic.setItem("BaseVersion", COSName.getPDFName("1.7"));
		devDic.setInt("ExtensionLevel", 42);
		
		/**
		 * <</Extensions
		 *   <</ADBE
		 *     <</BaseVersion/1.7
		 *       /ExtensionLevel 5
		 *     >>
		 *   >>
		 *   <</MYDEVEXT
		 *     <</BaseVersion/1.7
		 *       /ExtensionLevel 42
		 *     >>
		 *   >>
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);
		
		// expect that nothing has changed (false, false) since we already had appropriate extensions
		validateExtensionsDictionary(extDictionary, false, false);
		
		// expect that existing dictionary is used
		COSDictionary newAdbeDic = (COSDictionary)extDictionary.getDictionaryObject("ADBE");
		assertSame(newAdbeDic, adbeDic);
		COSDictionary newDevDic = (COSDictionary)extDictionary.getDictionaryObject("MYDEVEXT");
		assertSame(newDevDic, devDic);
		
		// expect no change at all
		assertThat(extDictionary.entrySet(), hasSize(2));
		assertThat(newDevDic.getCOSName(COSName.getPDFName("BaseVersion")), is(COSName.getPDFName("1.7")));
		assertThat(newDevDic.getInt("ExtensionLevel"), is(42));
		assertFalse(newDevDic.isNeedToBeUpdated());
	}


	@Test
	public void test_addOrUpdateADBEExtension_dictionary_already_exists_with_non_suitable_values() {
		
		COSDictionary adbeDic = new COSDictionary();
		adbeDic.setDirect(true);
		adbeDic.setItem("BaseVersion", COSName.getPDFName("1.5"));
		adbeDic.setInt("ExtensionLevel", 42);
		
		COSDictionary extDictionary = new COSDictionary();
		extDictionary.setItem("ADBE", adbeDic);
		extDictionary.setDirect(true);
		
		/**
		 * <</Extensions
		 *   <</ADBE
		 *     <</BaseVersion/1.5
		 *       /ExtensionLevel 42
		 *     >>
		 * >>
		 */
		
		cut.addOrUpdateADBEExtension(extDictionary);
		
		validateExtensionsDictionary(extDictionary, true, true);

	}
	
	@Test
	public void test_addOrUpdateExtensions_noExtensionsYet() {
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateADBEExtension", COSDictionary.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		// test only aspect of addOrUpdateExtensions, skip test for addOrUpdateExtensions(extDictionary), just make sure method gets called
		
		ctrl.reset();

		PDDocument pdDocument = emptyDocument();
		
		// document has no extensions dictionary
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		assertNull(rootDictionary.getDictionaryObject("Extensions"));
		// document is not dirty
		assertFalse(rootDictionary.isNeedToBeUpdated());

		Capture<COSDictionary> extDictionary = newCapture();
		
		cut.addOrUpdateADBEExtension(capture(extDictionary));
		
		ctrl.replay();
		
		// cat cut with document that does not have an extension dictionary
		cut.addOrUpdateExtensions(pdDocument);
		
		ctrl.verify();
		
		COSDictionary extDictionaryFromDocument = (COSDictionary) rootDictionary.getDictionaryObject("Extensions");
		// expect that Extentions dictionary has been created 
		assertNotNull(extDictionaryFromDocument);
		
		// make sure mocked method gets called with dictionary
		assertSame(extDictionaryFromDocument, extDictionary.getValue());

		// root dictionary should also be marked dirty
		assertTrue(rootDictionary.isNeedToBeUpdated());
		
	}

	
	@Test
	public void test_addOrUpdateExtensions_with_existing_extensions() {
		
		// test only aspect of addOrUpdateExtensions, skip test for addOrUpdateExtensions(extDictionary), just make sure method gets called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateADBEExtension", COSDictionary.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		// add extensions to document
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		COSDictionary extDictionary = new COSDictionary();
		rootDictionary.setItem("Extensions", extDictionary);
		// document is not dirty
		assertFalse(rootDictionary.isNeedToBeUpdated());

		ctrl.reset();
		
		Capture<COSDictionary> capturedExtDictionary = newCapture();
		cut.addOrUpdateADBEExtension(capture(capturedExtDictionary));
		
		ctrl.replay();
		
		// cat cut with document that does not have an extension dictionary
		cut.addOrUpdateExtensions(pdDocument);
		
		ctrl.verify();
		
		COSDictionary extDictionaryFromDocument = (COSDictionary) rootDictionary.getDictionaryObject("Extensions");
		// expect "our" Extensions dictionary 
		assertSame(extDictionaryFromDocument, extDictionary);
		
		// make sure mocked method gets called with dictionary
		assertSame(extDictionaryFromDocument, capturedExtDictionary.getValue());

		// root dictionary should also be marked dirty
		assertTrue(rootDictionary.isNeedToBeUpdated());
		
	}
	
	@Test
	public void test_addLTVInfo_noChainCerts() throws CertificateEncodingException, CRLException, IOException {
		
		IMocksControl ctrl = createControl();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(null);
		
		ctrl.replay();
		
		assertThrows(IllegalStateException.class, () -> cut.addLTVInfo(emptyDocument(), certificateVerificationData));
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addLTVInfo_emptyChainCerts() throws CertificateEncodingException, CRLException, IOException {
		
		IMocksControl ctrl = createControl();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(Collections.emptyList());
		
		ctrl.replay();
		
		assertThrows(IllegalStateException.class, () -> cut.addLTVInfo(emptyDocument(), certificateVerificationData));
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addLTVInfo() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addLTVInfo, skip tests for addOrUpdateDSS(PDDocument, CertificateVerificationData),
		// ensurePdf17(PDDocument) and addOrUpdateExtensions(PDDocument), just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addOrUpdateDSS", PDDocument.class, CertificateVerificationData.class)
				.addMockedMethod("ensurePdf17", PDDocument.class)
				.addMockedMethod("addOrUpdateExtensions", PDDocument.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		expect(certificateVerificationData.getChainCerts()).andReturn(Lists.list(ctrl.createMock(X509Certificate.class)));
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		cut.ensurePdf17(pdDocument);
		cut.addOrUpdateExtensions(pdDocument);
		expect(certificateVerificationData.getCRLs()).andReturn(Collections.emptyList());
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(Collections.emptyList());
		
		ctrl.replay();
		
		cut.addLTVInfo(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_noCatalogVersion() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// no catalog version
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_withCatalogVersion16() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// catalog version 1.6
		pdDocument.getDocumentCatalog().setVersion("1.6");
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion14_withCatalogVersion17() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.4
		pdDocument.getDocument().setVersion(1.4f);
		// catalog version 1.6
		pdDocument.getDocumentCatalog().setVersion("1.7");
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensurePdf17_withHeaderVersion17_noCatalogVersion() {
		
		PDDocument pdDocument = emptyDocument();
		
		// (default) header version 1.7
		pdDocument.getDocument().setVersion(1.7f);
		// no catalog version
		
		cut.ensurePdf17(pdDocument);
		
		assertThat(pdDocument.getVersion(), is(1.7f));
		
	}
	
	@Test
	public void test_ensureDSSDictionary_dssDoesNotExist() {
		
		PDDocument pdDocument = emptyDocument();
		
		COSDictionary dssDictionary = cut.ensureDSSDictionary(pdDocument);
		
		// make sure dss dictionary has been created
		assertNotNull(dssDictionary);
		
		// and has been added to the provided document
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		COSDictionary dssDictionaryFromDocument = (COSDictionary) rootDictionary.getDictionaryObject("DSS");
		assertSame(dssDictionary, dssDictionaryFromDocument);
		
		// assert that both root dictionary and dss dictionary are marked dirty
		assertTrue(rootDictionary.isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		
	}
	
	@Test
	public void test_ensureDSSDictionary_dssAlreadyExist() {
		
		PDDocument pdDocument = emptyDocument();
		COSDictionary dssDictionaryFromDocument = new COSDictionary();
		COSDictionary rootDictionary = pdDocument.getDocumentCatalog().getCOSObject();
		rootDictionary.setItem("DSS", dssDictionaryFromDocument);
		
		COSDictionary dssDictionary = cut.ensureDSSDictionary(pdDocument);
		assertNotNull(dssDictionary);
		
		// make sure we get the existing dictionary
		assertSame(dssDictionary, dssDictionaryFromDocument);
		
		// assert that both root dictionary and dss dictionary are not marked
		assertFalse(rootDictionary.isNeedToBeUpdated());
		assertFalse(dssDictionary.isNeedToBeUpdated());

	}
	
	@Test
	public void test_addOrUpdateDSS() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, Iterable<X509CRL>)
		// just make sure methods get called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);

		// chaincerts
		
		List<X509Certificate> chainCerts = Lists.list(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);

		cut.addDSSCerts(pdDocument, chainCerts);
		
		// ocsp responses
		
		List<byte[]> encodedOCSPResponses = Lists.list(new byte[] { 1, 2, 3 });
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(encodedOCSPResponses).atLeastOnce();
		
		cut.addDSSOCSPs(pdDocument, encodedOCSPResponses);
		
		// clrs
		List<X509CRL> crls = Lists.list(ctrl.createMock(X509CRL.class));
		expect(certificateVerificationData.getCRLs()).andReturn(crls).atLeastOnce();
		
		cut.addDSSCRLs(pdDocument, crls);
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addOrUpdateDSS_noOCSPs() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, Iterable<X509CRL>)
		// just make sure respective methods get called and others gets not called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);
		
		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);
		
		// chaincerts
		
		List<X509Certificate> chainCerts = Lists.list(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);
		
		cut.addDSSCerts(pdDocument, chainCerts);
		
		// ocsp responses
		
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(Collections.emptyList()).atLeastOnce();
		
		// expect that cut.addDSSOCSPs is not called
		
		// clrs
		List<X509CRL> crls = Lists.list(ctrl.createMock(X509CRL.class));
		expect(certificateVerificationData.getCRLs()).andReturn(crls).atLeastOnce();
		
		cut.addDSSCRLs(pdDocument, crls);
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
	}
	
	@Test
	public void test_addOrUpdateDSS_noCRLs() throws CertificateEncodingException, CRLException, IOException {
		
		// test only aspect of addOrUpdateDSS, skip tests for
		//    addDSSCerts(PDDocument, Iterable<X509Certificate>)
		//    addDSSOCSPs(PDDocument, Iterable<byte[]>)
		//    addDSSCRLs(PDDocument, Iterable<X509CRL>)
		// just make sure respective methods get called and others gets not called
		
		IMocksControl ctrl = createControl();
		
		// mock only abstract method
		// @formatter:off
		IMockBuilder<LTVSupportImpl> mockBuilder = EasyMock.partialMockBuilder(LTVSupportImpl.class)
				.withConstructor()
				.addMockedMethod("addDSSCerts", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSOCSPs", PDDocument.class, Iterable.class)
				.addMockedMethod("addDSSCRLs", PDDocument.class, Iterable.class);
		// @formatter:on
		
		LTVSupportImpl cut = mockBuilder.createMock(ctrl);

		PDDocument pdDocument = emptyDocument();
		
		ctrl.reset();
		
		CertificateVerificationData certificateVerificationData = ctrl.createMock(CertificateVerificationData.class);

		// chaincerts
		
		List<X509Certificate> chainCerts = Lists.list(ctrl.createMock(X509Certificate.class));
		expect(certificateVerificationData.getChainCerts()).andReturn(chainCerts);

		cut.addDSSCerts(pdDocument, chainCerts);
		
		// ocsp responses
		
		List<byte[]> encodedOCSPResponses = Lists.list(new byte[] { 1, 2, 3 });
		expect(certificateVerificationData.getEncodedOCSPResponses()).andReturn(encodedOCSPResponses).atLeastOnce();

		cut.addDSSOCSPs(pdDocument, encodedOCSPResponses);

		// clrs
		expect(certificateVerificationData.getCRLs()).andReturn(Collections.emptyList()).atLeastOnce();
		// expect that cut.addDSSCRLs is not called
		
		ctrl.replay();
		
		cut.addOrUpdateDSS(pdDocument, certificateVerificationData);
		
		ctrl.verify();
		
	}
	
	@Test
	public void testAddDSSCerts_noDssSoFar() throws IOException, CertificateException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509Certificate cert1 = resourceToCertificate("Max_Mustermann.20210224-20260224.SerNo7C174E16.crt");
		X509Certificate cert2 = resourceToCertificate("Max_Mustermann.20210907-20260907.SerNo34731014.crt");
		
		cut.addDSSCerts(pdDocument, Lists.list(cert1, cert2));
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray certsArray = (COSArray) dssDictionary.getDictionaryObject("Certs");
		assertThat(certsArray.size(), is(2));
		
		// make sure that cert1 has been added to dss
		try (InputStream in = ((COSStream) certsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert1.getEncoded()));
		}
		// make sure that cert2 has been added to dss
		try (InputStream in = ((COSStream) certsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert2.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(certsArray.isNeedToBeUpdated());
		
	}
	
	@Test
	public void testAddDSSCerts_dssAlreadyExists() throws IOException, CertificateException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509Certificate cert1 = resourceToCertificate("Max_Mustermann.20210224-20260224.SerNo7C174E16.crt");
		X509Certificate cert2 = resourceToCertificate("Max_Mustermann.20210907-20260907.SerNo34731014.crt");
		
		cut.addDSSCerts(pdDocument, Lists.list(cert1, cert2));
		
		// document now contains DSS with two certs
		
		X509Certificate cert3 = resourceToCertificate("Max_Mustermann.20220223-20270223.SerNo38EF60B6.crt");
		X509Certificate cert4 = resourceToCertificate("Max_Mustermann.20220502-20270502.SerNo35C40B8B.crt");
		
		cut.addDSSCerts(pdDocument, Lists.list(cert3, cert4));
		
		// expect that document now contains all four certs
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray certsArray = (COSArray) dssDictionary.getDictionaryObject("Certs");
		assertThat(certsArray.size(), is(4));
		
		// make sure that dss still contains cert1
		try (InputStream in = ((COSStream) certsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert1.getEncoded()));
		}
		// make sure that dss still contains cert2
		try (InputStream in = ((COSStream) certsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert2.getEncoded()));
		}
		
		// make sure that cert3 has been added to dss
		try (InputStream in = ((COSStream) certsArray.get(2)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert3.getEncoded()));
		}
		// make sure that cert4 has been added to dss
		try (InputStream in = ((COSStream) certsArray.get(3)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert4.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(certsArray.isNeedToBeUpdated());
		
	}

	@Test
	public void testAddDSSCerts_avoidDuplicateCerts() throws IOException, CertificateException {

		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509Certificate cert1 = resourceToCertificate("Max_Mustermann.20210224-20260224.SerNo7C174E16.crt");
		X509Certificate cert2 = resourceToCertificate("Max_Mustermann.20210907-20260907.SerNo34731014.crt");
		
		// #1, #2
		cut.addDSSCerts(pdDocument, Lists.list(cert1, cert2));
		
		// document now contains DSS with two certs
		
		X509Certificate cert3 = resourceToCertificate("Max_Mustermann.20220223-20270223.SerNo38EF60B6.crt");
		
		// #2, #3
		cut.addDSSCerts(pdDocument, Lists.list(cert2, cert3));
		
		// expect that document now contains cert1, cert2, cert3, each of them only once
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray certsArray = (COSArray) dssDictionary.getDictionaryObject("Certs");
		assertThat(certsArray.size(), is(3));
		
		// make sure that dss still contains cert1
		try (InputStream in = ((COSStream) certsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert1.getEncoded()));
		}
		// make sure that dss still contains cert2
		try (InputStream in = ((COSStream) certsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert2.getEncoded()));
		}
		
		// make sure that cert3 has been added to dss
		try (InputStream in = ((COSStream) certsArray.get(2)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(cert3.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(certsArray.isNeedToBeUpdated());

	}
	
	@Test
	public void test_toX509Certificate() throws IOException, CertificateException {
		
		COSStream cosStream = new COSStream();
		try (InputStream in = LTVSupportImplTest.class.getResourceAsStream("Max_Mustermann.20210224-20260224.SerNo7C174E16.crt");
				OutputStream out = cosStream.createOutputStream()) {
			IOUtils.copy(in, out);
		}
		
		Optional<X509Certificate> x509Certificate = toX509Certificate(cosStream);
		assertThat(x509Certificate).contains(resourceToCertificate("Max_Mustermann.20210224-20260224.SerNo7C174E16.crt"));
		
	}
	
	@Test
	public void test_toX509Certificate_errorParsingCertificate() throws IOException, CertificateException {
		
		COSStream cosStream = new COSStream();
		try (OutputStream out = cosStream.createOutputStream()) {
			IOUtils.write(new byte[] { 1, 2, 3 }, out);
		}
		
		Optional<X509Certificate> x509Certificate = toX509Certificate(cosStream);
		assertThat(x509Certificate).isEmpty();
		
	}
	
	@Test
	public void test_toX509CRL() throws IOException, CertificateException, CRLException {
		
		COSStream cosStream = new COSStream();
		try (InputStream in = LTVSupportImplTest.class.getResourceAsStream("A-Trust-Root-05.crl");
				OutputStream out = cosStream.createOutputStream()) {
			IOUtils.copy(in, out);
		}
		
		Optional<X509CRL> x509CRL = toX509CRL(cosStream);
		assertThat(x509CRL).contains(resourceToCRL("A-Trust-Root-05.crl"));
		
	}
	
	@Test
	public void test_toX509CRL_errorParsingCRL() throws IOException, CertificateException {
		
		COSStream cosStream = new COSStream();
		try (OutputStream out = cosStream.createOutputStream()) {
			IOUtils.write(new byte[] { 1, 2, 3 }, out);
		}
		
		Optional<X509CRL> x509CRL = toX509CRL(cosStream);
		assertThat(x509CRL).isEmpty();
		
	}
	
	@Test
	public void testAddDSSCRLs_noDssSoFar() throws IOException, CertificateException, CRLException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509CRL crl1 = resourceToCRL("A-Trust-Root-05.crl");
		X509CRL crl2 = resourceToCRL("A-Trust-Root-06.crl");
		
		cut.addDSSCRLs(pdDocument, Lists.list(crl1, crl2));
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray crlsArray = (COSArray) dssDictionary.getDictionaryObject("CRLs");
		assertThat(crlsArray.size(), is(2));
		
		// make sure that clr1 has been added to dss
		try (InputStream in = ((COSStream) crlsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl1.getEncoded()));
		}
		// make sure that clr2 has been added to dss
		try (InputStream in = ((COSStream) crlsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl2.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(crlsArray.isNeedToBeUpdated());
		
	}
	
	@Test
	public void testAddDSSCRLs_dssAlreadyExists() throws IOException, CertificateException, CRLException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509CRL crl1 = resourceToCRL("A-Trust-Root-05.crl");
		X509CRL crl2 = resourceToCRL("A-Trust-Root-06.crl");
		
		cut.addDSSCRLs(pdDocument, Lists.list(crl1, crl2));
		
		// document now contains DSS with two certs
		
		X509CRL crl3 = resourceToCRL("A-Trust-Root-07.crl");
		X509CRL crl4 = resourceToCRL("a-sign-light-07.crl");
		
		cut.addDSSCRLs(pdDocument, Lists.list(crl3, crl4));
		
		// expect that document now contains all four crls
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray crlsArray = (COSArray) dssDictionary.getDictionaryObject("CRLs");
		assertThat(crlsArray.size(), is(4));
		
		// make sure that clr1 has been added to dss
		try (InputStream in = toCOSStream(crlsArray.get(0)).get().createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl1.getEncoded()));
		}
		// make sure that clr2 has been added to dss
		try (InputStream in = toCOSStream(crlsArray.get(1)).get().createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl2.getEncoded()));
		}
		// make sure that clr3 has been added to dss
		try (InputStream in = toCOSStream(crlsArray.get(2)).get().createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl3.getEncoded()));
		}
		// make sure that clr4 has been added to dss
		try (InputStream in = toCOSStream(crlsArray.get(3)).get().createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl4.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(crlsArray.isNeedToBeUpdated());
		
	}
	

	@Test
	public void testAddDSSCRLs_avoidDuplicateCRLs() throws IOException, CertificateException, CRLException {

		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		X509CRL crl1 = resourceToCRL("A-Trust-Root-05.crl");
		X509CRL crl2 = resourceToCRL("A-Trust-Root-06.crl");
		
		// #1, #2
		cut.addDSSCRLs(pdDocument, Lists.list(crl1, crl2));
		
		// document now contains DSS with two crls
		
		X509CRL crl3 = resourceToCRL("A-Trust-Root-07.crl");
		
		// #2, #3
		cut.addDSSCRLs(pdDocument, Lists.list(crl2, crl3));
		
		// expect that document now contains crl1, crl2, crl3, each of them only once
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that crls have been added
		COSArray crlsArray = (COSArray) dssDictionary.getDictionaryObject("CRLs");
		assertThat(crlsArray.size(), is(3));
		
		// make sure that dss still contains crl1
		try (InputStream in = ((COSStream) crlsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl1.getEncoded()));
		}
		// make sure that dss still contains crl2
		try (InputStream in = ((COSStream) crlsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl2.getEncoded()));
		}
		
		// make sure that crl3 has been added to dss
		try (InputStream in = ((COSStream) crlsArray.get(2)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(crl3.getEncoded()));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(crlsArray.isNeedToBeUpdated());

	}

	
	@Nonnull
	private X509Certificate resourceToCertificate(@Nonnull String resourceUri) throws IOException, CertificateException {
		try (InputStream in = LTVSupportImplTest.class.getResourceAsStream(resourceUri)) {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
		}
	}
	
	@Nonnull
	private X509CRL resourceToCRL(@Nonnull String resourceUri) throws IOException, CRLException, CertificateException {
		try (InputStream in = LTVSupportImplTest.class.getResourceAsStream(resourceUri)) {
			return (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(in);
		}
	}
	
	@Test
	public void testAddDSSOCSPs_noDssSoFar() throws IOException, CertificateException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		cut.addDSSOCSPs(pdDocument, Lists.list(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 }));
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray ocspsArray = (COSArray) dssDictionary.getDictionaryObject("OCSPs");
		assertThat(ocspsArray.size(), is(2));
		
		// make sure that ocsp response 1 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 1, 2, 3 }));
		}
		// make sure that ocsp response 2 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 4, 5, 6 }));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(ocspsArray.isNeedToBeUpdated());
		
	}

	@Test
	public void testAddDSSOCSPs_dssAlreadyExists() throws IOException, CertificateException {
		
		PDDocument pdDocument = emptyDocument();
		// document contains no DSS yet
		
		cut.addDSSOCSPs(pdDocument, Lists.list(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 }));
		
		// document now contains DSS with two ocsp responses
		
		cut.addDSSOCSPs(pdDocument, Lists.list(new byte[] { 7, 8, 9 }, new byte[] { 4, 2, 0 }));
		
		// expect that document now contains all four ocsp responses
		
		COSDictionary dssDictionary = (COSDictionary) pdDocument.getDocumentCatalog().getCOSObject().getDictionaryObject("DSS");
		// expect that dss has been created
		assertNotNull(dssDictionary);
		
		// expect that certificates have been added
		COSArray ocspsArray = (COSArray) dssDictionary.getDictionaryObject("OCSPs");
		assertThat(ocspsArray.size(), is(4));
		
		// make sure that ocsp response 1 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(0)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 1, 2, 3 }));
		}
		// make sure that ocsp response 2 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(1)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 4, 5, 6 }));
		}
		// make sure that ocsp response 3 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(2)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 7, 8, 9 }));
		}
		// make sure that ocsp response 4 has been added to dss
		try (InputStream in = ((COSStream) ocspsArray.get(3)).createInputStream()) {
			assertThat(IOUtils.toByteArray(in), is(new byte[] { 4, 2, 0 }));
		}
		
		// make sure objects being modified are marked dirty
		assertTrue(pdDocument.getDocumentCatalog().getCOSObject().isNeedToBeUpdated());
		assertTrue(dssDictionary.isNeedToBeUpdated());
		assertTrue(ocspsArray.isNeedToBeUpdated());
		
	}

	@Nonnull
	private PDDocument emptyDocument() {
		PDDocument pdDocument = new PDDocument();
		// You need to add at least one page for the document to be valid.
		PDPage page = new PDPage();
		pdDocument.addPage(page);
		return pdDocument;
	}
	
}
