package io.pickles.reporting;

import java.util.List;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;

public interface ReportingStore {

	void create(TestRun run);

	void update(TestRun run);

	void create(FeatureModel feature);

	void create(ScenarioModel model);

	void create(StepModel step);

	void create(FeatureModel template, String uri, List<String> lines);
}
