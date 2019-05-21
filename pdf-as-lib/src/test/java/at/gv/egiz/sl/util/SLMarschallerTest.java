package at.gv.egiz.sl.util;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;

import org.junit.Test;

import at.gv.egiz.sl.schema.InfoboxReadParamsAssocArrayType;
import at.gv.egiz.sl.schema.InfoboxReadParamsAssocArrayType.ReadValue;
import at.gv.egiz.sl.schema.InfoboxReadRequestType;
import at.gv.egiz.sl.schema.ObjectFactory;

/**
 * Concurrency tests for {@link SLMarschaller} (sic!).
 *
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class SLMarschallerTest {

	private JAXBElement<InfoboxReadRequestType> createInfoboxReadRequest() {
		InfoboxReadRequestType request = new InfoboxReadRequestType();
		request.setInfoboxIdentifier("Certificates");
		InfoboxReadParamsAssocArrayType readData = new InfoboxReadParamsAssocArrayType();
		ReadValue readValue = new ReadValue();
		readValue.setKey("SecureSignatureKeypair");
		readData.setReadValue(readValue);
		request.setAssocArrayParameters(readData);
		return new ObjectFactory().createInfoboxReadRequest(request);
	}

	@Test
	public void testConcurrency() throws InterruptedException, TimeoutException {

		final int NUMBER_OF_TESTS = 1000;

		// prepare NUMBER_OF_TESTS callable tasks
		List<Callable<Void>> callables = new ArrayList<>(NUMBER_OF_TESTS);
		for (int i = 0; i < NUMBER_OF_TESTS; i++) {
			Callable<Void> callable = () -> {
				SLMarschaller.marshalToString(createInfoboxReadRequest());
				return null;
			};
			callables.add(callable);
		}

		// we like to execute with 10 threads in parallel
		ExecutorService executor = Executors.newFixedThreadPool(10);

		// start execution
		List<Future<Void>> results = executor.invokeAll(callables);


		// initiate two-phase orderly shutdown of execution service as recommended by Oracle
		// (https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)

		executor.shutdown(); // previously submitted tasks are executed, no more tasks accepted

		try {
			if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {  // wait for existing tasks to terminate
				executor.shutdownNow();                              // cancel currently running tasks
			}
			if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {  // wait for tasks to respond to being cancelled
				System.err.println("Pool did not terminate.");
			}
		} catch (InterruptedException e) {
		     // (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		}

		// loop over result look for exceptions occurred during execution (due to concurrency issues for instance).
		for (Future<Void> result : results) {
			try {
				result.get(1, TimeUnit.SECONDS);
			} catch (ExecutionException e) {
				System.err.println("Concurrency issue occurred!");
				e.getCause().printStackTrace();
				fail("Concurrency issue occurred!");
			}
		}

	}

}
