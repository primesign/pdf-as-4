package at.gv.egiz.pdfas.lib.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;

/**
 * InputStream considering both byte ranges as used for pdf signatures (digest input data) and byte ranges used for
 * preparing pdf documents reflecting the signed content.
 * <p>
 * In case of <strong>digest calculation</strong> (e.g. in the course of signature), gaps between two byte ranges are
 * skipped when calculating the digest. Use {@link #ByteRangeInputStream(InputStream, int[])} or
 * {@link #ByteRangeInputStream(InputStream, int[], Mode)} with {@link Mode#DIGEST}.
 * </p>
 * <p>
 * In case byte ranges are to be used in order to <strong>prepare a pdf document showing the signed data</strong>, gaps
 * between two byte ranges are filled with {@code 0} and surrounded by delimiters '{@code <}' and '{@code >}'. Use
 * {@link #ByteRangeInputStream(InputStream, int[], Mode)} with {@link Mode#SIGNED_PDF_DATA}
 * 
 * @author Thomas Knall, PrimeSign GmbH
 *
 */
public class ByteRangeInputStream extends FilterInputStream {

	/**
	 * Reflects a single byte range.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 *
	 */
	static class ByteRange {

		private final int offset;
		private int bytesLeft;
		private final boolean gap;
		
		// preset the delimiters to be used for byte ranges representing a gap
		private Byte startDelimiter = (byte) '<';
		private Byte endDelimiter =  (byte) '>';

		/**
		 * Creates a single byte range.
		 * 
		 * @param offset The offset. (must be non-negative)
		 * @param length The length. (must be non-negative)
		 * @param gap    {@code true} in case the byte range represents a gap between two non-gap byte ranges, {@code false}
		 *               otherwise.
		 */
		ByteRange(int offset, int length, boolean gap) {
			if (offset < 0 || length < 0) {
				throw new IllegalArgumentException("Negative offset or negative length are not supported.");
			}
			this.offset = offset;
			this.bytesLeft = length;
			this.gap = gap;
		}
		
		/**
		 * Tells if the start delimiter has already been consumed for the current (gap) byte range.
		 * 
		 * @return {@code true} if already consumed, {@code false} if not.
		 */
		public boolean hasStartDelimiter() {
			return startDelimiter != null;
		}

		/**
		 * Returns the start delimiter.
		 * 
		 * @return The start delimiter.
		 * @throws IllegalStateException In case the delimiter has already been consumed ({@link #hasStartDelimiter()} returns
		 *                               {@code false}).
		 * @apiNote Note that the delimiter can only be consumed once. Consecutive calls of this method raise
		 *          IllegalStateExceptions.
		 * @see #hasStartDelimiter()
		 */
		public byte consumeStartDelimiter() {
			if (!hasStartDelimiter()) {
				throw new IllegalStateException("Start delimiter has already been consumed.");
			}
			byte result = startDelimiter;
			startDelimiter = null;
			return result;
		}
		
		/**
		 * Tells if the end delimiter has already been consumed for the current (gap) byte range.
		 * 
		 * @return {@code true} if already consumed, {@code false} if not.
		 */
		public boolean hasEndDelimiter() {
			return endDelimiter != null;
		}
		
		/**
		 * Returns the end delimiter.
		 * 
		 * @return The end delimiter.
		 * @throws IllegalStateException In case the delimiter has already been consumed ({@link #hasEndDelimiter()} returns
		 *                               {@code false}).
		 * @apiNote Note that the delimiter can only be consumed once. Consecutive calls of this method raise
		 *          IllegalStateExceptions.
		 * @see #hasEndDelimiter()
		 */
		public byte consumeEndDelimiter() {
			if (!hasEndDelimiter()) {
				throw new IllegalStateException("End delimiter has already been consumed.");
			}
			byte result = endDelimiter;
			endDelimiter = null;
			return result;
		}

		/**
		 * Returns the offset.
		 * @return The offset.
		 */
		public int getOffset() {
			return offset;
		}

		/**
		 * Returns the number of bytes not yet {@link #consume(int) consumed}.
		 * 
		 * @return The number of bytes left.
		 */
		public int bytesLeft() {
			return this.bytesLeft;
		}
		
		/**
		 * Consumes {@code bytes} bytes.
		 * 
		 * @param bytes The number of bytes to be consumed. (must be smaller or equal {@link #bytesLeft()}.
		 */
		public void consume(int bytes) {
			if (bytes > this.bytesLeft) {
				throw new IllegalArgumentException("Unable to consume more that bytesLeft() bytes.");
			}
			this.bytesLeft -= bytes;
		}

