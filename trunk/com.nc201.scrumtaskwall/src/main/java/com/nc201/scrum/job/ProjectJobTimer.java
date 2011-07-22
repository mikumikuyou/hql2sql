package com.nc201.scrum.job;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务
 * @author zhuzhf
 *
 */
public class ProjectJobTimer {


    public void run() throws Exception {

        //System.out.println("------- Initializing -------------------");

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        // job 1 will run every 20 seconds
        JobDetail job = new JobDetail("job1", "group1", ProjectJob.class);
        CronTrigger trigger = new CronTrigger("trigger1", "group1", "job1",
                "group1", "0/1800 * * * * ?");
        sched.addJob(job, true);
        sched.scheduleJob(trigger);

        sched.start();

    }

}
