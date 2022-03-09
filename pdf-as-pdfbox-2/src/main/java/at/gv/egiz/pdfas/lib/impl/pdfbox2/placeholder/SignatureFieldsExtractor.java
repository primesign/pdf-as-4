package at.gv.egiz.pdfas.lib.impl.pdfbox2.placeholder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

import java.util.ArrayList;
import java.util.List;

public class SignatureFieldsExtractor {

    //Search for empty signature fields
    public static List<String> findEmptySignatureFields(PDDocument doc)
    {
        PDSignature signature;
        List<PDField> signatureField;
        List<String> signatureFieldNames = new ArrayList<>();
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
        if (acroForm != null) {
            signatureField = acroForm.getFields();
            for (PDField pdField : signatureField) {
                if(pdField instanceof PDSignatureField && pdField.getPartialName()!=null)
                {
                    signature = ((PDSignatureField) pdField).getSignature();
                    if(signature == null) signatureFieldNames.add(pdField.getPartialName());
                }
            }
        }
        return signatureFieldNames;
    }
}
