package io.pickles.preprocessor.stubs;

import java.util.ArrayList;
import java.util.List;

public class SampleFeatureTemplates {

	public static List<String> oneScenarioFeature() {
		List<String> template = new ArrayList<>();

		template.add("@FeatureTag");
		template.add("Feature: feature name");
		template.add("");
		template.add("Description");
		template.add("");
		template.add("@ScenarioTag1");
		template.add("Scenario: scenario name");
		template.add("Given a precondition");
		template.add("When an action");
		template.add("Then an outcome");

		return template;
	}

	public static List<String> twoThenAfterScenario() {
		List<String> template = new ArrayList<>();

		template.add("@FeatureTag");
		template.add("Feature: feature name");
		template.add("");
		template.add("Description");
		template.add("");
		template.add("@ScenarioTag1");
		template.add("Scenario: scenario name");
		template.add("Given a precondition");
		template.add("When an action");
		template.add("Then after 02:00 hr a first delayed outcome:");
		template.add("| header 1 | header 2 |");
		template.add("| r1 v1 | r1 v2|");
		template.add("| r2 v1 | r2 v2|");
		template.add("Then some direct verification");
		template.add("Then after 01:00 hr a second delayed outcome");

		return template;
	}

	public static List<String> simmpleFeatureTemplate() {
		List<String> template = new ArrayList<>();

		template.add("@FeatureTag1");
		template.add("@FeatureTag2a @FeatureTag2b");
		template.add("Feature: feature name");
		template.add("");
		template.add("Description");
		template.add("");
		template.add("Description");
		template.add("");
		template.add("# Scenario comment");
		template.add("");
		template.add("@ScenarioTag1");
		template.add("Scenario: scenario name");
		template.add("");
		template.add("#Step comment");
		template.add("");
		template.add("Given a step");

		return template;
	}

}
