package com.nc201.scrum.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nc201.common.util.DateUtil;
import com.nc201.common.util.FileUtil;
import com.nc201.scrum.SystemConstants;
import com.nc201.scrum.exception.ScrumException;

/**
 * scrum sprint
 * 
 * @author zhuzhf
 * 
 */
public class Sprint {

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * sprint名称
	 */
	private String sprintName;

	/**
	 * 开始时间
	 */
	private Date startDate;
	/**
	 * 结束时间
	 */
	private Date endDate;

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Sprint() {
	}

	public Sprint(String projectName, String sprintName) {
		super();
		this.projectName = projectName;
		this.sprintName = sprintName;
	}

	/**
	 * 创建一个sprint
	 * 
	 * @param sprint
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createNew(Sprint sprint) {
		if (sprint.getEndDate().getTime() <= sprint.getStartDate().getTime()) {
			throw new ScrumException("Sprint结束时间必须大于开始时间");
		}

		String projPath = SystemConstants.PROJECT_ROOT
				+ sprint.getProjectName();

		if (!new File(projPath).exists()) {
			throw new ScrumException("建立sprint出错：" + sprint.getProjectName()
					+ "还未建立");
		}
		String curSprintName;
		String[] sprintNames = getAllSprintName(sprint.getProjectName());
		if (sprintNames == null) {
			curSprintName = "1";
		} else {
			curSprintName = (Integer
					.parseInt(sprintNames[sprintNames.length - 1]) + 1) + "";
		}

		File sprintDir = new File(projPath + "/" + curSprintName);
		sprintDir.mkdir();
		Map info = new HashMap();
		// Properties props = new Properties();
		info.put("startDate",
				DateUtil.format(sprint.getStartDate(), "yyyy-MM-dd"));
		info.put("endDate", DateUtil.format(sprint.getEndDate(), "yyyy-MM-dd"));
		try {
			FileUtil.storeMap(info, new File(sprintDir.getPath()
					+ "/info.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ScrumException(e.getMessage());
		}

	}

	/**
	 * 得到所有sprint名称
	 * 
	 * @param projectName
	 * @return
	 */
	public static String[] getAllSprintName(String projectName) {
		String projPath = SystemConstants.PROJECT_ROOT + projectName;
		File[] files = new File(projPath).listFiles();
		if (files == null || files.length == 0) {
			return null;
		}
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].getName();
		}
		return fileNames;
	}

	/**
	 * 得到当日任务墙
	 * 
	 * @return
	 */
	public TaskWall getTaskWall(Date taskDate) {
		String sprintPath = SystemConstants.PROJECT_ROOT + projectName + "/"
				+ sprintName;
		File curTaskWallFile = new File(sprintPath + "/"
				+ DateUtil.format(taskDate, "yyyy-MM-dd") + ".xml");

		TaskWall taskWall = new TaskWall();
		taskWall.setProjectName(projectName);
		taskWall.setSprintName(sprintName);
		taskWall.setTaskDate(DateUtil.format(taskDate, "yyyy-MM-dd"));
		if (!curTaskWallFile.exists()) {
			// 获取迭代目录下面最新的文件作为当前日期的文件
			File[] childFiles = new File(sprintPath).listFiles();
			File lastestFile = null;
			for (int i = 0; i < childFiles.length; i++) {
				if (childFiles[i].getName().endsWith(".xml")) {
					lastestFile = childFiles[i];
				}
			}
			String taskXml = null;
			try {
				if (lastestFile != null) {
					taskXml = FileUtil.utf8ReadFile(lastestFile);
				} else {
					taskXml = FileUtil
							.utf8ReadFile(new File(SystemConstants.WEB_ROOT
									+ "/WEB-INF/def_task.xml"));
				}
				// FileUtil.outputFile(curTaskWallFile, taskXml, "UTF-8");
				taskWall.setTaskXml(taskXml);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ScrumException("读取文件出错:" + e.getMessage());
			}

		} else {
			try {
				taskWall.setTaskXml(FileUtil.utf8ReadFile(curTaskWallFile));
			} catch (Exception e) {
				throw new ScrumException("读取文件出错:" + e.getMessage());
			}
		}
		return taskWall;
	}

	/**
	 * 根据项目名称获得所有sprint
	 * 
	 * @param projectName
	 * @return
	 */
	public static List<Sprint> getAllSprint(String projectName) {
		String projPath = SystemConstants.PROJECT_ROOT + projectName;
		File[] files = new File(projPath).listFiles();
		List<Sprint> sprints = new ArrayList<Sprint>();
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				sprints.add(getSprint(projectName, files[i].getName()));
			}
		}
		return sprints;
	}

	/**
	 * 得到sprint
	 * 
	 * @param projectName
	 * @param sprintName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Sprint getSprint(String projectName, String sprintName) {
		Sprint sprint = new Sprint(projectName, sprintName);
		File sprintInfo = new File(SystemConstants.PROJECT_ROOT + projectName
				+ "/" + sprintName + "/info.properties");
		Map info = null;
		try {
			info = FileUtil.getMap(sprintInfo);
			sprint.setStartDate(DateUtil.parse(info.get("startDate")));
			sprint.setEndDate(DateUtil.parse(info.get("endDate")));
			return sprint;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ScrumException(e.getMessage());
		}
	}

	/**
	 * 得到燃尽图xml
	 * 
	 * @param taskDate
	 * @return
	 */
	public String getBurnDownFusionChart(Date taskDate) {
		Document document = DocumentHelper.createDocument();
		Element root = document
				.addElement("graph")
				.addAttribute("caption", "Burn down chart")
				.addAttribute("animation", "0")
				.addAttribute(
						"subcaption",
						DateUtil.format(this.getStartDate(), "yyyy-MM-dd")
								+ "~"
								+ DateUtil.format(this.getEndDate(),
										"yyyy-MM-dd"))
				.addAttribute("showvalues", "0")
				.addAttribute("rotateNames", "1");

		Element categories = root.addElement("categories");
		Date catDate = this.getStartDate();
		int totalDay = 0;
		while (true) {
			totalDay++;
			categories.addElement("category").addAttribute("name",
					DateUtil.format(catDate, "yyyy-MM-dd"));
			String nextDateStr = DateUtil.addDate("d", 1,
					DateUtil.format(catDate, "yyyy-MM-dd"));
			catDate = DateUtil.parse(nextDateStr);
			if (catDate.getTime() > this.getEndDate().getTime()) {
				break;
			}
		}
		Element planLine = root.addElement("dataset")
				.addAttribute("color", "F1683C")
				.addAttribute("anchorBorderColor", "F1683C")
				/*.addAttribute("anchorBgColor", "F1683C")*/;

		Element actLine = root.addElement("dataset")
				.addAttribute("color", "2AD62A")
				.addAttribute("anchorBorderColor", "2AD62A")
				/*.addAttribute("anchorBgColor", "2AD62A")*/;

		String sprintPath = SystemConstants.PROJECT_ROOT + projectName + "/"
				+ sprintName;

		List<String> taskList = new ArrayList<String>();

		File[] childFiles = new File(sprintPath).listFiles();
		if (childFiles != null && childFiles.length > 0) {
			// File lastestFile = null;
			File firstFile = null;
			for (int i = 0; i < childFiles.length; i++) {
				if (childFiles[i].getName().endsWith(".xml")) {
					if (firstFile == null) {
						firstFile = childFiles[i];

					}
					taskList.add(childFiles[i].getName().substring(0,
							childFiles[i].getName().length() - 4));
				}
			}
			if (firstFile != null) {
				Date firstDate = DateUtil.parse(firstFile.getName().substring(
						0, firstFile.getName().length() - 4));
				TaskWall taskWall = this.getTaskWall(firstDate);
				double totalSprintDay = taskWall.getRemainDays();
				double perData = totalSprintDay / totalDay;
				for (int i = 0; i < totalDay; i++) {
					planLine.addElement("set").addAttribute("value",
							(totalSprintDay - perData * i) + "");
				}
				for (int i = 0; i < taskList.size(); i++) {
					String aTaskDate = taskList.get(i);
					double curRemainDays = 0;

					if (i == 0) {
						curRemainDays = totalSprintDay;
					} else {
						curRemainDays = this.getTaskWall(
								DateUtil.parse(aTaskDate)).getRemainDays();
					}
					actLine.addElement("set")
							.addAttribute("value", curRemainDays + "")
							//.addAttribute("alpha", "100")
							.addAttribute(
									"link",
									"taskwall.jsp?handle=getTaskWall&projectName="
											+ projectName + "&sprintName="
											+ sprintName + "&taskDate="
											+ aTaskDate);
					// 连续
					if (i + 1 < taskList.size()) {
						int cday = DateUtil.compareDateOnDay(
								DateUtil.parse(taskList.get(i + 1)),
								DateUtil.parse(aTaskDate));
						if (cday > 1) {
							for (int j = 0; j < cday - 1; j++) {
/*								String bDate = DateUtil.addDate("d", j + 1,
										aTaskDate);*/
								actLine.addElement("set")
										.addAttribute("alpha", "50")
										.addAttribute("value",
												curRemainDays + "")
										/*.addAttribute(
												"link",
												"taskwall.jsp?handle=getTaskWall&projectName="
														+ projectName
														+ "&sprintName="
														+ sprintName
														+ "&taskDate="
														+ bDate)*/;
							}
						}
					}
				}

			}

		}

		return document.asXML();
	}

}
