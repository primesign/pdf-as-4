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
import at.gv.egiz.pdfas.lib.impl.signing.pdfbox.PADESPDFBOXSigner;
import at.gv.egiz.pdfas.sigs.pades.PAdESSigner;
import at.gv.egiz.pdfas.sigs.pkcs7detached.PKCS7DetachedSigner;
import at.gv.egiz.sl.util.BKUSLConnector;
import at.gv.egiz.sl.util.MOAConnector;

public class DeveloperMain {

	public static final String keyStoreFile = "/home/afitzek/devel/pdfas_neu/test.p12";
	public static final String keyStoreType = "PKCS12";
    public static final String keyStorePass = "123456";
    //public static final String keyAlias = "pdf";
    public static final String keyAlias = "ecc_test";
    public static final String keyPass = "123456";
	
	public static void main(String[] args) {		
		
		
		
		String user_home = System.getProperty("user.home");
		String pdfas_dir = user_home + File.separator + ".pdfas";
		PdfAs pdfas = PdfAsFactory.createPdfAs(new File(pdfas_dir));
		System.out.println(PdfAsFactory.getVersion());
		return;
		
		/*Configuration config = pdfas.getConfiguration();
		byte[] data;
		try {
			IPlainSigner signer = new PKCS7DetachedSigner(keyStoreFile, keyAlias, keyStorePass, keyPass, keyStoreType);
			data = StreamUtils.inputStreamToByteArray(new FileInputStream("/home/afitzek/simple.pdf"));
			SignParameter parameter = PdfAsFactory.createSignParameter(config, new ByteArrayDataSource(data));
			ByteArrayDataSink bads = new ByteArrayDataSink();
			parameter.setSignatureProfileId("AMTSSIGNATURBLOCK_DE");
			parameter.setOutput(bads);
			//parameter.setPlainSigner(new PAdESSigner(new BKUSLConnector(config)));
			//parameter.setPlainSigner(signer);
			parameter.setPlainSigner(new PAdESSigner(new MOAConnector(config)));
			/*
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
				request.setSigature(signer.sign(request.getSignatureData(), request.getSignatureDataByteRange()));
			} else {
				throw new Exception("Invalid status");
			}
			
			request = pdfas.process(request);
			
			if(request.isReady()) {
				pdfas.finishSign(request);
			} else {
				throw new Exception("Invalid status");
			}
			/
			pdfas.sign(parameter);
			FileOutputStream fos = new FileOutputStream("/home/afitzek/simple_signed.pdf");
			fos.write(bads.getData());
			fos.close();
			
			VerifyParameter verify = new VerifyParameterImpl(config, new ByteArrayDataSource(bads.getData()));
			pdfas.verify(verify);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}catch (PdfAsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

}
