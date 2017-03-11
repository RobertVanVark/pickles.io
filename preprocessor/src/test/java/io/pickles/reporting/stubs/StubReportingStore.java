package io.pickles.reporting.stubs;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.TestRun;
import io.pickles.reporting.ReportingStore;

public class StubReportingStore implements ReportingStore {

	@Override
	public void create(TestRun run) {
		System.out.println("XXXXXXXX - StubReportingStore - TestRun(create) : " + run.getId() + "-" + run.getName());
	}

	@Override
	public void update(TestRun run) {
		System.out.println("XXXXXXXX - StubReportingStore - TestRun(update) : " + run.getId() + "-" + run.getName());
	}

	@Override
	public void create(FeatureModel feature) {
		System.out.println(
				"XXXXXXXX - StubReportingStore - FeatureModel(create) : " + feature.getId() + "-" + feature.getName());
	}
}
