package at.gv.egiz.param_tests.serialization.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map.Entry;

import at.gv.egiz.param_tests.provider.BaseSignatureTestData;
import at.gv.egiz.param_tests.serialization.TestInfoSerializer;
import at.gv.egiz.param_tests.testinfo.TestInfo;
import at.gv.egiz.param_tests.testinfo.TestVerdict;

/**
 * A subclass implementing some methods, which can be implemented independent of
 * concrete test type. It uses the Twitter-bootstrap framework, which must be
 * provided for the files to be correctly displayed in a browser.
 * 
 * @author mtappler
 *
 * @param <T>
 *            the type of TestInfo which instance of this class, respectively
 *            subclasses of it can serialize
 */
public abstract class HTMLSerializer<T extends TestInfo> extends
        TestInfoSerializer<T> {

    @Override
    public String fileEnding() {
        return "html";
    }

    @Override
    protected void writeHeader(PrintWriter pw) {
        BaseSignatureTestData baseData = baseTestInfo.getBaseTestData();
        pw.println("<!doctype html>");
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>Test results for " + baseData.getTestName()
                + "</title>");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/bootstrap.min.css\">");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/bootstrap-theme.min.css\">");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<div class=\"container\">");
        pw.println("<div class=\"page-header\">");
        pw.println("<h1>Detailed test results <small>" + baseData.getTestName()
                + "</small></h1>");
        pw.println("</div>");
        String basicTestDataPanel = createBasicTestData();
        writePanel(pw, "Basic test data", basicTestDataPanel);
    }

    /**
     * Helper method which creates three tabs, one for basic test parameter, one
     * for the standard output and one for standard error.
     * 
     * @return HTML-string defining the tabs
     */
    protected String createBasicTestData() {
        BaseSignatureTestData baseData = baseTestInfo.getBaseTestData();
        String basicParameterBody = createBasicParameterRepresentation(baseData);
        StringBuilder basicTestDataPanel = new StringBuilder();
        basicTestDataPanel
                .append("<ul id=\"tabs\" class=\"nav nav-tabs\" data-tabs=\"tabs\">");
        basicTestDataPanel
                .append("<li class=\"active\"><a href=\"#basic-parameters\" "
                        + "data-toggle=\"tab\">Basic parameters</a></li>");
        basicTestDataPanel
                .append("<li><a href=\"#std-out\" data-toggle=\"tab\">Standard output</a></li>");
        basicTestDataPanel
                .append("<li><a href=\"#std-err\" data-toggle=\"tab\">Standard error</a></li>");
        basicTestDataPanel.append("</ul>");
        basicTestDataPanel
                .append("<div id=\"my-tab-content\" class=\"tab-content\">");
        basicTestDataPanel
                .append("<div class=\"tab-pane active\" id=\"basic-parameters\">");
        basicTestDataPanel.append(basicParameterBody);
        basicTestDataPanel.append("</div>");
        basicTestDataPanel.append("<div class=\"tab-pane\" id=\"std-out\">");
        basicTestDataPanel.append("<pre class=\"pre-scrollable\">"
                + baseTestInfo.getStdOut() + "</pre>");
        basicTestDataPanel.append("</div>");
        basicTestDataPanel.append("<div class=\"tab-pane\" id=\"std-err\">");
        basicTestDataPanel.append("<pre class=\"pre-scrollable\">"
                + baseTestInfo.getStdErr() + "</pre>");
        basicTestDataPanel.append("</div>");
        basicTestDataPanel.append("</div>");
        return basicTestDataPanel.toString();
    }

    /**
     * This method creates a definition list for basic parameters of a test.
     * 
     * @param baseData
     *            basic test parameters
     * @return HTML-string defining a definition list
     */
    private String createBasicParameterRepresentation(
            BaseSignatureTestData baseData) {
        StringBuilder sb = new StringBuilder();
        sb.append("<dl>");
        sb.append(createDescription("Input file", baseData.getPdfFile()));
        sb.append(createDescription("Output file", baseData.getOutputFile()));
        sb.append(createDescription("Signature profile", baseData.getProfilID()));
        sb.append(createDescription("Connector Type", baseData
                .getConnectorData().getConnectorType()));
        if (baseData.getConnectorData().getConnectorParameters().size() > 0) {
            StringBuilder connectorParameters = new StringBuilder();
            connectorParameters.append("<dl class=\"dl-horizontal\">");
            for (Entry<String, String> e : baseData.getConnectorData()
                    .getConnectorParameters().entrySet())
                connectorParameters.append(createDescription(e.getKey(),
                        e.getValue()));
            connectorParameters.append("</dl>");
            sb.append(createDescription("Connector Parameters",
                    connectorParameters.toString()));
        }
        sb.append(createDescription("Test Type", getTestType()));
        sb.append("</dl>");
        return sb.toString();
    }

    /**
     * Helper for writing a bootstrap panel with some title and content.
     * 
     * @param pw
     *            <code>PrintWriter</code>-object to which the panel should be
     *            written
     * @param panelTitle
     *            title of the panel
     * @param panelBody
     *            panel content
     */
    protected void writePanel(PrintWriter pw, String panelTitle,
            String panelBody) {
        pw.println("<div class=\"panel panel-default\">");
        pw.println(" <div class=\"panel-heading\">");
        pw.println("<h3 class=\"panel-title\">" + panelTitle + "</h3>");
        pw.println("</div>");
        pw.println("<div class=\"panel-body\">");
        pw.println(panelBody);
        pw.println("</div>");
        pw.println("</div>");
    }

    @Override
    protected void writeTestResult(PrintWriter pw) {
        StringBuilder panelBody = new StringBuilder();
        panelBody.append("<h4>"
                + HTMLTestSummaryWriter.verdictToLabel(baseTestInfo
                        .getVerdict()) + "</h4>");
        if (baseTestInfo.getVerdict().equals(TestVerdict.FAILED)
                || baseTestInfo.getVerdict().equals(TestVerdict.INCONCLUSIVE)) {
            panelBody.append(createExceptionDataString(baseTestInfo
                    .getFailCause()));
        }
        writePanel(pw, "Test result", panelBody.toString());

    }

    /**
     * This method creates a HTML-representation for data contained in a
     * throwable.
     * 
     * @param t
     *            <code>Throwable</code>-object which should be displayed
     * @return HTML-string for the throwable
     */
    protected String createExceptionDataString(Throwable t) {
        StringBuilder exceptionData = new StringBuilder();
        exceptionData.append("<dl class=\"dl-horizontal\">");
        exceptionData.append(createDescription("Cause", t.toString()));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        exceptionData.append(createDescription("Stacktrace", sw.toString()));
        exceptionData.append("</dl>");
        return exceptionData.toString();
    }

    /**
     * Helper method for creating an item of a definition list.
     * 
     * @param term
     *            the term of the item (the key)
     * @param definition
     *            the definition of the item (the value)
     * @return an HTML-string for the definition list item
     */
    protected String createDescription(String term, String definition) {
        return String.format("<dt>%s</dt><dd>%s</dd>%n", term, definition);
    }

    @Override
    protected void writeFooter(PrintWriter pw) {
        pw.println("<a href=\"../index.html\">Back to summary</a>");
        pw.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\">"
                + "</script>");
        pw.println("<script src=\"../js/bootstrap.min.js\"></script>");
        pw.println("</div>");
        pw.println("</body>");
        pw.println("</html>");
    }
}
