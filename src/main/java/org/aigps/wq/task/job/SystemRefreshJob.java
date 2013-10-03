/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * �ն���ҵ��ID�Ķ��ո���
 * @author Administrator
 *
 */
public class SystemRefreshJob implements Job {
	private static final Log log = LogFactory.getLog(SystemRefreshJob.class);
	public static final String ID="TmnSysIdRefreshJob";
	private static AtomicBoolean isRunning = new AtomicBoolean();//ͬһ��ʱ��㣬ֻ����һ��job����
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			try {
				DcGpsCache.refresh();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}finally{
				isRunning.set(false);;
			}
		}
	}
}
