package tech.grasshopper.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.cucumber.messages.Messages.Envelope;
import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.Pickle;
import io.cucumber.messages.Messages.Pickle.PickleStep;
import io.cucumber.messages.NdjsonToMessageIterable;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Scenario;
import tech.grasshopper.pojo.StepOrHook;
import tech.grasshopper.pojo.TestStep.ExecutionType;
import tech.grasshopper.processor.message.GherkinDocumentMessageProcessor;
import tech.grasshopper.processor.message.PickleMessageProcessor;
import tech.grasshopper.processor.message.TestCaseMessageProcessor;
import tech.grasshopper.processor.message.TestRunMessageProcessor;
import tech.grasshopper.reporters.ReporterRunData;

@Singleton
public class MessageToFeaturesProcessor {

	private TestRunMessageProcessor testRunBeginAndEndMessageProcessor;
	private GherkinDocumentMessageProcessor gherkinDocumentMessageProcessor;
	private PickleMessageProcessor pickleMessageProcessor;
	private TestCaseMessageProcessor testCaseMessageProcessor;
	private ReporterRunData runData;

	@Inject
	public MessageToFeaturesProcessor(TestRunMessageProcessor testRunBeginAndEndMessageProcessor,
			GherkinDocumentMessageProcessor gherkinDocumentMessageProcessor,
			PickleMessageProcessor pickleMessageProcessor, TestCaseMessageProcessor testCaseMessageProcessor,
			ReporterRunData runData) {
		this.testRunBeginAndEndMessageProcessor = testRunBeginAndEndMessageProcessor;
		this.gherkinDocumentMessageProcessor = gherkinDocumentMessageProcessor;
		this.pickleMessageProcessor = pickleMessageProcessor;
		this.testCaseMessageProcessor = testCaseMessageProcessor;
		this.runData = runData;
	}

	public void process(NdjsonToMessageIterable ndjMsgs) {

		Iterator<Envelope> iterator = ndjMsgs.iterator();

		while (iterator.hasNext()) {
			Envelope env = iterator.next();

			if (env.hasTestRunStarted())
				testRunBeginAndEndMessageProcessor.processRunBeginTime(env.getTestRunStarted());

			if (env.hasGherkinDocument())
				gherkinDocumentMessageProcessor.process(env.getGherkinDocument());

			if (env.hasPickle())
				pickleMessageProcessor.process(env.getPickle());

			if (env.hasTestCase())
				testCaseMessageProcessor.process(env.getTestCase());

			if (env.hasTestCaseStarted())
				testCaseMessageProcessor.processTestCaseStarted(env.getTestCaseStarted());

			if (env.hasTestStepStarted())
				testCaseMessageProcessor.processTestStepStarted(env.getTestStepStarted());
			
			if(env.hasAttachment())
				testCaseMessageProcessor.processAttachment(env.getAttachment());

			if (env.hasTestStepFinished())
				testCaseMessageProcessor.processTestStepFinished(env.getTestStepFinished());

			if (env.hasTestCaseFinished())
				testCaseMessageProcessor.processTestCaseFinished(env.getTestCaseFinished());

			if (env.hasTestRunFinished())
				testRunBeginAndEndMessageProcessor.processRunEndTime(env.getTestRunFinished());
		}

		Set<Feature> features = new LinkedHashSet<>();
		Map<String, Feature> uriToFeatureMap = new HashMap<>();
		testCaseMessageProcessor.getTestCases().forEach(tc -> {
			Pickle pickle = pickleMessageProcessor.retrievePickle(tc.getTestCase().getPickleId());
			GherkinDocument document = gherkinDocumentMessageProcessor.retrieveDocument(pickle.getUri());

			Feature feature = uriToFeatureMap.computeIfAbsent(document.getUri(), u -> Feature.createFeature(document));
			features.add(feature);

			io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario messageScenario = gherkinDocumentMessageProcessor
					.retrieveScenario(pickle);
			Scenario scenario = Scenario.createScenario(pickle, messageScenario, tc);
			feature.addScenario(scenario);
			feature.updateStartEndTimes(scenario);

			tc.getSteps().forEach(st -> {
				if (st.getType() == ExecutionType.STEP) {
					PickleStep pickleStep = pickle.getStepsList().stream()
							.filter(s -> s.getId().equalsIgnoreCase(st.getTestStep().getPickleStepId())).findAny()
							.get();
					io.cucumber.messages.Messages.GherkinDocument.Feature.Step messageStep = document.getFeature().getChildrenList().stream()
							.flatMap(c -> c.getScenario().getStepsList().stream())
							.filter(s -> s.getId().equalsIgnoreCase(pickleStep.getAstNodeIds(0))).findAny().get();
					scenario.addStepOrHook(StepOrHook.createStepOrHook(pickleStep, messageStep, st));
				} 
				else if (st.getType() == ExecutionType.HOOK) {
					scenario.addStepOrHook(StepOrHook.createStepOrHook(null, null, st));	
				}
			});
		});

		System.out.println(testRunBeginAndEndMessageProcessor.getRunData().getDuration().getStartTime());
		System.out.println(testRunBeginAndEndMessageProcessor.getRunData().getDuration().getEndTime());
		features.forEach(f -> {
			System.out.println(f);
			f.getScenarios().forEach(s -> {
				System.out.println(s);
				s.getStepOrHooks().forEach(st -> {
					System.out.println(st);
				});
			});
		});
		runData.setFeatures(features);
	}
}
