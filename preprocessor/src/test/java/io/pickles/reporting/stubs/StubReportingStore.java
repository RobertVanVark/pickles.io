package io.pickles.reporting.stubs;

import java.util.ArrayList;
import java.util.List;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class StubReportingStore implements ReportingStore {

	private static List<TestRun> createdTestRuns = new ArrayList<>();
	private static List<TestRun> updatedTestRuns = new ArrayList<>();
	private static List<FeatureModel> createdFeatures = new ArrayList<>();
	private static List<ScenarioModel> createdScenarios = new ArrayList<>();
	private static List<StepModel> createdSteps = new ArrayList<>();

	public static void initialize() {
		createdTestRuns = new ArrayList<>();
		updatedTestRuns = new ArrayList<>();
		createdFeatures = new ArrayList<>();
	}

	@Override
	public void create(TestRun run) {
		run.setId(1111);
		createdTestRuns.add(run);
	}

	@Override
	public void update(TestRun run) {
		updatedTestRuns.add(run);
	}

	@Override
	public void create(FeatureModel feature) {
		createdFeatures.add(feature);
		for (ScenarioModel scenario : feature.getScenarios()) {
			create(scenario);
		}
	}

	@Override
	public void create(ScenarioModel scenario) {
		createdScenarios.add(scenario);
		for (StepModel step : scenario.getSteps()) {
			create(step);
		}
	}

	@Override
	public void create(StepModel step) {
		createdSteps.add(step);
	}

	public static List<TestRun> createdTestRuns() {
		return createdTestRuns;
	}

	public static List<TestRun> updatedTestRuns() {
		return updatedTestRuns;
	}

	public static List<FeatureModel> createdFeatures() {
		return createdFeatures;
	}

	public static List<ScenarioModel> createdScenarios() {
		return createdScenarios;
	}

	public static List<StepModel> createdSteps() {
		return createdSteps;
	}

}
