/**
 * 
 */
package org.aigps.wq.rule.task.job;

import org.aigps.wq.rule.RuleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class DbWqRefreshJob implements Job {
	private static final Log log = LogFactory.getLog(DbWqRefreshJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="DbWqRefreshJob";
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning  = true;
			try {
				RuleCache ruleCache = RuleCache.context.getBean("ruleCache",RuleCache.class);
				ruleCache.refresh();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning = false;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