		/**
		 * Tells if the byte range reflects a "gap" ({@code true}) or reflects a "normal" byte range ({@code false}) covering
		 * the digest data.
		 * 
		 * @return {@code true} if gap, {@code false} otherwise.
		 */
		public boolean isGap() {
			return gap;
		}

	}
	
	private Iterator<ByteRange> ranges;
	private ByteRange currentRange;
	private long currentPosition = 0;

	/**
	 * Reflects the processing mode of the input stream.
	 * 
	 * @author Thomas Knall, PrimeSign GmbH
	 *
	 */
	public enum Mode {
	
		/**
		 * Reflects a processing mode suitable for digest calculation in the course of pdf signature.
		 */
		DIGEST,
		
		/**
		 * Reflects a processing mode suitable for creating pdf documents suitable to be shown to the user for inspection of signed data.
		 */
		SIGNED_PDF_DATA
	
	}

	/**
	 * Creates a new instance using the provided input stream and the provided {@code byteRange}. The instance is suitable
	 * for digest calculation.
	 * 
	 * @param in        The input stream to be used. (required; must not be {@code null})
	 * @param byteRange The byte range to be used. (required; must not be {@code null})
	 * @see Mode#DIGEST
	 * @apiNote Byte ranges must always consist of tuples (offset and length). Byte ranges must not overlap.
	 */
	public ByteRangeInputStream(@Nonnull InputStream in, @Nonnull int[] byteRange) {
		this(in, byteRange, Mode.DIGEST);
	}

	/**
	 * Creates a new instance using the provided input stream and the provided {@code byteRange}. The instance is suitable
	 * for digest calculation.
	 * 
	 * @param in        The input stream to be used. (required; must not be {@code null})
	 * @param byteRange The byte range to be used. (required; must not be {@code null})
	 * @param mode      Reflects the processing mode. (required; must not be {@code null})
	 * @apiNote Byte ranges must always consist of tuples (offset and length). Byte ranges must not overlap.
	 * @apiNote In case of processing mode {@link Mode#SIGNED_PDF_DATA} the provided byte range must not contain more than
	 *          one gap.
	 */
	public ByteRangeInputStream(@Nonnull InputStream in, @Nonnull int[] byteRange, @Nonnull Mode mode) {
		super(in);
		List<ByteRange> byteRanges = prepareByteRanges(Objects.requireNonNull(byteRange, "'byteRange' must not be null."));
		if (mode == Mode.SIGNED_PDF_DATA) {
			List<ByteRange> byteRangesWithGaps = addGaps(byteRanges);
			int numberOfGaps = byteRangesWithGaps.size() - byteRanges.size(); 
			if (numberOfGaps > 1) {
				throw new IllegalArgumentException("When using processing mode " + Mode.SIGNED_PDF_DATA + " the provided byte ranges must not contain more than one single gap. The provided byte ranges " + Arrays.toString(byteRange) + " reflect " + numberOfGaps + " gaps.");
			}
			byteRanges = byteRangesWithGaps;
		}
		ranges = byteRanges.iterator();
	}
	
