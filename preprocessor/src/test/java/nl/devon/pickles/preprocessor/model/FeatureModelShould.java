package nl.devon.pickles.preprocessor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Tag;

public class FeatureModelShould {

	@Test
	public void haveCucumberFeature() {
		List<Comment> comments = new ArrayList<>();
		String keyword = "Feature";
		String name = "Cucumber feature";
		String description = "description";
		Integer line = 1;
		String id = "id";
		List<Tag> tags = new ArrayList<>();
		Feature feature = new Feature(comments, tags, keyword, name, description, line, id);
		FeatureModel wrapped = new FeatureModel();
		wrapped.setFeature(feature);

		assertThat(wrapped.getFeature(), is(feature));
		assertThat(wrapped.getFeature().getName(), is("Cucumber feature"));
	}
}
