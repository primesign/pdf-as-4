package at.gv.egiz.pdfas.lib.api.sign;

import java.io.Closeable;
import java.io.IOException;

import javax.activation.DataSource;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

public class ExternalSignatureContextTest {
	
	interface CloseableDataSource extends DataSource, Closeable {
	}
	
	@Test
	public void test_close() throws IOException {
		
		IMocksControl ctrl = EasyMock.createControl();
		
		CloseableDataSource dataSource = ctrl.createMock(CloseableDataSource.class);
		
		// expect close() being called
		dataSource.close();
		
		ctrl.replay();
		
		try (ExternalSignatureContext cut = new ExternalSignatureContext()) {
			cut.setPreparedDocument(dataSource);
		}
		
		ctrl.verify();
		
	}

}
