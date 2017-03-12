package io.pickles.model;

import org.joda.time.DateTime;

public class TestRun {

	private Integer id;
	private String name;
	private String description;
	private DateTime startedAt;
	private DateTime finishedAt;

	public TestRun(String name, String description, DateTime startedAt) {
		this(null, name, description, startedAt, null);
	}

	public TestRun(String name, String description, DateTime startedAt, DateTime finishedAt) {
		this(null, name, description, startedAt, finishedAt);
	}

	public TestRun(Integer id, String name, String description, DateTime startedAt, DateTime finishedAt) {
		setId(id);
		setName(name);
		setDescription(description);
		setStartedAt(startedAt);
		setFinishedAt(finishedAt);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(DateTime startedAt) {
		this.startedAt = startedAt;
	}

	public DateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(DateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

}
