package at.gv.egiz.param_tests.testinfo;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.preflight.ValidationResult;

/**
 * Test information class for PDF-A conformance tests.
 * 
 * @author mtappler
 *
 */
public class PDFATestInfo extends TestInfo {

    /**
     * the validation result before signing for the input PDF file
     */
    private Pair<ValidationResult, Throwable> resultBeforeSign;
    /**
     * the validation result after signing for the output PDF file
     */
    private Pair<ValidationResult, Throwable> resultAfterSign;

    public Pair<ValidationResult, Throwable> getResultBeforeSign() {
        return resultBeforeSign;
    }

    public void setResultBeforeSign(
            Pair<ValidationResult, Throwable> resultBeforeSign) {
        this.resultBeforeSign = resultBeforeSign;
    }

    public Pair<ValidationResult, Throwable> getResultAfterSign() {
        return resultAfterSign;
    }

    public void setResultAfterSign(
            Pair<ValidationResult, Throwable> resultAfterSign) {
        this.resultAfterSign = resultAfterSign;
    }

}
