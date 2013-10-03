package org.aigps.wq.task.job;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.util.MapTileUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sunleads.dc.GeoCache;
/**
 * ����ʵʱ����λ�õĶ�λ��Ϣ
 * @author Administrator
 *
 */
public class ParseGpsLocDescJob implements Job {
	private static final Log log = LogFactory.getLog(ParseGpsLocDescJob.class);
	public static final String ID="ParseGpsLocDescJob";
	private static AtomicBoolean isRunning = new AtomicBoolean();//ͬһ��ʱ��㣬ֻ����һ��job����
	
	private static BasicThreadFactory tfactory = new BasicThreadFactory.Builder().namingPattern("parseGpsLoc-pool-%d").daemon(true).build();
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(15, 15, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(500000),tfactory);
	private static Set<String> runSet = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());

	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){//�����ǰjobû�����У��򴥷�
			try {
				execute();
			} catch (Throwable e) {
				log.error("",e);
			}
			finally{
				isRunning.set(false);;
			}
		}
	}

	private void execute() throws Throwable{
		for(final GisPosition gps : DcGpsCache.getLastGpsReport().values() ){
			if(runSet.contains(gps.getTmnKey()) || !needToGeo(gps)) {
				continue;
			}
			runSet.add(gps.getTmnKey());
			pool.execute(new Runnable(){
				public void run() {
					try {
						String geo = GeoCache.getGeo(gps.getLon(), gps.getLat());
						if(StringUtils.isBlank(geo)) {
							return;
						}
						GisPosition newLoc = setNewLoc(gps, geo);
						DcGpsCache.setVhcLocDesc(gps.getTmnKey(), newLoc);
					} catch (Throwable e) {
						log.error(e.getMessage());
					} finally {
						runSet.remove(gps.getTmnKey());
					}
				}
			});
		}
	}
	
	private static boolean needToGeo(GisPosition gps){
		try {
			double lng = gps.getLon();
			double lat = gps.getLat();
			if(lng<=0 || lat<=0 || lng>180 || lat>60){
				return false;
			}
			GisPosition preLoc = DcGpsCache.getVhcLocDesc(gps.getTmnKey());
			if(preLoc == null) {
				return true;
			}
			if (lng==preLoc.getLon() && lat==preLoc.getLat()) {
				return false;
			}
			if (preLoc.getRptTime().compareToIgnoreCase( gps.getRptTime()) >=0) {// ������λ��������
				return false;
			}
			
			int[] prevXY = MapTileUtil.getXY(preLoc.getLon(),preLoc.getLat());
			int[] nowXY = MapTileUtil.getXY(lng,lat);
			
			//��ͬһ����ͼ��Ƭ�ϣ������н���
			if(prevXY[0] == nowXY[0] && prevXY[1] == nowXY[1]) {
				return false;
			}
			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}

	private static GisPosition setNewLoc(GisPosition gps, String geo) throws Exception {
		gps.setLocDesc(geo);
		return gps;
	}

}
