package at.gv.egiz.param_tests.serialization.html;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.param_tests.serialization.TestSummaryWriter;
import at.gv.egiz.param_tests.testinfo.TestInfo;
import at.gv.egiz.param_tests.testinfo.TestVerdict;

/**
 * Concrete implementation of the <code>TestSummaryWriter</code>, which creates
 * HTML output and uses the Twitter-bootstrap framework.
 * 
 * @author mtappler
 *
 */
public class HTMLTestSummaryWriter implements TestSummaryWriter {

    /**
     * the location of the test directory
     */
    private String testDir;
    /**
     * the print writer which is used for writing
     */
    private PrintWriter pw;
    /**
     * the logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(HTMLTestSummaryWriter.class);

    /**
     * Constructor which sets the test directory.
     * 
     * @param testDir
     *            location of the test directory
     */
    public HTMLTestSummaryWriter(String testDir) {
        this.testDir = testDir;
    }

    public void writeHeader() {
        if (pw == null)
            return;
        pw.println("<!doctype html>");
        pw.println("<html>");
        pw.println("<head>");
        pw.println("<title>Summary of test results</title>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/bootstrap.min.css\">");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/bootstrap-theme.min.css\">");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<div class=\"container\">");
        pw.println("<div class=\"page-header\">");
        pw.println("<h1>Test result summary</h1>");
        pw.println("</div>");
        pw.println("<table class=\"table table-bordered table-striped\">");
        pw.println("<thead>");
        pw.println("<tr>");
        pw.println("<th>Test name</th>");
        pw.println("<th>Test directory</th>");
        pw.println("<th>Test type</th>");
        pw.println("<th>Verdict</th>");
        pw.println("</tr>");
        pw.println("</thead>");

    }

    public void writeSummaryOfTest(TestInfo tInfo, String testType) {
        if (pw == null)
            return;
        pw.println("<tr>");
        pw.println(String.format(
                "<td><a href=\"%s/test_result.html\">%s</a></td>", tInfo
                        .getBaseTestData().getTestDirectory(), tInfo
                        .getBaseTestData().getTestName()));
        pw.println(String.format("<td>%s</td>", tInfo.getBaseTestData()
                .getTestDirectory()));
        pw.println(String.format("<td>%s</td>", testType));
        pw.println(String.format("<td>%s</td>",
                verdictToLabel(tInfo.getVerdict())));
        pw.println("</tr>");
    }

    // intentionally package protected
    /**
     * Static method for creating bootstrap label for a test verdict. Since it
     * is technology dependent (HTML + bootstrap) it is defined as package
     * protected.
     * 
     * @param verdict
     *            the verdict of a test
     * @return HTML-string for a verdict label
     */
    static String verdictToLabel(TestVerdict verdict) {
        switch (verdict) {
        case FAILED:
            return "<span class=\"label label-danger\">Fail</span>";
        case INCONCLUSIVE:
            return "<span class=\"label label-warning\">Inconclusive</span>";
        case SUCCEEDED:
            return "<span class=\"label label-success\">Success</span>";
        default:
            return "<span class=\"label label-default\">Unknown</span>";
        }
    }

    public void writeFooter() {
        if (pw == null)
            return;

        pw.println("</table>");
        pw.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\">"
                + "</script>");
        pw.println("<script src=\"js/bootstrap.min.js\"></script>");
        pw.println("</div>");
        pw.println("</body>");
        pw.println("</html>");
    }

    public void init() {
        OutputStream os;
        try {
            os = new FileOutputStream(testDir + "/index.html");
            pw = new PrintWriter(new OutputStreamWriter(os, "UTF8"));
        } catch (FileNotFoundException e) {
            logger.debug("Could not find output file, not writing any summary",
                    e);
        } catch (UnsupportedEncodingException e) {
            logger.debug("Used unsupported encoding for writing summary file.",
                    e);
        }

    }

    public void close() {
        if (pw != null)
            pw.close();
    }

}
