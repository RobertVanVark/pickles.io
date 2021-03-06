package io.pickles.reporting;

import java.util.List;

import org.joda.time.DateTime;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.TestRun;

public interface ReportStore {

	TestRun readTestRun(Integer key);

	TestRun readTestRun(String name);

	List<TestRun> readTestRuns(DateTime from, DateTime until);

	String readTemplate(String hashKey);

	FeatureModel readFeature(Integer id);

	List<FeatureModel> readAllFeaturesFor(List<TestRun> testRuns);

	ScenarioModel findScenarioTriggeredBy(String dvId);
}
