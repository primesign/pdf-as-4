package at.gv.egiz.pdfas.lib.api.sign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import at.gv.egiz.pdfas.lib.impl.status.OperationStatus;
import at.gv.egiz.pdfas.lib.impl.status.RequestedSignature;

public class LocalSigningTimeSourceTest {
	
	private final IMocksControl ctrl = EasyMock.createControl();
	
	private SigningTimeSource cut = new LocalSigningTimeSource();
	
	@Test
	public void test_getSigningTime_noDateFromContext_expectCurrentDate() {
		
		ctrl.reset();
		
		OperationStatus status = ctrl.createMock(OperationStatus.class);
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		
		expect(requestedSignature.getStatus()).andReturn(status).atLeastOnce();
		expect(status.getSigningDate()).andReturn(null);
		
		ctrl.replay();
		
		Calendar signingTime = cut.getSigningTime(requestedSignature);
		
		ctrl.verify();
		
		// no signing date stored within context -> expect current date to be returned
		assertThat(signingTime.getTime()).isInSameMinuteWindowAs(new Date());
		
	}
	
	@Test
	public void test_getSigningTime_noStatus_expectCurrentDate() {
		
		ctrl.reset();
		
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		
		expect(requestedSignature.getStatus()).andReturn(null);
		
		ctrl.replay();
		
		Calendar signingTime = cut.getSigningTime(requestedSignature);
		
		ctrl.verify();
		
		// no signing date stored within context -> expect current date to be returned
		assertThat(signingTime.getTime()).isInSameMinuteWindowAs(new Date());
		
	}
	
	@Test
	public void test_getSigningTime_expectDateFromContext() {
		
		Calendar otherDay = Calendar.getInstance();
		otherDay.setTime(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
		
		ctrl.reset();
		
		OperationStatus status = ctrl.createMock(OperationStatus.class);
		RequestedSignature requestedSignature = ctrl.createMock(RequestedSignature.class);
		
		expect(requestedSignature.getStatus()).andReturn(status).atLeastOnce();
		expect(status.getSigningDate()).andReturn(otherDay).atLeastOnce();
		
		ctrl.replay();
		
		Calendar signingTime = cut.getSigningTime(requestedSignature);
		
		ctrl.verify();
		
		// no signing date stored within context -> expect current date to be returned
		assertThat(signingTime, is(otherDay));
		
	}

}
