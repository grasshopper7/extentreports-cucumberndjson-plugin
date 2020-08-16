package tech.grasshopper.test.heirarchy;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Hook;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.processor.message.AttachmentProcessor;

@Singleton
public class DefaultExtentTestHeirarchy extends ExtentTestHeirarchy {
	
	@Inject
	public DefaultExtentTestHeirarchy(AttachmentProcessor attachmentProcessor) {
		super(attachmentProcessor);
	}

	@Override
	public void createTestHeirarchy(Set<Feature> features, ExtentReports extent) {
		this.features = features;
		this.extent = extent;
		features.forEach(feature -> {
			ExtentTest featureTest = createFeatureExtentTest(feature);
			feature.getScenarios().forEach(scenario -> {
				ExtentTest scenarioTest = createScenarioExtentNode(featureTest, scenario);
				scenario.getStepOrHooks().forEach(stepOrHook -> {
					if(stepOrHook instanceof Step)
						createStepExtentNode(scenarioTest, (Step)stepOrHook);
					else if(stepOrHook instanceof Hook)
						createHookExtentNode(scenarioTest, (Hook)stepOrHook);
				});
			});
		});
	}
}
