package io.pickles.reporting;

import io.pickles.preprocessor.model.FeatureModel;
import io.pickles.preprocessor.model.TestRun;

public interface ReportingStore {

	void create(TestRun run);

	void update(TestRun run);

	void create(FeatureModel feature);
}
