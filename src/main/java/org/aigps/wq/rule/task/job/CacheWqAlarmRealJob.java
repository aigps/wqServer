/**
 * 
 */
package org.aigps.wq.rule.task.job;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.infinispan.wq.WqMemoryCache;
import org.gps.model.wq.WqAlarmInfo;
import org.gps.wq.WqDataCache;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Administrator
 *
 */
public class CacheWqAlarmRealJob  implements Job{
	private static final Log log = LogFactory.getLog(CacheWqAlarmRealJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public static final String ID="CacheWqAlarmRealJob";
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(!isRunning){
			isRunning = true;
			try {
				HashMap<String, HashMap<String, WqAlarmInfo>> realAlarmMap = WqDataCache.getRealAlarmMap();
				WqMemoryCache.saveWqRealAlarm(realAlarmMap);
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
