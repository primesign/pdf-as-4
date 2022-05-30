package at.gv.egiz.pdfas.lib.api.sign;

import java.util.Calendar;

import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

//TODO[PDFAS-115]: Add javadoc
//TODO[PDFAS-115]: Add test

public class LocalSigningTimeSource implements SigningTimeSource {

	@Override
	public Calendar getSigningTime(RequestedSignature requestedSignature) {
		Calendar claimedSigningDate = Calendar.getInstance();
		if (requestedSignature.getStatus() != null && requestedSignature.getStatus().getSigningDate() != null) {
			claimedSigningDate = requestedSignature.getStatus().getSigningDate();
		}
		return claimedSigningDate;
	}

}
