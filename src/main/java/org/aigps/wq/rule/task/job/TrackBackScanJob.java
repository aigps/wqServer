package org.aigps.wq.rule.task.job;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aigps.wq.entity.WqRetrospect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TrackBackScanJob implements Job {
	private static final Log log = LogFactory.getLog(TrackBackScanJob.class);
	private static boolean isRunning = false;//同一个时间点，只允许一个job跑数
	public  static final String ID="TrackBackScanJob";
	private static BlockingQueue taskQueue = new ArrayBlockingQueue(2000);
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(10,20,30,TimeUnit.SECONDS,taskQueue,new ThreadPoolExecutor.CallerRunsPolicy());

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			if(!isRunning){
//				isRunning = true;
//				WqDataDao.updateTrackBackState(WqRetrospect.STATE_NO_RUNNING, WqRetrospect.STATE_GET);
//				List<WqRetrospect> list = WqDataDao.getTrackBackState(WqRetrospect.STATE_GET);
//				if(list!=null && list.size()>0){
//					WqDataCache.setRegionMap(WqDataDao.getWqMapRegion());
//				}
//				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//					final WqRetrospect wqRetrospect = (WqRetrospect) iterator.next();
//					pool.execute(new Runnable(){
//						public void run() {
//							try {
//								WqTraceBackHandler.trackBack(wqRetrospect);
//							} catch (Exception e) {
//								log.error(e.getMessage(), e);
//							}
//						}
//					});
//				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			isRunning = false;
		}
	}

}
