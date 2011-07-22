package com.nc201.scrum.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nc201.common.util.DateUtil;
import com.nc201.common.util.ObjectUtil;
import com.nc201.scrum.SystemConstants;
import com.nc201.scrum.exception.ScrumException;
import com.nc201.scrum.job.ProjectJobTimer;
import com.nc201.scrum.model.Project;
import com.nc201.scrum.model.Sprint;
import com.nc201.scrum.model.TaskWall;

/**
 * 项目servlet
 * 
 * @author zhuzf
 * 
 */
@SuppressWarnings("serial")
public class ProjectServlet extends HttpServlet {
	/**
	 * 启动方法，整个系统只执行一次
	 */
	public void init() {
		SystemConstants.WEB_ROOT = this.getServletContext().getRealPath("/");
		SystemConstants.PROJECT_ROOT = SystemConstants.WEB_ROOT
				+ "/WEB-INF/project/";
		if (!new File(SystemConstants.PROJECT_ROOT).exists()) {
			new File(SystemConstants.PROJECT_ROOT).mkdirs();
		}
		// 定时器
		try {
			new ProjectJobTimer().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("WEB_ROOT:" + SystemConstants.WEB_ROOT);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doService(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doService(request, response);
	}

	/**
	 * Dispatch
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String result = "";

		String handle = request.getParameter("handle");

		boolean isXml = false;
		if ("getAllProject".equals(handle)) {
			result = getAllProject(request, response);
		} else if ("createProject".equals(handle)) {
			result = createProject(request, response);
		} else if ("createSprint".equals(handle)) {
			result = createSprint(request, response);
		} else if ("getTaskWall".equals(handle)) {
			result = getTaskWall(request, response);
		} else if ("lockTaskWall".equals(handle)) {
			result = lockTaskWall(request, response);
		} else if ("unlockTaskWall".equals(handle)) {
			result = unlockTaskWall(request, response);
		} else if ("getBurndown".equals(handle)) {
			result = getBurndown(request, response);
			isXml = true;
		} else if ("toXls".equals(handle)) {
			toXls(request, response);
			return;
		}

		response.setCharacterEncoding("UTF-8");
		if (isXml) {
			response.setContentType("text/xml");
		} else {
			response.setContentType("text/plain");
		}

		response.getWriter().write(result);
	}



	/**
	 * 得到所有项目信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getAllProject(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<Project> projects = Project.getAllProject();
		return ObjectUtil.toJson(projects);
	}

	/**
	 * 创建项目
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String createProject(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String projName = request.getParameter("projectName");
		try {
			Project.createNew(projName);
			return SystemConstants.ok();
		} catch (RuntimeException e) {
			return SystemConstants.result(e.getMessage());
		}
	}

	/**
	 * 创建sprint
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String createSprint(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String projectName = request.getParameter("projectName");
		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		try {
			Sprint sprint = new Sprint();
			sprint.setProjectName(projectName);
			sprint.setStartDate(DateUtil.parse(startDateStr));
			sprint.setEndDate(DateUtil.parse(endDateStr));
			Sprint.createNew(sprint);

			return SystemConstants.ok();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SystemConstants.result(e.getMessage());
		}
	}

	/**
	 * 得到任务墙信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getTaskWall(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sprintName = request.getParameter("sprintName");
		String taskDateStr = request.getParameter("taskDate");
		if (sprintName == null || "".equals(sprintName.trim())) {
			String[] sprintNames = Sprint.getAllSprintName(request
					.getParameter("projectName"));
			sprintName = sprintNames[sprintNames.length - 1];
		}
		Sprint sprint = new Sprint(request.getParameter("projectName"),
				sprintName);

		Date taskDate = null;
		if (taskDateStr == null || "".equals(taskDateStr.trim())) {
			taskDate = new Date();
		} else {
			taskDate = DateUtil.parse(taskDateStr);
		}
		return ObjectUtil.toJson(sprint.getTaskWall(taskDate));
	}

	/**
	 * 锁定任务墙
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String lockTaskWall(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getRemoteAddr();

		String projectName = request.getParameter("projectName");
		String sprintName = request.getParameter("sprintName");
		String taskDateStr = request.getParameter("taskDate");

		TaskWall taskWall = new TaskWall(projectName, sprintName, taskDateStr);

		try {
			taskWall.lock(userId);
			return SystemConstants.ok();
		} catch (ScrumException e1) {
			e1.printStackTrace();
			return SystemConstants.result(e1.getMessage());
		}
	}

	/**
	 * 解锁任务墙
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String unlockTaskWall(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getRemoteAddr();

		String projectName = request.getParameter("projectName");
		String sprintName = request.getParameter("sprintName");
		String taskDateStr = request.getParameter("taskDate");

		TaskWall taskWall = new TaskWall(projectName, sprintName, taskDateStr);
		taskWall.setTaskXml(request.getParameter("taskXml"));
		try {
			taskWall.unlock(userId);
			return SystemConstants.ok();
		} catch (ScrumException e1) {
			e1.printStackTrace();
			return SystemConstants.result(e1.getMessage());
		}

	}

	/**
	 * 得到燃尽图
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getBurndown(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sprintName = request.getParameter("sprintName");
		String taskDateStr = request.getParameter("taskDate");
		if (sprintName == null || "".equals(sprintName.trim())) {
			String[] sprintNames = Sprint.getAllSprintName(request
					.getParameter("projectName"));
			sprintName = sprintNames[sprintNames.length - 1];
		}
		Sprint sprint = Sprint.getSprint(request.getParameter("projectName"),
				sprintName);// new
							// Sprint(request.getParameter("projectName"),sprintName);

		Date taskDate = null;
		if (taskDateStr == null || "".equals(taskDateStr.trim())) {
			taskDate = new Date();
		} else {
			taskDate = DateUtil.parse(taskDateStr);
		}
		return sprint.getBurnDownFusionChart(taskDate);
	}
	/**
	 * 下载excel
	 * @param request
	 * @param response
	 */
	private void toXls(HttpServletRequest request, HttpServletResponse response) {

		String sprintName = request.getParameter("sprintName");
		String taskDateStr = request.getParameter("taskDate");
		if (sprintName == null || "".equals(sprintName.trim())) {
			String[] sprintNames = Sprint.getAllSprintName(request
					.getParameter("projectName"));
			sprintName = sprintNames[sprintNames.length - 1];
		}
		Sprint sprint = new Sprint(request.getParameter("projectName"),
				sprintName);

		Date taskDate = null;
		if (taskDateStr == null || "".equals(taskDateStr.trim())) {
			taskDate = new Date();
		} else {
			taskDate = DateUtil.parse(taskDateStr);
		}
		sprint.getTaskWall(taskDate).exportXLS(response);
		//return ObjectUtil.toJson(sprint.getTaskWall(taskDate));
		
		
	}	

}