	/**
	 * Iterates over the provided list of byte ranges adding intermediate byte ranges reflecting gaps.
	 * 
	 * @param byteRanges The list of non-gap related byte ranges. (required; must not be {@code null})
	 * @return A list in which "real" byte ranges and "gap" byte ranges alternate. (never {@code null} but may be empty).
	 * @implNote Empty gaps (gaps of length 0) are skipped. Gaps with 1 or 2 bytes are not supported since no delimiters can
	 *           be added.
	 */
	@Nonnull
	private List<ByteRange> addGaps(@Nonnull List<ByteRange> byteRanges) {

		if (byteRanges.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<ByteRange> result = new ArrayList<>();
		ByteRange previousByteRange;
		
		Iterator<ByteRange> it = byteRanges.iterator();
		
		// make sure not to start with gap
		result.add(previousByteRange = it.next());
		
		while (it.hasNext()) {
			ByteRange currentByteRange = it.next();
			// add gap byte range
			int offset = previousByteRange.getOffset() + previousByteRange.bytesLeft();
			int length = currentByteRange.getOffset() - offset;
			if (length > 0) {
				if (length < 2) {
					throw new IllegalArgumentException("Unable to support gaps smaller than 2 bytes since there would be no space for 2 delimiters.");
				}
				result.add(new ByteRange(offset, length, true));
			}
			// add regular byte range
			result.add(currentByteRange);
			previousByteRange = currentByteRange;
		}
		
		return result;
		
	}

	/**
	 * Prepares and validates the provided byte ranges.
	 * 
	 * @param byteRange The byte ranges (offset/length tuples). (required; must not be {@code null})
	 * @return A list of byte ranges. (never {@code null} but may be empty)
	 * @implNote Empty byte ranges are skipped.
	 */
	@Nonnull
	private List<ByteRange> prepareByteRanges(@Nonnull int[] byteRange) {
		if (byteRange.length % 2 != 0) {
			throw new IllegalArgumentException("'byteRange' must provide offset and length tuples.");
		}
		List<ByteRange> byteRanges = new ArrayList<>(byteRange.length / 2);
		long position = 0;
		for (int i = 0; i < byteRange.length / 2; i++) {
			int offset = byteRange[i * 2];
			int length = byteRange[i * 2 + 1];
			if (offset < position) {
				throw new IllegalArgumentException("Overlapping byteRanges are not supported: offset=" + offset + ", length=" + length);
			}
			if (length != 0) {
				byteRanges.add(new ByteRange(offset, length, false));
			} // skip empty byte ranges
			position = offset + length;
		}
		return byteRanges;
	}

	/**
	 * Makes sure {@link #currentRange} reflects the byte range to read from. {@link #currentRange} is set {@code null} in
	 * case to further readable byte ranges are available.
	 * 
	 * @throws IOException Thrown in case of error reading from original stream.
	 */
	private void updateCurrentRange() throws IOException {
		
		if (currentRange != null && currentRange.bytesLeft() > 0) {
			// no need to update currentRange
			return;
		}
		
		// update currentRange
		
		while (ranges.hasNext() && (currentRange = ranges.next()).bytesLeft <= 0) {
			// skip empty ranges
		}

		// do we have readable byte ranges left ?
		if (currentRange != null && currentRange.bytesLeft() > 0) {
			
			while (currentPosition < currentRange.getOffset() ) {
				// skip bytes until reaching next byte range offset 
				long skipped = super.skip(currentRange.getOffset() - currentPosition);
				currentPosition += skipped;
			}
			
		} else {
			// no further byte ranges to read
			currentRange = null;
		}
		
	}
	
	@Override
	public int available() throws IOException {
		updateCurrentRange();
		if (currentRange == null) {
			return 0;
		}
		return Math.min(currentRange.bytesLeft(), super.available());
	}

	@Override
	public int read() throws IOException {
		updateCurrentRange();
		if (currentRange == null) {
			return -1;
		}
		int value = super.read();
		currentRange.consume(1);
		currentPosition++;
		return currentRange.isGap() ? determineGapValue() : value;
	}
	
	/**
	 * Either returns {@code 0}, the start delimiter or the end delimiter, depending on the relative read position within a
	 * gap related byte range.
	 * 
	 * @return A suitable int when reading a gap.
	 */
	private int determineGapValue() {
		if (!currentRange.isGap()) {
			throw new IllegalStateException("Current range expected to reflect a gap.");
		}
		if (currentRange.hasStartDelimiter()) {
			return currentRange.consumeStartDelimiter();
		} else if (currentRange.bytesLeft() == 0) { // reached the end of the byte range
			return currentRange.consumeEndDelimiter();
		}
		return 0;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		updateCurrentRange();
		if (currentRange == null) {
			return -1;
		}
		int bytesRead = super.read(b, off, Math.min(currentRange.bytesLeft(), len));
		currentRange.consume(bytesRead);
		currentPosition += bytesRead;
		
		if (currentRange.isGap()) {
			// fill the specific part of the byte array that has been read with 0
			for (int i = off; i < off + bytesRead; i++) {
				b[i] = 0;
			}
			considerGapDelimiters(b, off, bytesRead);
		}
		
		return bytesRead;
	}
	
	/**
	 * Sets the respective delimiter when the current (gap) range starts or ends.
	 * 
	 * @param b         The buffer into which the data is read. (required; must not be {@code null})
	 * @param off       The start offset in the destination array {@code b}.
	 * @param bytesRead The number of bytes read. (must be a positive integer)
	 */
	private void considerGapDelimiters(byte[] b, int off, int bytesRead) {
		if (!currentRange.isGap()) {
			throw new IllegalStateException("Current range expected to reflect a gap.");
		}
		if (currentRange.hasStartDelimiter()) {
			b[off] = currentRange.consumeStartDelimiter();
		}
		if (currentRange.bytesLeft() == 0) { // reached the end of the byte range
			b[off + bytesRead - 1] = currentRange.consumeEndDelimiter();
		}
	}

	@Override
	public long skip(long n) throws IOException {
		return IOUtils.skip(this, n); // reads n bytes (this is essential for our code)
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new IllegalStateException(getClass().getSimpleName() + " does not support mark.");
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("The stream has not been marked since " + getClass().getSimpleName() + " does not support mark.");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

}
