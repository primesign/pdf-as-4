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
package at.gv.egiz.pdfas.lib.impl.signing.pdfbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.MessageResolver;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.TempFileHelper;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.ValueResolver;
import at.gv.egiz.pdfas.lib.impl.stamping.pdfbox.PDFAsVisualSignatureProperties;
import at.gv.egiz.pdfas.lib.impl.status.PDFObject;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

public class PADESPDFBOXSigner implements IPdfSigner {

    private static final Logger logger = LoggerFactory.getLogger(PADESPDFBOXSigner.class);

    public void signPDF(PDFObject pdfObject, RequestedSignature requestedSignature, 
    		PDFASSignatureInterface signer)
            throws PdfAsException {
        String fisTmpFile = null;
        
        TempFileHelper helper = pdfObject.getStatus().getTempFileHelper();
        
        try {
            fisTmpFile = helper.getStaticFilename();

            // write to temporary file
            FileOutputStream fos = new FileOutputStream(new File(fisTmpFile));
            fos.write(pdfObject.getStampedDocument());


            FileInputStream fis = new FileInputStream(new File(fisTmpFile));

            PDDocument doc = PDDocument.load(
                    new ByteArrayInputStream(pdfObject.getStampedDocument()));

            PDSignature signature = new PDSignature();
            signature.setFilter(COSName.getPDFName(signer.getPDFFilter())); // default filter
            signature.setSubFilter(COSName.getPDFName(signer.getPDFSubFilter()));

            SignatureProfileSettings signatureProfileSettings = TableFactory
					.createProfile(requestedSignature.getSignatureProfileID(), 
							pdfObject.getStatus().getSettings());
            
            ValueResolver resolver = new ValueResolver();
            String signerName = resolver.resolve("SIG_SUBJECT", signatureProfileSettings.getValue("SIG_SUBJECT"), 
            		signatureProfileSettings, requestedSignature);
            
            signature.setName(signerName);
            signature.setSignDate(Calendar.getInstance());
            String signerReason = signatureProfileSettings.getSigningReason();
            
            if(signerReason == null) {
            	signerReason = "PAdES Signature";
            }
            
            signature.setReason(signerReason);
            logger.debug("Signing reason: " + signerReason);

            logger.debug("Signing @ " + signer.getSigningDate().getTime().toString());
            // the signing date, needed for valid signature
            //signature.setSignDate(signer.getSigningDate());

            signer.setPDSignature(signature);
            SignatureOptions options = new SignatureOptions();
            
            // FOR DEVELOPING: Call custom visual signature creation
            PDFAsVisualSignatureProperties properties = new PDFAsVisualSignatureProperties(
            		pdfObject.getStatus().getSettings(), pdfObject);
            properties.buildSignature();
            
            ByteArrayOutputStream sigbos = new ByteArrayOutputStream();
            sigbos.write(StreamUtils.inputStreamToByteArray(properties.getVisibleSignature()));
            sigbos.close();
            
            FileOutputStream fos2 = new FileOutputStream("/tmp/apsig.pdf");
            fos2.write(sigbos.toByteArray());
            fos2.close();
            
            options.setVisualSignature(new ByteArrayInputStream(sigbos.toByteArray()));
            
            doc.addSignature(signature, signer, options);

            // pdfbox patched (FIS -> IS)
            doc.saveIncremental(fis, fos);
            fis.close();
            fos.close();

            fis = new FileInputStream(new File(fisTmpFile));

            // write to resulting output stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(StreamUtils.inputStreamToByteArray(fis));
            fis.close();
            bos.close();

            pdfObject.setSignedDocument(bos.toByteArray());

            helper.deleteFile(fisTmpFile);

        } catch (IOException e) {
            logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
            throw new PdfAsException("error.pdf.sig.01", e);
        } catch(SignatureException e) {
            logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
            throw new PdfAsException("error.pdf.sig.01", e);
        } catch (COSVisitorException e) {
            logger.error(MessageResolver.resolveMessage("error.pdf.sig.01"), e);
            throw new PdfAsException("error.pdf.sig.01", e);
        }
    }
}
