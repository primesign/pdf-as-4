package at.gv.egiz.pdfas.web.helper;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public class VerifyResultHTMLEncoder implements VerifyResultEncoder {

	public void produce(HttpServletRequest request,
			HttpServletResponse response, List<VerifyResult> results) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><head><title></title></head><body>");
		sb.append("<h3>Verification Results for: " + PdfAsHelper.getPDFFileName(request) + "</h3>");
		sb.append("<table style=\"width:100%\" border='1' >");
		
		sb.append("<tr>");
		
		sb.append("<th>Signature</th>");
		sb.append("<th>Processed</th>");
		sb.append("<th>Signed By</th>");
		sb.append("<th>Cert Code</th>");
		sb.append("<th>Cert Message</th>");
		sb.append("<th>Value Code</th>");
		sb.append("<th>Value Message</th>");
		sb.append("<th>Error</th>");
		sb.append("<th>Certificate</th>");
		sb.append("<th>Signed Data</th>");
		
		sb.append("</tr>");
		
		for (int i = 0; i < results.size(); i++) {
			VerifyResult result = results.get(i);
			sb.append("<tr>");
			
			sb.append("<td>" + i + "</td>");			
			
			if (result.isVerificationDone()) {
				sb.append("<td>YES</td>");
				int certCode = result.getCertificateCheck().getCode();
				String certMessage = result.getCertificateCheck().getMessage();

				int valueCode = result.getValueCheckCode().getCode();
				String valueMessage = result.getValueCheckCode().getMessage();

				Exception e = result.getVerificationException();
				
 				X509Certificate cert = (X509Certificate)result.getSignerCertificate();
				
				sb.append("<td>" + cert.getSubjectDN().getName() + "</td>");
				sb.append("<td>" + certCode + "</td>");
				sb.append("<td>" + certMessage + "</td>");
				sb.append("<td>" + valueCode + "</td>");
				sb.append("<td>" + valueMessage + "</td>");
				if(e != null) {
					sb.append("<td>" + e.getMessage() + "</td>");
				} else {
					sb.append("<td>-</td>");
				}
				if(result.isQualifiedCertificate()) {
					sb.append("<td><a href=\"signCert;jsessionid=" + request.getSession().getId() + 
						"?SIGID=" + i + "\">here</a> (QC)</td>");
				} else {
					sb.append("<td><a href=\"signCert;jsessionid=" + request.getSession().getId() + 
						"?SIGID=" + i + "\">here</a></td>");
				}
				sb.append("<td><a href=\"signData;jsessionid=" + request.getSession().getId() + 
						"?SIGID=" + i + "\">here</a></td>");
				
			} else {
				sb.append("<td>NO</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
				sb.append("<td>-</td>");
			}
			
			
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body></html>");
		
		response.setContentType("text/html");
		OutputStream os = response.getOutputStream();
		os.write(sb.toString().getBytes());
		os.close();
	}

}
