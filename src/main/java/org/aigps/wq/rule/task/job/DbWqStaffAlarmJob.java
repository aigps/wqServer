/**
 * 
 */
package org.aigps.wq.rule.task.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.entity.WqAlarmInfo;
import org.aigps.wq.rule.RuleCache;
import org.aigps.wq.rule.RuleDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbWqStaffAlarmJob implements Job {
	private static final Log log = LogFactory.getLog(DbWqStaffAlarmJob.class);
	private static AtomicBoolean isRunning = new AtomicBoolean();//同一个时间点，只允许一个job跑数
	public static final String ID="DbWqStaffAlarmJob";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			List<WqAlarmInfo> alarmList = RuleCache.newWqAlarmList;
			try {
				RuleCache.newWqAlarmList = new ArrayList<WqAlarmInfo>(1000);
				if(alarmList!=null && alarmList.size()>0){
					RuleDao ruleDao = RuleCache.context.getBean("ruleDao", RuleDao.class);
					ruleDao.saveWqAlarmInfo(alarmList);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				alarmList.clear();
				isRunning.set(false);
			}
		}
	}

}
