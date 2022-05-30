package at.gv.egiz.pdfas.lib.api.sign;

import java.util.Calendar;

import javax.annotation.Nonnull;

import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

// TODO[PDFAS-115]: Add javadoc
// TODO[PDFAS-115]: Add test

@FunctionalInterface
public interface SigningTimeSource {
	
	@Nonnull
	Calendar getSigningTime(@Nonnull RequestedSignature requestedSignature);

}
