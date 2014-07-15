package at.gv.egiz.pdfas.web.helper;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.gv.egiz.pdfas.lib.api.verify.VerifyResult;

public interface VerifyResultEncoder {
	public void produce(HttpServletRequest request,
			HttpServletResponse response, 
			List<VerifyResult> results) throws IOException;
}
