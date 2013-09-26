package at.gv.egiz.pdfas.lib.api;

public class ByteArrayDataSource implements DataSource {

    private byte[] byteData;

    public ByteArrayDataSource(byte[] data)  {
        this.byteData = data;
    }

    public String getMIMEType() {
        return "application/pdf";
    }

    public byte[] getByteData() {
        return this.byteData;
    }

}
