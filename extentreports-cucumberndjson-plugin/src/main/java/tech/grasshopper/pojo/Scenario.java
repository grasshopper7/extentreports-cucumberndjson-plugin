package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.cucumber.messages.Messages.Pickle;
import lombok.Data;

@Data
public class Scenario {

	private String uri;
	private Location location;
	private String keyword;
	private String name;
	private String scenarioOutlineName;
	private String description;
	private List<Tag> tags = new ArrayList<>();
	private List<StepOrHook> stepOrHooks = new ArrayList<>();

	private int testId;
	private Date scenarioStartTime;
	private Date scenarioEndTime;

	public static Scenario createScenario(Pickle pickle, io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario messageScenario, TestCase testCase) {
		Scenario scenario = new Scenario();
		scenario.setUri(pickle.getUri());
		scenario.setName(pickle.getName());
		pickle.getTagsList().forEach(t -> scenario.tags.add(Tag.createTag(t)));
		
		scenario.setKeyword(messageScenario.getKeyword());
		scenario.setDescription(messageScenario.getDescription().trim());
		scenario.setLocation(Location.createLocation(messageScenario.getLocation()));
		scenario.setScenarioOutlineName(messageScenario.getName());
		
		scenario.setScenarioStartTime(testCase.getTestCaseBeginTime());
		scenario.setScenarioEndTime(testCase.getTestCaseEndTime());
		return scenario;
	}
	
	public void addStepOrHook(StepOrHook stepOrHook) {
		stepOrHooks.add(stepOrHook);
	}

	@Override
	public String toString() {
		return "Scenario [uri=" + uri + ", location=" + location + ", keyword=" + keyword + ", name=" + name
				+ ", description=" + description + ", tags=" + tags + ", scenarioStartTime=" + scenarioStartTime
				+ ", scenarioEndTime=" + scenarioEndTime + "]";
	}
}
