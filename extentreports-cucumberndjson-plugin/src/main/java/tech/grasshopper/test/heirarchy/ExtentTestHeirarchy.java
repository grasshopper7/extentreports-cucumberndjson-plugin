package tech.grasshopper.test.heirarchy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.model.Test;

import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Hook;
import tech.grasshopper.pojo.Scenario;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.processor.message.AttachmentProcessor;

public abstract class ExtentTestHeirarchy {

	protected Set<Feature> features;
	protected ExtentReports extent;
	protected Map<String, ExtentTest> uriFeatureTestMap = new HashMap<>();
	protected Map<String, ExtentTest> uriLinesScenarioOutlineTestMap = new HashMap<>();

	private AttachmentProcessor attachmentProcessor;

	@Inject
	protected ExtentTestHeirarchy(AttachmentProcessor attachmentProcessor) {
		this.attachmentProcessor = attachmentProcessor;
	}

	public abstract void createTestHeirarchy(Set<Feature> features, ExtentReports extent);

	public ExtentTest createFeatureExtentTest(Feature feature) {
		String uri = feature.getUri();
		if (uriFeatureTestMap.containsKey(uri))
			return uriFeatureTestMap.get(uri);

		ExtentTest featureExtentTest = extent.createTest(com.aventstack.extentreports.gherkin.model.Feature.class,
				feature.getName(), feature.getDescription());
		uriFeatureTestMap.put(uri, featureExtentTest);
		feature.getTags().forEach(t -> featureExtentTest.assignCategory(t.getName()));
		Test test = featureExtentTest.getModel();
		test.setStartTime(feature.getFeatureStartTime());
		test.setEndTime(feature.getFeatureEndTime());
		feature.setTestId(test.getId());
		return featureExtentTest;
	}

	public ExtentTest createScenarioExtentNode(ExtentTest parentExtentTest, Scenario scenario) {
		if (scenario.getKeyword().equalsIgnoreCase("Scenario Outline"))
			parentExtentTest = createScenarioOutlineExtentNode(parentExtentTest, scenario);

		ExtentTest scenarioExtentTest = parentExtentTest.createNode(
				com.aventstack.extentreports.gherkin.model.Scenario.class, scenario.getName(),
				scenario.getDescription());
		scenario.getTags().forEach(t -> scenarioExtentTest.assignCategory(t.getName()));
		Test test = scenarioExtentTest.getModel();
		test.setStartTime(scenario.getScenarioStartTime());
		test.setEndTime(scenario.getScenarioEndTime());
		scenario.setTestId(test.getId());
		return scenarioExtentTest;
	}

	public ExtentTest createScenarioOutlineExtentNode(ExtentTest parentExtentTest, Scenario scenarioOutline) {
		String uriStepLines = scenarioOutline.getUri() + ":" + scenarioOutline.getLocation().getLine() + ":"
				+ scenarioOutline.getLocation().getColumn();
		ExtentTest scenarioOutlineExtentTest = null;

		if (!uriLinesScenarioOutlineTestMap.containsKey(uriStepLines))
			scenarioOutlineExtentTest = parentExtentTest.createNode(
					com.aventstack.extentreports.gherkin.model.ScenarioOutline.class,
					scenarioOutline.getScenarioOutlineName(), scenarioOutline.getDescription());
		else
			scenarioOutlineExtentTest = uriLinesScenarioOutlineTestMap.get(uriStepLines);

		Test test = scenarioOutlineExtentTest.getModel();
		if (test.getStartTime() == null || scenarioOutline.getScenarioStartTime().compareTo(test.getStartTime()) < 0)
			test.setStartTime(scenarioOutline.getScenarioStartTime());
		if (test.getEndTime() == null || scenarioOutline.getScenarioEndTime().compareTo(test.getEndTime()) > 0)
			test.setEndTime(scenarioOutline.getScenarioEndTime());

		uriLinesScenarioOutlineTestMap.put(uriStepLines, scenarioOutlineExtentTest);
		return scenarioOutlineExtentTest;
	}

	public ExtentTest createHookExtentNode(ExtentTest parentExtentTest, Hook hook) {
		ExtentTest hookExtentTest = parentExtentTest
				.createNode(com.aventstack.extentreports.gherkin.model.Asterisk.class, "Hook");
		hook.setTestId(hookExtentTest.getModel().getId());

		Test test = hookExtentTest.getModel();
		test.setStartTime(hook.getExecutionStartTime());
		test.setEndTime(hook.getExecutionEndTime());

		hook.getAttachments().removeIf(a -> a.getType() == null);
		attachmentProcessor.updateExtentTestWithAttachment(hookExtentTest, hook.getAttachments());
		return hookExtentTest;
	}

	public ExtentTest createStepExtentNode(ExtentTest parentExtentTest, Step step) {
		ExtentTest stepExtentTest = null;
		GherkinKeyword keyword = null;
		try { // Default set to And
			keyword = new GherkinKeyword("And");
			keyword = new GherkinKeyword(step.getKeyword().trim());
		} catch (ClassNotFoundException e) {
		}

		stepExtentTest = parentExtentTest.createNode(keyword, step.getKeyword() + step.getText(), "");
		step.setTestId(stepExtentTest.getModel().getId());

		/*
		 * if (step.getRows().size() > 0)
		 * stepExtentTest.info(step.getDataTableMarkup()); if (step.getDocStringMarkup()
		 * != null) stepExtentTest.info(step.getDocStringMarkup());
		 */

		step.getAttachments().removeIf(a -> a.getType() == null);
		attachmentProcessor.updateExtentTestWithAttachment(stepExtentTest, step.getAttachments());

		Test test = stepExtentTest.getModel();
		test.setStartTime(step.getExecutionStartTime());
		test.setEndTime(step.getExecutionEndTime());
		updateTestLogStatus(stepExtentTest, step);
		return stepExtentTest;
	}

	public void updateTestLogStatus(ExtentTest test, Step step) {
		String stepStatus = step.getResult();
		String stepMessage = step.getMessage();

		if (stepStatus.equalsIgnoreCase("failed")) {
			// Throwable throwInstance =
			// errorMessageProcessor.createThrowableObject(result.getErrorMessage());
			// test.fail(throwInstance);
			test.fail("");
		} else if (stepStatus.equalsIgnoreCase("passed"))
			test.pass("");
		else
			test.skip("");
	}

}
