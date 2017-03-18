package io.pickles.reporting;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.pickles.model.FeatureModel;
import io.pickles.model.ScenarioModel;
import io.pickles.model.StepModel;
import io.pickles.model.TestRun;

public class ReportBuilder {

	private ReportStore reportStore;

	public void setReportStore(ReportStore reportStore) {
		this.reportStore = reportStore;
	}

	public JsonElement generate(List<TestRun> testRuns) {
		JsonArray featuresJson = new JsonArray();
		for (FeatureModel feature : featuresFor(testRuns)) {
			featuresJson.add(feature.toDeepJsonObject());
		}

		return featuresJson;
	}

	private Collection<FeatureModel> featuresFor(List<TestRun> testRuns) {
		Map<String, FeatureModel> results = new HashMap<>();
		List<FeatureModel> features = reportStore.readAllFeaturesFor(testRuns);
		for (FeatureModel feature : features) {
			feature.keepInitiatingScenariosOnly();
			if (!results.containsKey(feature.getName())) {
				results.put(feature.getName(), feature);
			} else {
				combine(results.get(feature.getName()), feature);
			}

			completeScenarios(feature);
		}
		return results.values();
	}

	private void combine(FeatureModel base, FeatureModel addition) {
		for (ScenarioModel scenario : addition.getScenarios()) {
			base.addScenario(scenario);
		}
	}

	private void completeScenarios(FeatureModel feature) {
		for (ScenarioModel scenario : feature.getScenarios()) {
			completeScenario(scenario);
		}
	}

	private void completeScenario(ScenarioModel scenario) {
		if (scenario.isFinalScenario()) {
			return;
		} else {
			ScenarioModel nextScenario = reportStore.findScenarioTriggeredBy(scenario.getNextDvId());
			if (nextScenario != null) {
				completeScenario(nextScenario);
				combine(scenario, nextScenario);
			}
		}
	}

	private void combine(ScenarioModel base, ScenarioModel addition) {
		List<StepModel> steps = addition.getSteps();
		base.getLastStep().setMatch(steps.get(1).getMatch());
		base.getLastStep().setResult(steps.get(1).getResult());
		base.getLastStep().setDatatable(steps.get(1).getDatatable());
		for (int i = 2; i < steps.size(); i++) {
			base.addStep(steps.get(i));
		}
	}

}
