package io.pickles.reporting;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;
import io.pickles.model.FeatureModel;
import io.pickles.model.FeatureModelShould;
import io.pickles.model.ScenarioModel;
import io.pickles.model.ScenarioModelShould;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;
import io.pickles.reporting.stubs.StubReportStore;

public class ReportBuilderShould {

	private ReportBuilder reporter;
	private StubReportStore stubReportStore;

	@Test
	public void generateEmptyReportForNoFeatures() {
		givenEmptyReportStore();

		JsonElement report = reporter.generate(Collections.emptyList());
		assertThat(report.toString(), equalTo("[]"));

		report = reporter.generate(Arrays.asList(testrunWithoutFeatures()));
		assertThat(report.toString(), equalTo("[]"));
	}

	@Test
	public void combineSameFeaturesInDifferentTestRuns() {
		givenTwoTestRunsWithSameFeature();
		JsonElement report = reporter.generate(stubReportStore.readTestRuns(null, null));
		assertThat(report.getAsJsonArray(), iterableWithSize(1));
		assertThat(report.getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("elements"), iterableWithSize(2));
	}

	@Test
	public void notCombineDifferentFeaturesOverMultipleTestRuns() {
		givenTwoTestRunsWithDifferentFeatures();

		JsonElement report = reporter.generate(stubReportStore.readTestRuns(null, null));

		JsonArray featuresJson = report.getAsJsonArray();
		assertThat(featuresJson, iterableWithSize(2));

		JsonArray firstScenarios = featuresJson.get(0).getAsJsonObject().getAsJsonArray("elements");
		assertThat(firstScenarios, iterableWithSize(1));

		JsonArray secondScenarios = featuresJson.get(1).getAsJsonObject().getAsJsonArray("elements");
		assertThat(secondScenarios, iterableWithSize(1));
	}

	@Test
	public void combineVerificationWithInitiationScenario() {
		givenThenAfterScenarioInTwoTestRuns();
		JsonElement report = reporter.generate(stubReportStore.readTestRuns(null, null));

		JsonArray featuresJson = report.getAsJsonArray();
		assertThat(featuresJson, iterableWithSize(1));

		JsonArray scenarios = featuresJson.get(0).getAsJsonObject().getAsJsonArray("elements");
		assertThat(scenarios, iterableWithSize(1));

		JsonArray steps = scenarios.get(0).getAsJsonObject().getAsJsonArray("steps");
		assertThat(steps, iterableWithSize(4));
		assertThat(steps.get(2).getAsJsonObject().getAsJsonObject("result").get("duration").getAsInt(),
				equalTo(999999));
		assertThat(steps.get(2).getAsJsonObject().getAsJsonArray("rows"), iterableWithSize(2));
	}

	@Test
	public void combineTwoVerificationWithInitiationScenario() {
		givenThenAfterScenarioInThreeTestRuns();
		JsonElement report = reporter.generate(stubReportStore.readTestRuns(null, null));

		JsonArray featuresJson = report.getAsJsonArray();
		assertThat(featuresJson, iterableWithSize(1));

		JsonArray scenarios = featuresJson.get(0).getAsJsonObject().getAsJsonArray("elements");
		assertThat(scenarios, iterableWithSize(1));

		JsonArray steps = scenarios.get(0).getAsJsonObject().getAsJsonArray("steps");
		assertThat(steps, iterableWithSize(6));
	}

	@Ignore
	@Test
	public void completeIncompleteThenAfterScenario() {
		givenIncompleteThenAfterScenario();
		JsonElement report = reporter.generate(stubReportStore.readTestRuns(null, null));

		JsonArray featuresJson = report.getAsJsonArray();
		assertThat(featuresJson, iterableWithSize(1));

		JsonArray scenarios = featuresJson.get(0).getAsJsonObject().getAsJsonArray("elements");
		assertThat(scenarios, iterableWithSize(1));

		JsonArray steps = scenarios.get(0).getAsJsonObject().getAsJsonArray("steps");
		assertThat(steps, iterableWithSize(4));
	}

