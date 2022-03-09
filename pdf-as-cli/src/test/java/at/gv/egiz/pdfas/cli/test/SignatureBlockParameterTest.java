/*******************************************************************************
 * <copyright> Copyright 2014 by E-Government Innovation Center EGIZ, Graz, Austria </copyright>
 * PDF-AS has been contracted by the E-Government Innovation Center EGIZ, a
 * joint initiative of the Federal Chancellery Austria and Graz University of
 * Technology.
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This product combines work with different licenses. See the "NOTICE" text
 * file for details on the various modules and licenses.
 * The "NOTICE" text file is part of the distribution. Any derivative works
 * that you distribute must include a readable copy of the "NOTICE" text file.
 ******************************************************************************/
package at.gv.egiz.pdfas.cli.test;

import at.gv.egiz.pdfas.common.exceptions.PDFASError;
import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.sign.SignResult;
import at.gv.egiz.pdfas.sigs.pades.PAdESSignerKeystore;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.junit.Assert;
import org.junit.Test;

import javax.activation.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignatureBlockParameterTest {

  public static final String KS_ALIAS = "ecc_test";
  public static final String KS_TYPE = "PKCS12";
  public static final String KS_PASS = "123456";
  public static final String KS_KEY_PASS = "123456";

  public String getPath(String resourceName) {
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(resourceName).getFile());
    String absolutePath = file.getAbsolutePath();

    System.out.println(absolutePath);
    return absolutePath;
  }

  @Test(expected = PdfAsException.class)
  public void invalid() throws IOException, PdfAsException, PDFASError {

    PdfAs pdfas = PdfAsFactory.createPdfAs(new File(getPath("pdfas-config")));
    Configuration config = pdfas.getConfiguration();

    byte[] input = IOUtils.toByteArray(new FileInputStream(getPath("simple_rotated_0.pdf")));

    IPlainSigner signer = new PAdESSignerKeystore(getPath("test.p12"), KS_ALIAS, KS_PASS, KS_KEY_PASS, KS_TYPE);

    String profile = "SIGNATURBLOCK_DE_NOTE_DYNAMIC";
    System.out.println("Testing " + profile);

    DataSource source = new ByteArrayDataSource(input);
    String outFile = getPath("out") + "/" + profile + ".pdf";
    FileOutputStream fos = new FileOutputStream(outFile);
    SignParameter signParameter = PdfAsFactory.createSignParameter(
        config, source, fos);

    Map<String, String> map = new HashMap<>();
    map.put("subject", "TEST123");
    map.put("WAY_TOOOOOOOOOOOOOOOOOOOOO_LONG_KEY", "bar");
    signParameter.setDynamicSignatureBlockArguments(map);
//    signParameter.setPlainSigner(signer);
//    signParameter.setSignatureProfileId(profile);
//
//    SignResult result = pdfas.sign(signParameter);

    fos.close();

  }

  @Test
  public void test() throws IOException, PDFASError, PdfAsException {

    PdfAs pdfas = PdfAsFactory.createPdfAs(new File(getPath("pdfas-config")));
    Configuration config = pdfas.getConfiguration();

    byte[] input = IOUtils.toByteArray(new FileInputStream(getPath("simple_rotated_0.pdf")));

    IPlainSigner signer = new PAdESSignerKeystore(getPath("test.p12"), KS_ALIAS, KS_PASS, KS_KEY_PASS, KS_TYPE);

    String profile = "SIGNATURBLOCK_DE_NOTE_DYNAMIC";
    System.out.println("Testing " + profile);

    DataSource source = new ByteArrayDataSource(input);
    String outFile = getPath("out") + "/" + profile + ".pdf";
    FileOutputStream fos = new FileOutputStream(outFile);
    SignParameter signParameter = PdfAsFactory.createSignParameter(
        config, source, fos);

    Map<String, String> map = new HashMap<>();
    map.put("subject", "TEST123");
    map.put("foo", "bar");
    signParameter.setDynamicSignatureBlockArguments(map);
    signParameter.setPlainSigner(signer);
    signParameter.setSignatureProfileId(profile);

    SignResult result = pdfas.sign(signParameter);

    fos.close();
    String name = getName(outFile, "PDF-AS Signatur1");
    Assert.assertEquals("TEST123 test bar 123 c TEST123 Andreas Fitzek ECC", name);


		outFile = getPath("out") + "/" + profile + "-1.pdf";
    fos = new FileOutputStream(outFile);
    signParameter = PdfAsFactory.createSignParameter(
				config, source, fos);

		map = new HashMap<>();
		map.put("subject", "TEST123");
		signParameter.setDynamicSignatureBlockArguments(map);
		signParameter.setPlainSigner(signer);
		signParameter.setSignatureProfileId(profile);

		result = pdfas.sign(signParameter);

		fos.close();
		name = getName(outFile, "PDF-AS Signatur1");
		Assert.assertEquals("TEST123 test null 123 c TEST123 Andreas Fitzek ECC", name);

		outFile = getPath("out") + "/" + profile + "-2.pdf";
		fos = new FileOutputStream(outFile);
		signParameter = PdfAsFactory.createSignParameter(
				config, source, fos);
		map = new HashMap<>();
		map.put("foo", "bar");
		signParameter.setDynamicSignatureBlockArguments(map);
		signParameter.setPlainSigner(signer);
		signParameter.setSignatureProfileId(profile);
		result = pdfas.sign(signParameter);
		fos.close();
		name = getName(outFile, "PDF-AS Signatur1");
		Assert.assertEquals("null test bar 123 c null Andreas Fitzek ECC", name);

		outFile = getPath("out") + "/" + profile + "-3.pdf";
		fos = new FileOutputStream(outFile);
		signParameter = PdfAsFactory.createSignParameter(
				config, source, fos);

		signParameter.setPlainSigner(signer);
		signParameter.setSignatureProfileId(profile);
		result = pdfas.sign(signParameter);
		fos.close();
		name = getName(outFile, "PDF-AS Signatur1");
    Assert.assertEquals("null test null 123 c null Andreas Fitzek ECC", name);
//		Assert.assertEquals("{sbp.subject} test {sbp.foo} 123 {subject.T != null ? (subject.T + \" a \"+sbp.subject) : " +
//				"\"c \"+sbp.subject+\" \"}Andreas Fitzek ECC", name);
//TODO was ist gewünscht?


		outFile = getPath("out") + "/" + profile + "-4.pdf";
		fos = new FileOutputStream(outFile);
		signParameter = PdfAsFactory.createSignParameter(
				config, source, fos);
		map = new HashMap<>();
		signParameter.setDynamicSignatureBlockArguments(map);
		signParameter.setPlainSigner(signer);
		signParameter.setSignatureProfileId(profile);
		result = pdfas.sign(signParameter);
		fos.close();
		name = getName(outFile, "PDF-AS Signatur1");
		Assert.assertEquals("null test null 123 c null Andreas Fitzek ECC", name);


	}

  @Test
  public void testWithUmlaute() throws IOException, PDFASError, PdfAsException {

    PdfAs pdfas = PdfAsFactory.createPdfAs(new File(getPath("pdfas-config")));
    Configuration config = pdfas.getConfiguration();


    byte[] input = IOUtils.toByteArray(new FileInputStream(getPath("simple_rotated_0.pdf")));

    IPlainSigner signer = new PAdESSignerKeystore(getPath("test.p12"), KS_ALIAS, KS_PASS, KS_KEY_PASS, KS_TYPE);

    String profile = "SIGNATURBLOCK_DE_NOTE_DYNAMIC";
    System.out.println("Testing " + profile);

    DataSource source = new ByteArrayDataSource(input);
    String outFile = getPath("out") + "/" + profile + "-umlaute.pdf";
    FileOutputStream fos = new FileOutputStream(outFile);
    SignParameter signParameter = PdfAsFactory.createSignParameter(
        config, source, fos);

    Map<String, String> map = new HashMap<>();
    map.put("subject", "TEST123");
    map.put("foo", "baräöÜ");
    signParameter.setDynamicSignatureBlockArguments(map);
    signParameter.setPlainSigner(signer);
    signParameter.setSignatureProfileId(profile);

    SignResult result = pdfas.sign(signParameter);

    fos.close();
    String name = getName(outFile, "PDF-AS Signatur1");
    Assert.assertEquals("TEST123 test baräöÜ 123 c TEST123 Andreas Fitzek ECC", name);
    //expected:<TEST123 test bar[] 123 c TEST123 Andre...> but was:<TEST123 test bar[äöÜ] 123 c TEST123 Andre...>
  }


  @Test
  public void testWithTextAfterVariable() throws IOException, PDFASError, PdfAsException {

    PdfAs pdfas = PdfAsFactory.createPdfAs(new File(getPath("pdfas-config")));
    Configuration config = pdfas.getConfiguration();


    byte[] input = IOUtils.toByteArray(new FileInputStream(getPath("simple_rotated_0.pdf")));

    IPlainSigner signer = new PAdESSignerKeystore(getPath("test.p12"), KS_ALIAS, KS_PASS, KS_KEY_PASS, KS_TYPE);

    String profile = "SIGNATURBLOCK_DE_NOTE_DYNAMIC_WITH_TEXT_AFTER_VARIABLE";
    System.out.println("Testing " + profile);

    DataSource source = new ByteArrayDataSource(input);
    String outFile = getPath("out") + "/" + profile + "-umlaute.pdf";
    FileOutputStream fos = new FileOutputStream(outFile);
    SignParameter signParameter = PdfAsFactory.createSignParameter(
        config, source, fos);

    Map<String, String> map = new HashMap<>();
    map.put("subject", "TEST123");
    map.put("foo", "baräöÜ");
    signParameter.setDynamicSignatureBlockArguments(map);
    signParameter.setPlainSigner(signer);
    signParameter.setSignatureProfileId(profile);

    SignResult result = pdfas.sign(signParameter);

    fos.close();
    String name = getName(outFile, "PDF-AS Signatur1");
    Assert.assertEquals("Andreas Fitzek ECC text after variable", name);
    //expected:<TEST123 test bar[] 123 c TEST123 Andre...> but was:<TEST123 test bar[äöÜ] 123 c TEST123 Andre...>
  }

  private String getName(String fileName, String sigFieldName) throws IOException {
    PDDocument pdDoc = PDDocument.load(new File(fileName));
    PDSignature signature = null;
    PDSignatureField signatureField;
    PDAcroForm acroForm = pdDoc.getDocumentCatalog().getAcroForm();
    if (acroForm != null) {
      List<PDField> aa = acroForm.getFields();
      signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
      if (signatureField != null) {
        // retrieve signature dictionary
        signature = signatureField.getSignature();
        if (signature != null) {
          String name = signature.getName();
          return name;

        }

      }
    }
    return null;
  }

}
