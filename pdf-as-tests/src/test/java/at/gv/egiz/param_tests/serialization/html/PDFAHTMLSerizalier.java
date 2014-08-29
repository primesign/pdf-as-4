package at.gv.egiz.param_tests.serialization.html;

import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;

import at.gv.egiz.param_tests.serialization.TestInfoSerializer;
import at.gv.egiz.param_tests.testinfo.PDFATestInfo;

/**
 * Concrete test information serializer for PDF-A conformance tests using HTML
 * and Twitter-bootstrap.
 * 
 * @author mtappler
 *
 */
public class PDFAHTMLSerizalier extends HTMLSerializer<PDFATestInfo> {

    /**
     * the test information object which is serialized, it is the same as
     * <code>TestInfoSerializer.baseTestInfo</code>
     */
    private PDFATestInfo testInfo;

    /**
     * Default contructor used for registering it as prototype.
     */
    public PDFAHTMLSerizalier() {
        this(null);
    }

    /**
     * Package protected constructor used for cloning
     * 
     * @param testInfo
     */
    PDFAHTMLSerizalier(PDFATestInfo testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public PDFATestInfo createTestInfo() {
        testInfo = new PDFATestInfo();
        baseTestInfo = testInfo;
        return testInfo;
    }

    @Override
    public TestInfoSerializer<PDFATestInfo> cloneSerializer() {
        return new PDFAHTMLSerizalier(testInfo);
    }

    @Override
    protected void writeTestData(PrintWriter pw) {
        pw.println("<h2>Validation status before signing</h2>");
        writeValidationResult(pw, testInfo.getResultBeforeSign());
        if (testInfo.getResultAfterSign() != null) {
            pw.println("<h2>Validation status after signing</h2>");
            writeValidationResult(pw, testInfo.getResultAfterSign());
        }
    }

    /**
     * This method writes the validation result to the given print writer, i.e.
     * an information about the success of the validation or an exception which
     * was thrown during the validation or a list of validation errors.
     * 
     * @param pw
     *            the <code>PrintWriter</code>-object to which the HTML-code is
     *            written
     * @param validationResult
     *            the pair which defines the result of a validation
     */
    private void writeValidationResult(PrintWriter pw,
            Pair<ValidationResult, Throwable> validationResult) {
        if (validationResult.getRight() != null) {
            StringBuilder exceptionString = new StringBuilder();
            exceptionString
                    .append("p>An exception happened during the validation process:</p>");
            exceptionString.append(createExceptionDataString(validationResult
                    .getRight()));
            writePanel(pw, "Validation exception details",
                    exceptionString.toString());
        }
        if (validationResult.getLeft() != null) {

            StringBuilder conformanceString = new StringBuilder();
            if (validationResult.getLeft().isValid()) {
                conformanceString
                        .append("<p>The document conforms to the PDF-A standard.</p>");
            } else {
                List<ValidationError> errors = validationResult.getLeft()
                        .getErrorsList();
                conformanceString.append("<p>With to the PDF-A standard, "
                        + "the document contains the following errors:</p>");
                conformanceString
                        .append("<table class=\"table table-bordered table-striped\">");
                conformanceString.append("<thead>");
                conformanceString.append("<tr>");
                conformanceString.append("<th>Error code</th>");
                conformanceString.append("<th>Error details</th>");
                conformanceString.append("<th>Warning</th>");
                conformanceString.append("</tr>");
                conformanceString.append("</thead>");
                for (ValidationError error : errors) {
                    writeValidationError(conformanceString, error);
                }
                conformanceString.append("</table>");
            }
            writePanel(pw, "PDF-A conformance", conformanceString.toString());
        }
    }

    /**
     * Helper method for writing a table line for a single validation error.
     * 
     * @param conformanceString
     *            the string builder to which the table line is appended
     * @param error
     *            the error which should be serialized
     */
    private void writeValidationError(StringBuilder conformanceString,
            ValidationError error) {
        conformanceString.append("<tr>");
        conformanceString.append(String.format("<td>%s</td>",
                error.getErrorCode()));
        conformanceString.append(String.format("<td>%s</td>",
                error.getDetails()));
        conformanceString.append(String.format("<td>%s</td>",
                error.isWarning() ? "x" : ""));
        conformanceString.append("</tr>");
    }

    @Override
    public String getTestType() {
        return "PDF-A";
    }

    @Override
    protected void writeTestSpecificParameters(PrintWriter pw) {
        // intentionally left blank, no pdf-a specific parameters
    }

}
