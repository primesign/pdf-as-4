package at.gv.egiz.pdfas.lib.api.sign;

import java.util.Calendar;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

//TODO[PDFAS-115]: Add test

/**
 * Provides current local time as pdf signing time.
 * 
 * @implNote Note that an already set time
 *           ({@link RequestedSignature#getStatus()}.{@link OperationStatus#getSigningDate() getSigningDate()} is
 *           preferred if set.
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
@Immutable
@ThreadSafe
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
