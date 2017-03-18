package io.pickles.reporting.stubs;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import io.pickles.model.FeatureModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.ReportStore;

public class StubReportStore implements ReportStore {

	private List<TestRun> testRuns = new ArrayList<>();
	private List<FeatureModel> features = new ArrayList<>();

	public void setTestRuns(List<TestRun> testRuns) {
		this.testRuns = testRuns;
	}

	public void setFeatures(List<FeatureModel> features) {
		this.features = features;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FeatureModel> readAllFor(List<TestRun> testRuns) {
		// TODO Auto-generated method stub
		return null;
	}

}
