/**
 * 
 */
package org.aigps.wq.task.job;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * @author Administrator
 *
 */
public class DbGpsRealSaveJob implements Job {
	private static final Log log = LogFactory.getLog(DbGpsRealSaveJob.class);
	private static AtomicBoolean isRunning = new AtomicBoolean();//ͬһ��ʱ��㣬ֻ����һ��job����
	public static final String ID="DbGpsRealSaveJob";
	/**
	 * �䶯���Ķ�λ��Ϣ
	 */
	private static Map<String,GisPosition> swapGps = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();

	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			Map<String,GisPosition> gpsRealMap = DcGpsCache.gpsChangeCache;
			DcGpsCache.gpsChangeCache = swapGps;
			try {
				GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
				if(gpsRealMap!=null && gpsRealMap.size()>0){
					gpsDataDao.saveGpsReal(gpsRealMap.values());
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				gpsRealMap.clear();
				swapGps = gpsRealMap;
				isRunning.set(false);
			}
		}
	}

}
