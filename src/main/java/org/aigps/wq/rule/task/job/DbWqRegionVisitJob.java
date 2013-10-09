package org.aigps.wq.rule.task.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aigps.wq.entity.WqRegionVisit;
import org.aigps.wq.rule.RuleCache;
import org.aigps.wq.rule.RuleDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
/**
 * �½����������
 * @author Administrator
 *
 */
public class DbWqRegionVisitJob implements Job {
	private static final Log log = LogFactory.getLog(DbWqRegionVisitJob.class);
	private static AtomicBoolean isRunning = new AtomicBoolean();//ͬһ��ʱ��㣬ֻ����һ��job����
	public static final String ID="DbWqRegionVisitJob";
	private static Queue<WqRegionVisit> swapOutRegionQueue = new ConcurrentLinkedQueue<WqRegionVisit>();
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(isRunning.compareAndSet(false, true)){
			List<WqRegionVisit> newInList = null;
			Queue<WqRegionVisit> newOutList = null;
			try {
				//���浽��ʷ��
				newOutList = RuleCache.newOutRegionQueue;
				RuleCache.newOutRegionQueue = swapOutRegionQueue;
				swapOutRegionQueue = newOutList;
				RuleDao ruleDao = RuleCache.context.getBean("ruleDao", RuleDao.class);
				ruleDao.delWqRegionVisit(newOutList);
				ruleDao.saveWqRegionVisitHis(newOutList);
				
				//���浽ʵʱ��
				newInList = RuleCache.getNewInRegionAndRemove();
				ruleDao.delWqRegionVisit(newInList);
				ruleDao.saveWqRegionVisit(newInList);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally{
				if(newInList != null){
					newInList.clear();
				}
				swapOutRegionQueue.clear();
				isRunning.set(false);
			}
		}
	}

}
