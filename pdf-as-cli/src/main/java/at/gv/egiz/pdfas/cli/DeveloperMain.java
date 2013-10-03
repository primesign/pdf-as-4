package at.gv.egiz.pdfas.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSink;
import at.gv.egiz.pdfas.lib.api.ByteArrayDataSource;
import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.PdfAs;
import at.gv.egiz.pdfas.lib.api.PdfAsFactory;
import at.gv.egiz.pdfas.lib.api.StatusRequest;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.api.sign.SignParameter;
import at.gv.egiz.pdfas.lib.api.verify.VerifyParameter;
import at.gv.egiz.pdfas.lib.impl.VerifyParameterImpl;
import at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedSigner;

public class DeveloperMain {

	public static final String keyStoreFile = "/home/afitzek/devel/pdfas_neu/test.p12";
	public static final String keyStoreType = "PKCS12";
    public static final String keyStorePass = "123456";
    //public static final String keyAlias = "pdf";
    public static final String keyAlias = "ecc_test";
    public static final String keyPass = "123456";
	
	public static void main(String[] args) {		
		String user_home = System.getProperty("user.home");
		String pdfas_dir = user_home + File.separator + "PDF-AS";
		PdfAs pdfas = PdfAsFactory.createPdfAs(new File(pdfas_dir));
		Configuration config = pdfas.getConfiguration();
		byte[] data;
		try {
			IPlainSigner signer = new PKCS7DetachedSigner(keyStoreFile, keyAlias, keyStorePass, keyPass, keyStoreType);
			data = StreamUtils.inputStreamToByteArray(new FileInputStream("/home/afitzek/devel/pdfas_neu/simple.pdf"));
			SignParameter parameter = PdfAsFactory.createSignParameter(config, new ByteArrayDataSource(data));
			ByteArrayDataSink bads = new ByteArrayDataSink();
			parameter.setSignatureProfileId("SIGNATURBLOCK_DE_NEU");
			parameter.setOutput(bads);
			parameter.setPlainSigner(signer);

			StatusRequest request = pdfas.startSign(parameter);
			
			if(request.needCertificate()) {
				request.setCertificate(signer.getCertificate().getEncoded());
			} else {
				throw new Exception("Invalid status");
			}
			
			request = pdfas.process(request);
			
			if(request.needSignature()) {
				FileOutputStream fos2 = new FileOutputStream("/home/afitzek/devel/pdfas_neu/sign1.pdf");
				fos2.write(request.getSignatureData());
				fos2.close();
				request.setSigature(signer.sign(request.getSignatureData()));
			} else {
				throw new Exception("Invalid status");
			}
			
			request = pdfas.process(request);
			
			if(request.isReady()) {
				pdfas.finishSign(request);
			} else {
				throw new Exception("Invalid status");
			}
			
			pdfas.sign(parameter);
			FileOutputStream fos = new FileOutputStream("/home/afitzek/devel/pdfas_neu/simple_out.pdf");
			fos.write(bads.getData());
			fos.close();
			
			VerifyParameter verify = new VerifyParameterImpl(config, new ByteArrayDataSource(bads.getData()));
			pdfas.verify(verify);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (PdfAsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
