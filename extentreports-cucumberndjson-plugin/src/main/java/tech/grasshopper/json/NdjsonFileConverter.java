package tech.grasshopper.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.cucumber.messages.NdjsonToMessageIterable;
import tech.grasshopper.processor.MessageToFeaturesProcessor;

@Singleton
public class NdjsonFileConverter {
	
	private MessageToFeaturesProcessor messageToFeaturesProcessor;
	
	@Inject
	public NdjsonFileConverter(MessageToFeaturesProcessor messageToFeaturesProcessor) {
		this.messageToFeaturesProcessor = messageToFeaturesProcessor;
	}

	public void retrieveFeaturesFromReport() throws FileNotFoundException {
		NdjsonToMessageIterable iterable = new NdjsonToMessageIterable(
				new FileInputStream(new File("D:/cucumber-report.ndjson")));

		messageToFeaturesProcessor.process(iterable);
	}
}
