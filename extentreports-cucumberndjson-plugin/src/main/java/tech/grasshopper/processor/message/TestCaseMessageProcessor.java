package tech.grasshopper.processor.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.cucumber.messages.Messages.TestCaseFinished;
import io.cucumber.messages.Messages.TestCaseStarted;
import io.cucumber.messages.Messages.TestStepFinished;
import io.cucumber.messages.Messages.TestStepStarted;
import lombok.Data;
import tech.grasshopper.pojo.TestCase;
import tech.grasshopper.pojo.TestStep;
import tech.grasshopper.pojo.TestStep.ExecutionType;
import tech.grasshopper.processor.DateConverter;

@Singleton
@Data
public class TestCaseMessageProcessor {

	private TestCase currentTestCase;
	private List<TestCase> testCases = new ArrayList<>();
	private TestStep currentTestStep;
	private Map<String, io.cucumber.messages.Messages.TestCase.TestStep> hooks = new HashMap<>();
	private Map<String, io.cucumber.messages.Messages.TestCase.TestStep> steps = new HashMap<>();

	private AttachmentProcessor attachmentProcessor;

	@Inject
	public TestCaseMessageProcessor(AttachmentProcessor attachmentProcessor) {
		this.attachmentProcessor = attachmentProcessor;
	}

	public void process(io.cucumber.messages.Messages.TestCase testcase) {
		currentTestCase = new TestCase();
		currentTestCase.setTestCase(testcase);
		testCases.add(currentTestCase);

		hooks = new HashMap<>();
		steps = new HashMap<>();
		testcase.getTestStepsList().forEach(s -> {
			if (s.getHookId().isEmpty() && !s.getPickleStepId().isEmpty())
				steps.put(s.getId(), s);
			if (s.getPickleStepId().isEmpty() && !s.getHookId().isEmpty())
				hooks.put(s.getId(), s);
		});
	}

	public TestCase retrieveTestCase() {
		return currentTestCase;
	}

	public void processTestCaseStarted(TestCaseStarted testCaseStarted) {
		currentTestCase.setTestCaseBeginTime(DateConverter.parseToDate(testCaseStarted.getTimestamp()));
	}

	public void processTestStepStarted(TestStepStarted testStepStarted) {
		currentTestStep = new TestStep();
		currentTestStep.setTestStepId(testStepStarted.getTestStepId());
		currentTestCase.addTestStep(currentTestStep);
		currentTestStep.setTestStepBeginTime(DateConverter.parseToDate(testStepStarted.getTimestamp()));

		if (steps.containsKey(testStepStarted.getTestStepId()))
			currentTestStep.setType(ExecutionType.STEP);
		else if (hooks.containsKey(testStepStarted.getTestStepId()))
			currentTestStep.setType(ExecutionType.HOOK);
	}

	public void processAttachment(io.cucumber.messages.Messages.Attachment attachment) {
		currentTestStep.addAttachment(attachmentProcessor.process(attachment));
	}

	public void processTestStepFinished(TestStepFinished testStepFinished) {
		currentTestStep.setTestStepEndTime(DateConverter.parseToDate(testStepFinished.getTimestamp()));
		currentTestStep.setTestStepResult(testStepFinished.getTestStepResult());
	}

	public void processTestCaseFinished(TestCaseFinished testCaseFinished) {
		currentTestCase.setTestCaseEndTime(DateConverter.parseToDate(testCaseFinished.getTimestamp()));
		currentTestCase.updateTestStepData();
	}
}
