package at.gv.egiz.pdfas.lib.impl.preprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.pdfas.lib.api.Configuration;
import at.gv.egiz.pdfas.lib.api.preprocessor.PreProcessor;

public class PreProcessorLoader {

	private static final Logger logger = LoggerFactory
			.getLogger(PreProcessorLoader.class);
	
	private static ServiceLoader<PreProcessor> preProcessorLoader = ServiceLoader.load(PreProcessor.class);
	
	public synchronized static List<PreProcessor> getPreProcessors(Configuration configuration) {
		logger.debug("building PreProcessors");
		
		List<PreProcessor> processors = new ArrayList<PreProcessor>();
		Iterator<PreProcessor> processorIterator = preProcessorLoader.iterator();
		
		while(processorIterator.hasNext()) {
			PreProcessor preProcessor = processorIterator.next();
			logger.debug("Loading " + preProcessor.getName() + " [" + preProcessor.getClass().getName() + "]");
			preProcessor.initialize(configuration);
			logger.debug("Initialized " + preProcessor.getName());
			processors.add(preProcessor);
			logger.debug("Preprocessor added " + preProcessor.getName());
		}
		
		logger.debug("PreProcessors constructed");
		
		Collections.sort(processors, new PreProcessorCompare());
		
		logger.debug("PreProcessors sorted");
		return processors;
	}
}
