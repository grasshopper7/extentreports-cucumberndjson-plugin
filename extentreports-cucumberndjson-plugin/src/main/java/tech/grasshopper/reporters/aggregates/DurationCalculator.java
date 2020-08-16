package tech.grasshopper.reporters.aggregates;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Test;

import tech.grasshopper.pojo.Feature;
//import tech.grasshopper.pojo.Hook;
//import tech.grasshopper.pojo.Result;
import tech.grasshopper.pojo.Scenario;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.pojo.StepOrHook;

public class DurationCalculator {

	private List<Test> extentTestHeirarchy;
	private List<Scenario> scenarios;
	private Date updatedDate;

	public DurationCalculator(Set<Feature> features, List<Test> extentTestHeirarchy) {
		this.extentTestHeirarchy = extentTestHeirarchy;
		scenarios = features.stream().flatMap(f -> f.getScenarios().stream()).collect(toList());
		updatedDate = new Date();
		extentTestHeirarchy.forEach(t ->  {
			System.out.println(t.getStartTime());
			System.out.println(t.getEndTime());
		});
	}

	public void updateExtentTestTimeData() {
		List<Test> featureChildrenTests = extentTestHeirarchy.stream()
				.flatMap(e -> e.getNodeContext().getAll().stream()).collect(toList());
		List<Test> scenarioTests = new ArrayList<>();

		for (Test test : featureChildrenTests) {
			if (test.getBehaviorDrivenTypeName().equalsIgnoreCase("Scenario Outline")) {
				scenarioTests.addAll(test.getNodeContext().getAll());
				updateScenarioOutlineExtentTestStartEndTimes(test);
			} else
				scenarioTests.add(test);
		}
		updateScenarioExtentTestStartEndTimes(scenarioTests);
	}

	protected void updateScenarioOutlineExtentTestStartEndTimes(Test scenarioOutlineExtentTest) {
		List<Integer> childTestIds = scenarioOutlineExtentTest.getNodeContext().getAll().stream().map(t -> t.getId())
				.collect(toList());
		List<Scenario> childScenarios = scenarios.stream().filter(s -> childTestIds.contains(s.getTestId()))
				.collect(toList());

		List<Date> startTimes = childScenarios.stream().map(s -> s.getScenarioStartTime()).collect(toList());
		List<Date> endTimes = childScenarios.stream().map(s -> s.getScenarioEndTime()).collect(toList());

		Comparator<Date> dateComparator = Date::compareTo;
		Comparator<Date> dateComparatorReversed = dateComparator.reversed();

		startTimes.sort(dateComparator);
		endTimes.sort(dateComparatorReversed);

		scenarioOutlineExtentTest.setStartTime(startTimes.get(0));
		scenarioOutlineExtentTest.setEndTime(endTimes.get(0));
	}

	protected void updateScenarioExtentTestStartEndTimes(List<Test> scenarioExtentTests) {
		Map<Integer, Scenario> idToScenarioMap = scenarios.stream()
				.collect(Collectors.toMap(Scenario::getTestId, Function.identity()));

		scenarioExtentTests.forEach(s -> {
			Scenario matched = idToScenarioMap.get(s.getId());
			s.setStartTime(matched.getScenarioStartTime());
			s.setEndTime(matched.getScenarioEndTime());
		});

		List<Test> stepAndHooksExtentTests = scenarioExtentTests.stream()
				.flatMap(e -> e.getNodeContext().getAll().stream()).collect(toList());
		updateStepAndHookExtentTestStartEndTimesAndLogTimestamp(stepAndHooksExtentTests);
	}

	private void updateStepAndHookExtentTestStartEndTimesAndLogTimestamp(List<Test> stepAndHooksExtentTests) {
		Map<Integer, Test> idToTestMap = stepAndHooksExtentTests.stream()
				.collect(Collectors.toMap(Test::getId, Function.identity()));

		scenarios.forEach(s -> {
			updatedDate = s.getScenarioStartTime();
			//updateHookExtentTestStartEndTimesAndLogTimestamp(s.getBefore(), idToTestMap);
			for (StepOrHook stepOrHook : s.getStepOrHooks()) {
				//updateHookExtentTestStartEndTimesAndLogTimestamp(step.getBefore(), idToTestMap);
				if(stepOrHook instanceof Step)
					updateStepExtentTestStartEndTimesAndLogTimestamp((Step)stepOrHook, idToTestMap);
				//updateHookExtentTestStartEndTimesAndLogTimestamp(step.getAfter(), idToTestMap);
			}
			//updateHookExtentTestStartEndTimesAndLogTimestamp(s.getAfter(), idToTestMap);
		});
	}

	/*
	 * private void updateHookExtentTestStartEndTimesAndLogTimestamp(List<Hook>
	 * hooks, Map<Integer, Test> idToTestMap) { for (Hook hook : hooks) { Test
	 * hookTest = idToTestMap.get(hook.getTestId());
	 * updateTestStartEndTimesAndLogTimestamp(hookTest, hook.getResult()); } }
	 */

	private void updateStepExtentTestStartEndTimesAndLogTimestamp(Step step, Map<Integer, Test> idToTestMap) {
		Test stepTest = idToTestMap.get(step.getTestId());
		updateTestStartEndTimesAndLogTimestamp(stepTest, step.getDuration());
	}

	private void updateTestStartEndTimesAndLogTimestamp(Test test, Duration duration) {
		test.setStartTime(updatedDate);
		Date logTimeStamp = updatedDate;

		List<Log> stepLogs = test.getLogContext().getAll();
		stepLogs.forEach(l -> l.setTimestamp(logTimeStamp));

		updatedDate = Date.from(updatedDate.toInstant().plusSeconds(duration.getSeconds()).plusNanos(duration.getNano()));
		test.setEndTime(updatedDate);
	}
}