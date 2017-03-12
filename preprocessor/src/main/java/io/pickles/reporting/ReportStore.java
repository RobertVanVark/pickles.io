package io.pickles.reporting;

import java.util.List;

import io.pickles.model.FeatureModel;
import io.pickles.model.TestRun;

public interface ReportStore {

	TestRun readTestRun(Integer key);

	TestRun readTestRun(String name);

	FeatureModel readFeature(Integer id);

	List<FeatureModel> readAllFor(TestRun run);
}
