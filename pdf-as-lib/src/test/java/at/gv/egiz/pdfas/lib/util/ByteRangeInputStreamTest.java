package at.gv.egiz.pdfas.lib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
	public void testReadFromStreamByteWise() throws IOException {
		
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
		int bytesRead = 0;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int data;
			while ((data = in.read()) != -1) {
				out.write(data);
				if (bytesRead++ > 10000) {
					fail("Endless loop detected.");
				}
			}
			result = out.toByteArray();
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
	public void testInitWithOverlappingByteRanges1WithGap() {

		// @formatter:off
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				349, 150,   // offset, length  [349...498]
				600,   0,   // offset, length  []
				600,   1    // offset, length  [600]
		};
		// @formatter:on

		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA);
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
	public void testInitWithOddByteRangesWithGap() {

		// @formatter:off
		int[] byteRange = {
				10, 100,   // offset, length  [ 10...109]
				150, 200,   // offset, length  [150...349]
				350
		};
		// @formatter:on

		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA);
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
	public void testInitWithNegativeOffsetWithGap() {

		// @formatter:off
		int[] byteRange = {
				-10, 100,   // offset, length
				150, 200    // offset, length
		};
		// @formatter:on

		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA);
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
	@Test(expected = IllegalArgumentException.class)
	public void testInitWithNegativeLengthWithGap() {

		// @formatter:off
		int[] byteRange = {
				 10,  100,   // offset, length
				150, -200    // offset, length
		};
		// @formatter:on

		new ByteRangeInputStream(new ByteArrayInputStream(new byte[0]), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA);
	}

	private void createGap(byte[] buffer, int start, int length) {
		if (length < 2) return;
		byte[] nullBytes = new byte[10000];
		System.arraycopy("<".getBytes(StandardCharsets.UTF_8),  0, buffer,   start, 1);
		System.arraycopy(nullBytes,  0, buffer,   start + 1, length - 2);
		System.arraycopy(">".getBytes(StandardCharsets.UTF_8),  0, buffer,   start + length - 1, 1);
	}
	@Test
	public void testReadFromStreamWithGapFilled() throws IOException {

		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
		};
		// @formatter:on

		byte[] result;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {
			result = IOUtils.toByteArray(in);
		}

		assertEquals(490, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		createGap(expectedData, 100, 40); // gap
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		createGap(expectedData,   340, 0); // gap ignored
		System.arraycopy(randomData, 350, expectedData, 340, 150);
//		createGap(expectedData,   490, 100); // gap ignored
//		System.arraycopy(randomData, 600, expectedData, 590,   0); // because range is 0

		assertArrayEquals(expectedData, result);
	}

	@Test
	public void testReadFromStreamWithBigEnoughGap() throws IOException {
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			112, 200,   // offset, length  [112...311]
		};
		// @formatter:on

		byte[] result;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {
			result = IOUtils.toByteArray(in);
		}

		assertEquals(302, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 2 + 200];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		createGap(expectedData, 100, 2); // gap
		System.arraycopy(randomData, 112, expectedData, 102, 200);
		assertArrayEquals(expectedData, result);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testReadFromStreamWithTooSmallGap() throws IOException {
		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			111, 200,   // offset, length  [111...310] -> gap has only space for opening but not closing delimiter
		};
		// @formatter:on

		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {
			IOUtils.toByteArray(in);
		}
	}

	@Test
	(expected = IllegalArgumentException.class)
	public void testReadFromStreamWithMultipleGaps() throws IOException {
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
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {
			result = IOUtils.toByteArray(in);
		}

		assertEquals(490, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		createGap(expectedData, 100, 40); // gap
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		createGap(expectedData,   340, 0); // gap ignored
		System.arraycopy(randomData, 350, expectedData, 340, 150);
		createGap(expectedData,   490, 100); // gap ignored
		System.arraycopy(randomData, 600, expectedData, 590,   1); // -> range introducing a second gap

		assertArrayEquals(expectedData, result);
	}


	@Test
	public void testReadFromStreamWithAvailableAndGapFilled() throws IOException {

		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
		};
		// @formatter:on

		long cycles = 0;
		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {
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

		assertEquals(490, result.length);
		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		createGap( expectedData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		createGap( expectedData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, expectedData, 340, 150);


		assertArrayEquals(expectedData, result);
	}

	@Test
	public void testReadFromStreamWithSkipAndGapFilled() throws IOException {

		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
			600,   0,   // offset, length  []
		};
		// @formatter:on

		byte[] result;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA)) {

			byte[] buffer = new byte[50];
			assertEquals(50, in.read(buffer));   // 10 skipped gap, read 50 bytes, pos 59
			out.write(buffer);

			long skipped = in.skip(200);         // 200 bytes skipped
			assertEquals(200, skipped);    // 40 bytes range + 40 bytes gap + 120 range, pos 259
 			skipped = in.skip(1);                //   1 byte skipped, pos 260
			assertEquals(1, skipped);

			assertEquals(239, IOUtils.copy(in, out)); //  89 bytes range, 150  range; ignored 100 gap, because 0 range afterwards;
			result = out.toByteArray(); // pos 600
		}

		assertEquals(289, result.length); // 50 bytes range + 340 bytes range + 200 skip + 1 skip = 591 + 10 initial skipped gap

		// tmp data result
		byte[] tmpData = new byte[100 + 40 + 200 + 0 + 150 + 100 + 0 + 1];
		System.arraycopy(randomData,  10, tmpData,   0, 100);
		createGap( tmpData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, tmpData, 140, 200);
		createGap( tmpData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, tmpData, 340, 150);
		// everything afterwards is ignored

		// expected result
		byte[] expectedData = new byte[289];
		System.arraycopy(tmpData,   0, expectedData,   0, 50);
		// ... 201 bytes skipped
		System.arraycopy(tmpData, 251, expectedData, 50, 239);

		assertArrayEquals(expectedData, result);
	}

	@Test
	public void testReadFromStreamByteWiseWithGapFilled() throws IOException {

		byte[] randomData = new byte[10000];
		new Random().nextBytes(randomData);

		// @formatter:off
		// 451 bytes total
		int[] byteRange = {
			10, 100,   // offset, length  [ 10...109]
			150, 200,   // offset, length  [150...349]
			350, 150,   // offset, length  [350...499]
		};
		// @formatter:on

		byte[] result;
		int bytesRead = 0;
		try (InputStream in = new ByteRangeInputStream(new ByteArrayInputStream(randomData), byteRange, ByteRangeInputStream.Mode.SIGNED_PDF_DATA);
		     ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int data;
			while ((data = in.read()) != -1) {
				out.write(data);
				if (bytesRead++ > 10000) {
					fail("Endless loop detected.");
				}
			}
			result = out.toByteArray();
		}

		assertEquals(490, result.length);

		// expected result
		byte[] expectedData = new byte[100 + 40 + 200 + 0 + 150];
		System.arraycopy(randomData,  10, expectedData,   0, 100);
		createGap( expectedData,   100, 40); //nullbytes
		System.arraycopy(randomData, 150, expectedData, 140, 200);
		createGap( expectedData,   340, 0); //nullbytes
		System.arraycopy(randomData, 350, expectedData, 340, 150);


		assertArrayEquals(expectedData, result);
	}

}
