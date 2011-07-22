package com.nc201.scrum.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nc201.common.util.DateUtil;
import com.nc201.common.util.FileUtil;
import com.nc201.scrum.SystemConstants;
import com.nc201.scrum.exception.ScrumException;

/**
 * 任务墙
 * 
 * @author zhuzhf
 * 
 */
public class TaskWall {

	/**
	 * 项目名称
	 */
	private String projectName;
	/**
	 * sprintName
	 */
	private String sprintName;

	/**
	 * 任务日期
	 */
	private String taskDate;
	/**
	 * 任务xml
	 */
	private String taskXml;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getSprintName() {
		return sprintName;
	}

	public void setSprintName(String sprintName) {
		this.sprintName = sprintName;
	}

	public String getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}

	public String getTaskXml() {
		return taskXml;
	}

	public void setTaskXml(String taskXml) {
		this.taskXml = taskXml;
	}

	public TaskWall() {
	}

	public TaskWall(String projectName, String sprintName, String taskDate) {
		super();
		this.projectName = projectName;
		this.sprintName = sprintName;
		this.taskDate = taskDate;
	}

	/**
	 * 锁定任务墙
	 * 
	 * @param lockTag
	 *            锁标记
	 * @return
	 */
	public void lock(String lockTag) {
		String taskXmlLockPath = SystemConstants.PROJECT_ROOT + projectName
				+ "/" + sprintName + "/" + taskDate + ".xml.lck";

		String taskXmlPath = SystemConstants.PROJECT_ROOT + projectName + "/"
				+ sprintName + "/" + taskDate + ".xml";

		File taskXmlFile = new File(taskXmlPath);
		if (!taskXmlFile.exists()) {

		}

		Map<String, String> lockInfo = null;
		if (new File(taskXmlLockPath).exists()) {
			try {
				lockInfo = FileUtil.getMap(new File(taskXmlLockPath));
				// String lfc = "";
				// lfc = FileUtil.utf8ReadFile(new File(taskXmlLockPath));
				if (!lockInfo.get("lockTag").equals(lockTag)) {
					String lockTime = DateUtil.format(new Date(Long
							.parseLong(lockInfo.get("lockTag"))));
					throw new ScrumException("文件已于" + lockTime + "被用户（"
							+ lockTag + "）锁定， 不能重复锁定");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ScrumException(e.getMessage());
			}
		}
		lockInfo = new HashMap<String, String>();
		lockInfo.put("lockTag", lockTag);
		lockInfo.put("lockTime", new Date().getTime() + "");
		try {
			FileUtil.storeMap(lockInfo, new File(taskXmlLockPath));// .outputFile(new
																	// File(taskXmlLockPath),
																	// lockTag,
																	// "UTF-8");
		} catch (Exception e) {
			throw new ScrumException("写xml锁文件出错：" + taskXmlLockPath);
		}

	}

	/**
	 * 解锁任务墙
	 * 
	 * @param lockTag
	 *            锁标记
	 * @return
	 */
	public void unlock(String lockTag) {
		String taskXmlLockPath = SystemConstants.PROJECT_ROOT + projectName
				+ "/" + sprintName + "/" + taskDate + ".xml.lck";
		String taskXmlPath = SystemConstants.PROJECT_ROOT + projectName + "/"
				+ sprintName + "/" + taskDate + ".xml";

		if (!new File(taskXmlLockPath).exists()) {
			throw new ScrumException("不能获得用户锁");
		} else {
			try {
				Map<String, String> lockInfo = FileUtil.getMap(new File(
						taskXmlLockPath));
				if (!lockInfo.get("lockTag").equals(lockTag)) {
					throw new ScrumException("文件不是由您锁定，不能解锁");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ScrumException(e.getMessage());
			}
		}
		try {
			FileUtil.outputFile(new File(taskXmlPath), taskXml, "UTF-8");
			FileUtil.deleteFile(taskXmlLockPath);
		} catch (Exception e) {
			throw new ScrumException("写taskXml文件出错：" + taskXmlLockPath);
		}
	}

	/**
	 * 得到任务墙的剩余工作量
	 * <p>
	 * 计算规则 :sum(notChecked.initDay)+sum(checked.remainDay)
	 * </p>
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public double getRemainDays() {
		Document document = null;
		double totalDay = 0;

		try {
			document = DocumentHelper.parseText(this.getTaskXml());
			List list = document.selectNodes("//Task");
			if (list != null) {
				for (Iterator iter = list.iterator(); iter.hasNext();) {
					Element task = (Element) iter.next();
					String status = task.attributeValue("status");
					if ("notChecked".equals(status)) {
						try {
							totalDay += Double.parseDouble(task
									.attributeValue("initDay"));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					} else if ("checked".equals(status)) {
						try {
							totalDay += Double.parseDouble(task
									.attributeValue("remainDay"));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}

				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ScrumException(e.getMessage());
		}

		return totalDay;
	}

	/**
	 * 导出excel
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void exportXLS(HttpServletResponse response) {
		InputStream myxls = null;
		Document document = null;
		try {
			myxls =new FileInputStream(new File(SystemConstants.WEB_ROOT
					+ "/WEB-INF/excelTemplate.xls"));
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); 
			
			document = DocumentHelper.parseText(this.getTaskXml());
			int i=1;
			List list = document.selectNodes("//Task");
			if (list != null) {
				for (Iterator iter = list.iterator(); iter.hasNext();) {
					Element task = (Element) iter.next();
					//String status = task.attributeValue("status");
					
					int rowNum=i;
					HSSFRow row = sheet.getRow(rowNum); 
					HSSFCell cell = row.getCell((short) 0); 
					cell.setCellValue(i++);
					
					HSSFRow row1 = sheet.getRow(rowNum); 
					HSSFCell cell1 = row1.getCell((short) 1); 
					cell1.setCellValue(nullSet(task.attributeValue("title")));	
					
					HSSFRow row2 = sheet.getRow(rowNum); 
					HSSFCell cell2 = row2.getCell((short) 2); 
					cell2.setCellValue(nullSet(task.attributeValue("charge")));	
					
					HSSFRow row3 = sheet.getRow(rowNum); 
					HSSFCell cell3 = row3.getCell((short) 3); 
					cell3.setCellValue("5");	
					
					HSSFRow row4 = sheet.getRow(rowNum); 
					HSSFCell cell4 = row4.getCell((short) 4); 
					cell4.setCellValue("2");	
					
					HSSFRow row5 = sheet.getRow(rowNum); 
					HSSFCell cell5 = row5.getCell((short) 5); 
					cell5.setCellValue("已完成");	
					
					if("notChecked".equals(task.attributeValue("status"))){
						cell3.setCellValue(nullSet(task.attributeValue("initDay")));	
						cell4.setCellValue(nullSet(task.attributeValue("initDay")));						
						cell5.setCellValue("未开始");
					}else if("checked".equals(task.attributeValue("status"))){
						cell3.setCellValue(nullSet(task.attributeValue("initDay")));	
						cell4.setCellValue(nullSet(task.attributeValue("remainDay")));
						cell5.setCellValue("进行中");
					}else if("done".equals(task.attributeValue("status"))){
						cell3.setCellValue(nullSet(task.attributeValue("initDay")));	
						cell4.setCellValue(nullSet(task.attributeValue("0")));
						cell5.setCellValue("已完成");
					}
					
				}
			}
			
			response.reset();   
			response.setContentType("application/x-msdownload");   
			response.addHeader("Content-Disposition","attachment;filename=" + projectName+"_"+sprintName+"_"+this.taskDate+".xls");  
			OutputStream finleOut = (OutputStream)response.getOutputStream();   
		    wb.write(finleOut);   
		    response.flushBuffer();  
		    finleOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ScrumException(e.getMessage());
		}finally{
			try {
				myxls.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String nullSet(String s){
		return s==null?"":s;
	}
}
