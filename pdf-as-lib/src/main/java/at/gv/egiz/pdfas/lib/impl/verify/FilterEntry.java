package at.gv.egiz.pdfas.lib.impl.verify;

import org.apache.pdfbox.cos.COSName;

public class FilterEntry {
	private COSName filter;
	private COSName subFilter;
	
	public FilterEntry(COSName filter, COSName subfilter) {
		this.filter = filter;
		this.subFilter = subfilter;
	}
	
	public COSName getFilter() {
		return filter;
	}
	public void setFilter(COSName filter) {
		this.filter = filter;
	}
	public COSName getSubFilter() {
		return subFilter;
	}
	public void setSubFilter(COSName subFilter) {
		this.subFilter = subFilter;
	}
	
	
}
