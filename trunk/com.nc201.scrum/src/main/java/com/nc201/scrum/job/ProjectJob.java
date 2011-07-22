package com.nc201.scrum.job;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nc201.common.util.FileUtil;
import com.nc201.scrum.SystemConstants;

public class ProjectJob implements Job {
	private long OVER_TIME = 30 * 60 * 1000;

	/**
	 * 定时删除超时锁文件
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// System.out.println("开始删除锁:"+DateUtil.format(new Date(),
		// "yyyy-MM-dd HH:mm:ss"));
		deleteLck(new File(SystemConstants.PROJECT_ROOT));
		// System.out.println("结束删除锁");
	}

	/**
	 * 递归删除锁文件
	 * 
	 * @param root
	 */
	private void deleteLck(File root) {
		File[] files = root.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				deleteLck(files[i]);
			} else if (files[i].isFile()) {
				if (files[i].getName().endsWith(".lck")) {
					Map<String, String> lckInfo = null;
					try {
						lckInfo = FileUtil.getMap(files[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Date lockTime = new Date(Long.parseLong(lckInfo
							.get("lockTime")));
					if ((new Date().getTime() - lockTime.getTime()) > OVER_TIME) {
						System.out.println("删除超时锁文件：" + files[i].getPath());
						files[i].delete();
					}
				}
			}
		}
	}
}
