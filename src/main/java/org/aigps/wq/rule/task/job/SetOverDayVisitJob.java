package org.aigps.wq.rule.task.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SetOverDayVisitJob implements Job {
	private static final Log log = LogFactory.getLog(SetOverDayVisitJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="SetOverDayVisitJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}

}
