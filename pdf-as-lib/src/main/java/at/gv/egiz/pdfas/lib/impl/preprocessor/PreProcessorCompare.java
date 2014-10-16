package at.gv.egiz.pdfas.lib.impl.preprocessor;

import java.util.Comparator;

import at.gv.egiz.pdfas.lib.api.preprocessor.PreProcessor;

public class PreProcessorCompare implements Comparator<PreProcessor> {

	@Override
	public int compare(PreProcessor o1, PreProcessor o2) {
		if(o1.registrationPosition() < 0 && o2.registrationPosition() < 0) {
			// equal
			return 0;
		} else if(o1.registrationPosition() < 0) {
			// o2 vor o1
			return 1;
		} else if(o2.registrationPosition() < 0) {
			// o1 vor o2
			return -1;
		} else {
			return Integer.compare(o1.registrationPosition(), o2.registrationPosition());
		}
	}

}
