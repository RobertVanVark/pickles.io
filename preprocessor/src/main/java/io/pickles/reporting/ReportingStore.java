package io.pickles.reporting;

import io.pickles.model.FeatureModel;
import io.pickles.model.TestRun;

public interface ReportingStore {

	void create(TestRun run);

	void update(TestRun run);

	void create(FeatureModel feature);
}
