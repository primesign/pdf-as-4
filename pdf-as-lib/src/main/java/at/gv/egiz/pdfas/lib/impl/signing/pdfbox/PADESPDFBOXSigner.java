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
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.common.exceptions.PdfAsException;
import at.gv.egiz.pdfas.common.messages.MessageResolver;
import at.gv.egiz.pdfas.common.settings.SignatureProfileSettings;
import at.gv.egiz.pdfas.common.utils.StreamUtils;
import at.gv.egiz.pdfas.common.utils.TempFileHelper;
import at.gv.egiz.pdfas.lib.api.sign.IPlainSigner;
import at.gv.egiz.pdfas.lib.impl.signing.IPdfSigner;
import at.gv.egiz.pdfas.lib.impl.signing.sig_interface.PDFASSignatureInterface;
import at.gv.egiz.pdfas.lib.impl.stamping.TableFactory;
import at.gv.egiz.pdfas.lib.impl.stamping.ValueResolver;
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
            //signature.setLocation("signer location");
            signature.setReason("PDF-AS Signatur");


            logger.debug("Signing @ " + signer.getSigningDate().getTime().toString());
            // the signing date, needed for valid signature
            signature.setSignDate(signer.getSigningDate());

            signer.setPDSignature(signature);
            
            doc.addSignature(signature, signer);

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


    public void signPDF(String src, String dst, SignatureInterface signer) throws Exception {
        //ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(new File(src));
        FileOutputStream fos = new FileOutputStream(new File(dst));
        byte[] buffer = new byte[8 * 1024];
        byte[] outbuffer;
        int c;
        while ((c = fis.read(buffer)) != -1)
        {
            fos.write(buffer, 0, c);
        }
        fis.close();
        PDDocument doc = PDDocument.load(src);
        fis = new FileInputStream(new File(dst));

        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE); // default filter

        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName("Andraes Fitzek");
        signature.setLocation("signer location");
        signature.setReason("Test Signature");

        // the signing date, needed for valid signature
        signature.setSignDate(Calendar.getInstance());

        doc.addSignature(signature, signer);

        // pdfbox patched (FIS -> IS)
        doc.saveIncremental(fis, fos);

        fos.close();
       // FileUtils.writeByteArrayToFile(new File(dst), os.toByteArray());
    }

}
