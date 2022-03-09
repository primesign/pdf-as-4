
package at.gv.egiz.pdfas.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StreamUtils {
  
  /**
   * Compare the contents of two <code>InputStream</code>s.
   * 
   * @param is1 The 1st <code>InputStream</code> to compare.
   * @param is2 The 2nd <code>InputStream</code> to compare.
   * @return boolean <code>true</code>, if both streams contain the exactly the
   * same content, <code>false</code> otherwise.
   * @throws IOException An error occurred reading one of the streams.
   */
  public static boolean compareStreams(InputStream is1, InputStream is2) 
    throws IOException {
      
    byte[] buf1 = new byte[256];
    byte[] buf2 = new byte[256];
    int length1;
    int length2;
  
    try {
      while (true) {
        length1 = is1.read(buf1);
        length2 = is2.read(buf2);
        
        if (length1 != length2) {
          return false;
        }
        if (length1 <= 0) {
          return true;
        }
        if (!compareBytes(buf1, buf2, length1)) {
          return false;
        }
      }
    } catch (IOException e) {
      throw e;
    } finally {
      // close both streams
      try {
        is1.close();
        is2.close();
      } catch (IOException e) {
        // ignore this
      }
    }
  }
  
  /**
   * Compare two byte arrays, up to a given maximum length.
   * 
   * @param b1 1st byte array to compare.
   * @param b2 2nd byte array to compare.
   * @param length The maximum number of bytes to compare.
   * @return <code>true</code>, if the byte arrays are equal, <code>false</code>
   * otherwise.
   */
  private static boolean compareBytes(byte[] b1, byte[] b2, int length) {
    if (b1.length != b2.length) {
      return false;
    }
  
    for (int i = 0; i < b1.length && i < length; i++) {
      if (b1[i] != b2[i]) {
        return false;
      }
    }
  
    return true;
  }

  /**
   * Reads a byte array from a stream.
   * @param in The <code>InputStream</code> to read.
   * @return The bytes contained in the given <code>InputStream</code>.
   * @throws IOException on any exception thrown
   */
  public static byte[] readStream(InputStream in) throws IOException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    copyStream(in, out, null);
		  
		/*  
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int b;
    while ((b = in.read()) >= 0)
      out.write(b);
    
    */
    in.close();
    return out.toByteArray();
  }

  /**
   * Reads a <code>String</code> from a stream, using given encoding.
   * @param in The <code>InputStream</code> to read.
   * @param encoding The character encoding to use for converting the bytes
   * of the <code>InputStream</code> into a <code>String</code>.
   * @return The content of the given <code>InputStream</code> converted into
   * a <code>String</code>.
   * @throws IOException on any exception thrown
   */
  public static String readStream(InputStream in, String encoding) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    copyStream(in, out, null);

    /*
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int b;
    while ((b = in.read()) >= 0)
      out.write(b);
      */
    in.close();
    return out.toString(encoding);
  }
  
  /**
   * Reads all data (until EOF is reached) from the given source to the 
   * destination stream. If the destination stream is null, all data is dropped.
   * It uses the given buffer to read data and forward it. If the buffer is 
   * null, this method allocates a buffer.
   *
   * @param source The stream providing the data.
   * @param destination The stream that takes the data. If this is null, all
   *                    data from source will be read and discarded.
   * @param buffer The buffer to use for forwarding. If it is null, the method
   *               allocates a buffer.
   * @exception IOException If reading from the source or writing to the 
   *                        destination fails.
   */
  private static void copyStream(InputStream source, OutputStream destination, byte[] buffer) throws IOException {
    if (source == null) {
      throw new NullPointerException("Argument \"source\" must not be null.");
    }
    if (buffer == null) {
      buffer = new byte[8192];
    }
    
    if (destination != null) {
      int bytesRead;
      while ((bytesRead = source.read(buffer)) >= 0) {
        destination.write(buffer, 0, bytesRead);
      }
    } else {
      while (source.read(buffer) >= 0);
    }    
  }
  
  /**
   * Gets the stack trace of the <code>Throwable</code> passed in as a string.
   * @param t The <code>Throwable</code>.
   * @return a String representing the stack trace of the <code>Throwable</code>.
   */
  public static String getStackTraceAsString(Throwable t)
  {
    ByteArrayOutputStream stackTraceBIS = new ByteArrayOutputStream();
    t.printStackTrace(new PrintStream(stackTraceBIS));
    return new String(stackTraceBIS.toByteArray());
  }
}
