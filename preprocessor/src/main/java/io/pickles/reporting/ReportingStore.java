package io.pickles.reporting;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.TestRun;

public interface ReportingStore {

	void createTestRun(TestRun run);

	TestRun getTestRunBy(String id);

	void storeFeture(FeatureModel feature);

	void readFeature(FeatureModel feature);

	void readAllFor(TestRun run);
}