	private TestRun testrunWithoutFeatures() {
		return new TestRun(999, "", "", DateTime.now(), DateTime.now());
	}

	private void givenEmptyReportStore() {
		stubReportStore = new StubReportStore();
		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private void givenTwoTestRunsWithSameFeature() {
		stubReportStore = new StubReportStore();

		TestRun run1 = new TestRun(1, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature1 = FeatureModelShould.modelWithName("same feature");
		feature1.setTestRun(run1);
		ScenarioModel scenario1 = ScenarioModelShould.modelWithName("scenario");
		feature1.addScenario(scenario1);

		TestRun run2 = new TestRun(2, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature2 = FeatureModelShould.modelWithName("same feature");
		feature2.setTestRun(run2);
		ScenarioModel scenario2 = ScenarioModelShould.modelWithName("a scenario");
		feature2.addScenario(scenario2);

		stubReportStore.setTestRuns(Arrays.asList(run1, run2));
		stubReportStore.setFeatures(Arrays.asList(feature1, feature2));

		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private void givenThenAfterScenarioInTwoTestRuns() {
		stubReportStore = new StubReportStore();

		TestRun run1 = new TestRun(1, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature1 = FeatureModelShould.modelWithName("same feature");
		feature1.setTestRun(run1);
		ScenarioModel scenario1 = ScenarioModelShould.modelWithName("scenario");
		scenario1.setNextDvId("1111111111");
		feature1.addScenario(scenario1);
		scenario1.addStep(matchedStepWith("Given ", "a certain context", 100));
		scenario1.addStep(matchedStepWith("When ", "an action is performed", 100));
		scenario1.addStep(matchedStepWith("Then ", "after 03:00 hr I expect a certain outcome", 100));

		TestRun run2 = new TestRun(2, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature2 = FeatureModelShould.modelWithName("same feature");
		feature2.setTestRun(run2);
		ScenarioModel scenario2 = ScenarioModelShould.modelWithName("scenario (dvId=next dv id)");
		scenario2.setTriggeringDvId("1111111111");
		feature2.addScenario(scenario2);
		scenario2.addStep(matchedStepWith("Given ", "Test Execution Context is loaded for dvId=1111111111", 999999L));
		scenario2.addStep(matchedStepWithDatatableAnd("Then ", "I expect a certain outcome", 999999L));
		scenario2.addStep(matchedStepWith("Then ", "another outcome", 999999L));

		stubReportStore.setTestRuns(Arrays.asList(run1, run2));
		stubReportStore.setFeatures(Arrays.asList(feature1, feature2));
		stubReportStore.setScenarios(Arrays.asList(scenario1, scenario2));

		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private void givenIncompleteThenAfterScenario() {
		stubReportStore = new StubReportStore();

		TestRun run1 = new TestRun(1, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature1 = FeatureModelShould.modelWithName("same feature");
		feature1.setTestRun(run1);
		ScenarioModel scenario1 = ScenarioModelShould.modelWithName("scenario");
		scenario1.setNextDvId("1111111111");
		feature1.addScenario(scenario1);
		scenario1.addStep(matchedStepWith("Given ", "a certain context", 100));
		scenario1.addStep(matchedStepWith("When ", "an action is performed", 100));
		scenario1.addStep(matchedStepWith("Then ", "after 03:00 hr I expect a certain outcome", 100));

		stubReportStore.setTestRuns(Arrays.asList(run1));
		stubReportStore.setFeatures(Arrays.asList(feature1));
		stubReportStore.setScenarios(Arrays.asList(scenario1));

		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private void givenThenAfterScenarioInThreeTestRuns() {
		stubReportStore = new StubReportStore();

		TestRun run1 = new TestRun(1, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature1 = FeatureModelShould.modelWithName("same feature");
		feature1.setTestRun(run1);
		ScenarioModel scenario1 = ScenarioModelShould.modelWithName("scenario");
		scenario1.setNextDvId("1111111111");
		feature1.addScenario(scenario1);
		scenario1.addStep(matchedStepWith("Given ", "a certain context", 100));
		scenario1.addStep(matchedStepWith("When ", "an action is performed", 100));
		scenario1.addStep(matchedStepWith("Then ", "after 03:00 hr I expect a certain outcome", 100));

		TestRun run2 = new TestRun(2, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature2 = FeatureModelShould.modelWithName("same feature");
		feature2.setTestRun(run2);
		ScenarioModel scenario2 = ScenarioModelShould.modelWithName("scenario (dvId=1111111111)");
		scenario2.setTriggeringDvId("1111111111");
		scenario2.setNextDvId("2222222222");
		feature2.addScenario(scenario2);
		scenario2.addStep(matchedStepWith("Given ", "Test Execution Context is loaded for dvId=1111111111", 999999L));
		scenario2.addStep(matchedStepWithDatatableAnd("Then ", "I expect a certain outcome", 999999L));
		scenario2.addStep(matchedStepWith("Then ", "after 5:00 hr yet another outcome", 999999L));

		TestRun run3 = new TestRun(2, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature3 = FeatureModelShould.modelWithName("same feature");
		feature3.setTestRun(run3);
		ScenarioModel scenario3 = ScenarioModelShould.modelWithName("scenario (dvId=2222222222)");
		scenario3.setTriggeringDvId("2222222222");
		feature3.addScenario(scenario3);
		scenario3.addStep(matchedStepWith("Given ", "Test Execution Context is loaded for dvId=2222222222", 100L));
		scenario3.addStep(matchedStepWith("Then ", "yet another outcome", 100L));
		scenario3.addStep(matchedStepWith("Then ", "still another outcome", 100L));
		scenario3.addStep(matchedStepWith("Then ", "the final outcome", 100L));

		stubReportStore.setTestRuns(Arrays.asList(run1, run2, run3));
		stubReportStore.setFeatures(Arrays.asList(feature1, feature2, feature3));
		stubReportStore.setScenarios(Arrays.asList(scenario1, scenario2, scenario3));

		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private void givenTwoTestRunsWithDifferentFeatures() {
		stubReportStore = new StubReportStore();

		TestRun run1 = new TestRun(1, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature1 = FeatureModelShould.modelWithName("same feature");
		feature1.setTestRun(run1);
		ScenarioModel scenario1 = ScenarioModelShould.modelWithName("scenario");
		feature1.addScenario(scenario1);

		TestRun run2 = new TestRun(2, "", "", DateTime.now(), DateTime.now());
		FeatureModel feature2 = FeatureModelShould.modelWithName("a different feature");
		feature2.setTestRun(run2);
		ScenarioModel scenario2 = ScenarioModelShould.modelWithName("a scenario");
		feature2.addScenario(scenario2);

		stubReportStore.setTestRuns(Arrays.asList(run1, run2));
		stubReportStore.setFeatures(Arrays.asList(feature1, feature2));

		reporter = new ReportBuilder();
		reporter.setReportStore(stubReportStore);
	}

	private StepModel matchedStepWith(String keyword, String name, long duration) {
		Step step = new Step(Collections.emptyList(), keyword, name, -1, Collections.emptyList(), null);
		StepModel model = new StepModel(step);
		model.setMatch(new Match(Collections.emptyList(), "aClass:aMethodName"));
		model.setResult(new Result("passed", duration, null));
		return model;
	}

	private StepModel matchedStepWithDatatableAnd(String keyword, String name, long duration) {
		List<DataTableRow> rows = Arrays.asList(
				new DataTableRow(Collections.emptyList(), Arrays.asList("h1", "h2"), -1),
				new DataTableRow(Collections.emptyList(), Arrays.asList("v1", "v2"), -1));
		Step step = new Step(Collections.emptyList(), keyword, name, -1, rows, null);
		StepModel model = new StepModel(step);
		model.setMatch(new Match(Collections.emptyList(), "aClass:aMethodName"));
		model.setResult(new Result("passed", duration, null));
		return model;
	}
}
