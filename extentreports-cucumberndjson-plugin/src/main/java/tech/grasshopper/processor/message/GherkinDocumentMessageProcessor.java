package tech.grasshopper.processor.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.Pickle;

@Singleton
public class GherkinDocumentMessageProcessor {

	private Map<String, GherkinDocument> uriToGherkinDocumentMap = new HashMap<>();

	public void process(GherkinDocument document) {
		uriToGherkinDocumentMap.put(document.getUri(), document);
	}

	public GherkinDocument retrieveDocument(String uri) {
		return uriToGherkinDocumentMap.get(uri);
	}

	public Scenario retrieveScenario(Pickle pickle) {
		List<Scenario> scenarios = uriToGherkinDocumentMap.get(pickle.getUri()).getFeature().getChildrenList().stream()
				.map(c -> c.getScenario()).collect(Collectors.toList());
		return scenarios.stream().filter(s -> s.getId().equalsIgnoreCase(pickle.getAstNodeIds(0))).findAny().get();
	}
}
