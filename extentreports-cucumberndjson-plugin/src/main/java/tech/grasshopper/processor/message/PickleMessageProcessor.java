package tech.grasshopper.processor.message;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import io.cucumber.messages.Messages.Pickle;

@Singleton
public class PickleMessageProcessor {

	private Map<String, Pickle> pickleIdToPickleMap = new HashMap<>();
	
	public void process(Pickle pickle) {
		pickleIdToPickleMap.put(pickle.getId(), pickle);
	}
	
	public Pickle retrievePickle(String pickleId) {
		return pickleIdToPickleMap.get(pickleId);
	}
}
