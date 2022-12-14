package at.gv.egiz.pdfas.lib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ByteRangeInputStreamTest {

	@Test
	public void testReadFromStream() throws IOException {
		
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);
		
		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				350, 150,   // offset, length  [350...499]  
				600,   0,   // offset, length  []
				600,   1    // offset, length  [600]
		}; 
		// @formatter:on
		
		byte[] result;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange)) {
			result = IOUtils.toByteArray(in);
		}
		
		assertEquals(451, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 200 + 150 + 0 + 1];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		System.arraycopy(randomData, 150, expectedData, 100, 200);
		System.arraycopy(randomData, 350, expectedData, 300, 150);
		System.arraycopy(randomData, 600, expectedData, 450,   1);
		
		assertArrayEquals(expectedData, result);
		
	}
	
	@Test
	public void testReadFromStreamWithSkip() throws IOException {
		
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);
		
		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
                 10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				350, 150,   // offset, length  [350...499]  
				600,   0,   // offset, length  []
				600,   1    // offset, length  [600]
		}; 
		// @formatter:on
		
		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange)) {
			
			byte[] buffer = new byte[50];
			assertEquals(50, in.read(buffer));   // read 50 bytes
			out.write(buffer);
			
			long skipped = in.skip(200);         // 200 bytes skipped
			assertEquals(200, skipped);
			skipped = in.skip(1);                //   1 byte skipped
			assertEquals(1, skipped);
			
			IOUtils.copy(in, out);               // 200 bytes read
			result = out.toByteArray();
		}

		assertEquals(250, result.length);
		
		// tmp data result
		byte[] tmpData = new byte[100 + 200 + 150 + 0 + 1];
		System.arraycopy(randomData,  10, tmpData,   0, 100);
		System.arraycopy(randomData, 150, tmpData, 100, 200);
		System.arraycopy(randomData, 350, tmpData, 300, 150);
		System.arraycopy(randomData, 600, tmpData, 450,   1);
		
		// expected result
		byte[] expectedData = new byte[250];
		System.arraycopy(tmpData,   0, expectedData,   0, 50);
		// ... 201 bytes skipped
		System.arraycopy(tmpData, 251, expectedData, 50, 200);
		
		assertArrayEquals(expectedData, result);
		
	}
	
	@Test
	public void testReadFromStreamWithAvailable() throws IOException {
		
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);
		
		// @formatter:off
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				350, 150,   // offset, length  [350...499]  
				600,   0,   // offset, length  []
				600,   1    // offset, length  [600]
		}; 
		// @formatter:on
		
		long cycles = 0;
		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange)) {
			while (in.available() > 0) {
				byte[] buffer = new byte[in.available()];
				assertEquals(buffer.length, in.read(buffer));
				out.write(buffer);
				if (cycles++ > 100) {
					fail("Endless loop detected.");
				}
			}
			result = out.toByteArray();
		}
		
		// expected result
		byte[] expectedData = new byte[100 + 200 + 150 + 0 + 1];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		System.arraycopy(randomData, 150, expectedData, 100, 200);
		System.arraycopy(randomData, 350, expectedData, 300, 150);
		System.arraycopy(randomData, 600, expectedData, 450,   1);
		
		assertArrayEquals(expectedData, result);
		
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitWithOverlappingByteRanges1() {
		
		// @formatter:off
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				349, 150,   // offset, length  [349...498]  
				600,   0,   // offset, length  []
				600,   1    // offset, length  [600]
		}; 
		// @formatter:on
		
		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitWithOddByteRanges() {
		
		// @formatter:off
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				350
		}; 
		// @formatter:on
		
		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitWithNegativeOffset() {
		
		// @formatter:off
		int[] byteRange = {
				-10, 100,   // offset, length
				150, 200    // offset, length
		}; 
		// @formatter:on
		
		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitWithNegativeLength() {

		// @formatter:off
		int[] byteRange = {
				 10,  100,   // offset, length
				150, -200    // offset, length
		}; 
		// @formatter:on
		
		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange);
		
	}

	@Test
	public void testReadFromStreamWithGapFilled() throws IOException {

		byte[] nullBytes = new byte[10000];
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
			600,   1    // offset, length  [600]
		};
		// @formatter:on

		byte[] result;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, true)) {
			result = IOUtils.toByteArray(in);
		}

		assertEquals(591, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150 + 100 + 0 + 1];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		System.arraycopy(nullBytes,  0, expectedData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		System.arraycopy(nullBytes,  0, expectedData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, expectedData, 340, 150);
		System.arraycopy(nullBytes,  0, expectedData,   490, 100); //nullbytes
		System.arraycopy(randomData, 600, expectedData, 590,   0);
		System.arraycopy(nullBytes,  0, expectedData,   590, 0); //nullbytes
		System.arraycopy(randomData, 600, expectedData, 590,   1);

		assertArrayEquals(expectedData, result);
	}

	@Test
	public void testReadFromStreamWithAvailableAndGapFilled() throws IOException {

		byte[] nullBytes = new byte[10000];
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
			600,   1    // offset, length  [600]
		};
		// @formatter:on

		long cycles = 0;
		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, true)) {
			while (in.available() > 0) {
				byte[] buffer = new byte[in.available()];
				assertEquals(buffer.length, in.read(buffer));
				out.write(buffer);
				if (cycles++ > 100) {
					fail("Endless loop detected.");
				}
			}
			result = out.toByteArray();
		}

		assertEquals(591, result.length);
		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150 + 100 + 0 + 1];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		System.arraycopy(nullBytes,  0, expectedData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		System.arraycopy(nullBytes,  0, expectedData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, expectedData, 340, 150);
		System.arraycopy(nullBytes,  0, expectedData,   490, 100); //nullbytes
		System.arraycopy(randomData, 600, expectedData, 590,   0);
		System.arraycopy(nullBytes,  0, expectedData,   590, 0); //nullbytes
		System.arraycopy(randomData, 600, expectedData, 590,   1);

		assertArrayEquals(expectedData, result);
	}

	@Test
	public void testReadFromStreamWithSkipAndGapFilled() throws IOException {

		byte[] nullBytes = new byte[10000];
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
			600,   1    // offset, length  [600]
		};
		// @formatter:on

		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, true)) {

			byte[] buffer = new byte[50];
			assertEquals(50, in.read(buffer));   // 10 skipped gap, read 50 bytes, pos 59
			out.write(buffer);

			long skipped = in.skip(200);         // 200 bytes skipped
			assertEquals(240, skipped);    // 200 bytes range skipped + 40 bytes gap, pos 299 TODO: discuss what is correct here
 			skipped = in.skip(1);                //   1 byte skipped, pos 300
			assertEquals(1, skipped);

			assertEquals(300, IOUtils.copy(in, out)); //  49 bytes range, 150  range, 100 gap, 1 range, pos 600
			result = out.toByteArray();
		}

		assertEquals(350, result.length); // 250 bytes range + 100 bytes gap

		// tmp data result
		byte[] tmpData = new byte[100 + 40 + 200 + 0 + 150 + 100 + 0 + 1];
		System.arraycopy(randomData,  10, tmpData,   0, 100);
		System.arraycopy(nullBytes,  0, tmpData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, tmpData, 140, 200);
		System.arraycopy(nullBytes,  0, tmpData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, tmpData, 340, 150);
		System.arraycopy(nullBytes,  0, tmpData,   490, 100); //nullbytes
		System.arraycopy(randomData, 600, tmpData, 590,   0);
		System.arraycopy(nullBytes,  0, tmpData,   590, 0); //nullbytes
		System.arraycopy(randomData, 600, tmpData, 590,   1);

		// expected result
		byte[] expectedData = new byte[350];
		System.arraycopy(tmpData,   0, expectedData,   0, 50);
		// ... 201 bytes skipped
		System.arraycopy(tmpData, 291, expectedData, 50, 300);

		assertArrayEquals(expectedData, result);

	}

}
