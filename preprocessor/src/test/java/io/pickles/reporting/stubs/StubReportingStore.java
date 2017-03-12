package io.pickles.reporting.stubs;

import java.util.ArrayList;
import java.util.List;

import io.pickles.model.FeatureModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class StubReportingStore implements ReportingStore {

	private static List<TestRun> createdTestRuns = new ArrayList<>();
	private static List<TestRun> updatedTestRuns = new ArrayList<>();
	private static List<FeatureModel> createdFeatures = new ArrayList<>();

	public static void initialize() {
		createdTestRuns = new ArrayList<>();
		updatedTestRuns = new ArrayList<>();
		createdFeatures = new ArrayList<>();
	}

	@Override
	public void create(TestRun run) {
		createdTestRuns.add(run);
	}

	@Override
	public void update(TestRun run) {
		updatedTestRuns.add(run);
	}

	@Override
	public void create(FeatureModel feature) {
		createdFeatures.add(feature);
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
}
