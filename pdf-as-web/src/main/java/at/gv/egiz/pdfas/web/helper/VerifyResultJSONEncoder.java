package at.gv.egiz.pdfas.web.helper;

import iaik.x509.X509Certificate;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

import org.apache.commons.codec.binary.Base64;

public class VerifyResultJSONEncoder implements VerifyResultEncoder {

	private static final Logger logger = LoggerFactory
			.getLogger(VerifyResultJSONEncoder.class);

	public void produce(HttpServletRequest request,
			HttpServletResponse response, List<VerifyResult> results)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("{\"signatures\":[");
		for (int i = 0; i < results.size(); i++) {
			VerifyResult result = results.get(i);

			X509Certificate cert = (X509Certificate) result
					.getSignerCertificate();

			int certCode = result.getCertificateCheck().getCode();
			String certMessage = result.getCertificateCheck().getMessage();

			int valueCode = result.getValueCheckCode().getCode();
			String valueMessage = result.getValueCheckCode().getMessage();

			Exception e = result.getVerificationException();

			sb.append("{");
			if (result.isVerificationDone()) {
				sb.append("\"processed\":\"" + result.isVerificationDone()
						+ "\", ");
				sb.append("\"signedBy\":\"" + cert.getSubjectDN().getName()
						+ "\", ");
				sb.append("\"certCode\":\"" + certCode + "\", ");
				sb.append("\"certMessage\":\"" + certMessage + "\", ");
				sb.append("\"valueCode\":\"" + valueCode + "\", ");
				sb.append("\"valueMessage\":\"" + valueMessage + "\"");
				if (e != null) {
					sb.append(", ");
					sb.append("\"error\":\"" + e.getMessage()
							+ "\"");
				}
				sb.append(", ");
				sb.append("\"certificate\":\"signCert?SIGID=" + i + "\", ");
				sb.append("\"signedData\":\"signData?SIGID=" + i + "\"");
			} else {
				sb.append("\"processed\":\"" + result.isVerificationDone()
						+ "\"");
			}

			sb.append("}");
			
			if(i < results.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]}");

		response.setContentType("application/json");
		OutputStream os = response.getOutputStream();
		os.write(sb.toString().getBytes());
		os.close();
	}

}
