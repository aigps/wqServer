package org.aigps.wq.rule.task.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SetOverDayVisitJob implements Job {
	private static final Log log = LogFactory.getLog(SetOverDayVisitJob.class);
	private static boolean isRunning = false;//ͬһ��ʱ��㣬ֻ����һ��job����
	public static final String ID="SetOverDayVisitJob";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}

}
