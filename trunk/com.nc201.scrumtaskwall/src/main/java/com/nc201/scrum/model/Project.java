package com.nc201.scrum.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nc201.scrum.SystemConstants;
import com.nc201.scrum.exception.ScrumException;

/**
 * 项目
 * 
 * @author zhuzhf
 * 
 */
public class Project {
	/**
	 * 项目名称
	 */
	private String projectName;
	/**
	 * 所有sprint
	 */
	private List<Sprint> sprints;

	public List<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(List<Sprint> sprints) {
		this.sprints = sprints;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Project() {
	}

	public Project(String projectName) {
		super();
		this.projectName = projectName;
	}

	/**
	 * 得到所有项目
	 * 
	 * @return
	 */
	public static List<Project> getAllProject() {
		File file = new File(SystemConstants.PROJECT_ROOT);
		File[] files = file.listFiles();
		List<Project> projects = new ArrayList<Project>();
		if (files == null || files.length == 0) {
		} else {
			for (int i = 0; i < files.length; i++) {
				projects.add(getProject(files[i].getName()));
			}
		}
		return projects;
	}

	/**
	 * 根据项目名称，得到项目
	 * 
	 * @param projectName
	 * @return
	 */
	public static Project getProject(String projectName) {
		Project project = new Project(projectName);
		project.setSprints(Sprint.getAllSprint(projectName));
		return project;
	}

	/**
	 * 创建项目
	 * 
	 * @param projName
	 */
	public static void createNew(String projName) {
		File file = new File(SystemConstants.PROJECT_ROOT + projName);
		if (file.exists()) {
			throw new ScrumException("项目已存在：" + projName);
		} else {
			file.mkdirs();
		}

	}

	/**
	 * 创建一个sprint
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void createSprint(Date startDate, Date endDate) {
		Sprint sprint = new Sprint();
		sprint.setProjectName(projectName);
		sprint.setStartDate(startDate);
		sprint.setEndDate(endDate);

		Sprint.createNew(sprint);
	}
}
