package io.pickles.reporting.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportStore;

public class StubReportStore implements ReportStore {

	private List<TestRun> testRuns = new ArrayList<>();
	private List<FeatureModel> features = new ArrayList<>();
	private List<ScenarioModel> scenarios = new ArrayList<>();
	private String featureTemplate;

	public void setTestRuns(List<TestRun> testRuns) {
		this.testRuns = testRuns;
	}

	public void setFeatures(List<FeatureModel> features) {
		this.features = features;
	}

	public void setScenarios(List<ScenarioModel> scenarios) {
		this.scenarios = scenarios;
	}

	public void setFeatureTemplate(String featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	@Override
	public TestRun readTestRun(Integer key) {
		return null;
	}

	@Override
	public TestRun readTestRun(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureModel readFeature(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TestRun> readTestRuns(DateTime from, DateTime until) {
		return testRuns;
	}

	@Override
	public List<FeatureModel> readAllFeaturesFor(List<TestRun> testRuns) {
		return features.stream().filter(s -> testRuns.contains(s.getTestRun())).collect(Collectors.toList());
	}

	@Override
	public ScenarioModel findScenarioTriggeredBy(String dvId) {
		Optional<ScenarioModel> findFirst = scenarios.stream().filter(s -> dvId.equals(s.getTriggeringDvId()))
				.findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	@Override
	public String readTemplate(String hashKey) {
		return featureTemplate;
	}

}
