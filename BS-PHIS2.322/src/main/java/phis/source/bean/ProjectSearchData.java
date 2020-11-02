package phis.source.bean;

import java.util.List;

public class ProjectSearchData {
	private Long count;
	private List<Project> project;
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Project> getProject() {
		return project;
	}
	public void setProject(List<Project> project) {
		this.project = project;
	}
}
