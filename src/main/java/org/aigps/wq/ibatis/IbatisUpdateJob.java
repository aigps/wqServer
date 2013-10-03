package org.aigps.wq.ibatis;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.JobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @Title������ִ�и������ݿ����
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-10-19����03:50:21
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
@Component
public class IbatisUpdateJob {
	private static final Log log = LogFactory.getLog(IbatisUpdateJob.class);
	
	// ����SQL�Ļ��� key -ibatisSQL   value -��������Ķ���
	private static Map<String, Queue<Object>> updateSqlObjectMap = new ConcurrentHashMap<String, Queue<Object>>();
	
	// SQL����ִ��״��  key - ibatisSQL  value - boolean(�Ƿ�����ִ��)
	private static Map<String, AtomicBoolean> updateSqlSttsMap = new ConcurrentHashMap<String, AtomicBoolean>();
	
	// ��ǰ����������ܸ���
	private static AtomicInteger taskCounter = new AtomicInteger();
	
	private static BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(200);
	private static BasicThreadFactory tfactory = new BasicThreadFactory.Builder().namingPattern("ibatisUpdateJob-pool-%d").daemon(true).build();
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(2,5,30,TimeUnit.SECONDS,taskQueue,tfactory,new ThreadPoolExecutor.CallerRunsPolicy());

	@Autowired
	private static SqlMapClient sqlMapClient;
	public static SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		IbatisUpdateJob.sqlMapClient = sqlMapClient;
	}
	public void addExeSql(String ibatisSql,Object param){
		Queue<Object> params = updateSqlObjectMap.get(ibatisSql);
		if(params == null) {
			updateSqlObjectMap.put(ibatisSql, params = new ConcurrentLinkedQueue<Object>());
		}
		params.add(param);
	}
	
	public IbatisUpdateJob() {
		//��ʱִ�л����е�SQL���
		JobUtil.interval(0.5, "Thread-DB-IbatisUpdateJob", new JobUtil.Callback() {
			public void execute() throws Exception {
				Iterator<String> sqlIt = updateSqlObjectMap.keySet().iterator();
				//�����̳߳�ִ��ÿһ��SQL������������
				while(sqlIt.hasNext()){
					final String sql = sqlIt.next();
					final Queue<Object> queue = updateSqlObjectMap.get(sql);
					boolean isSQLRunning = false;
					if(updateSqlSttsMap.containsKey(sql)){
						isSQLRunning = updateSqlSttsMap.get(sql).get();
					}
					//��������ݣ����ҵ�ȻSQLû����ִ�У��ŵ�һ���������̳߳���ȥִ��
					if(queue.peek()!=null && !isSQLRunning){
						updateSqlSttsMap.put(sql, new AtomicBoolean(true));
						taskCounter.incrementAndGet();
						pool.execute(new Runnable() {
							public void run() {
								try {
									if(log.isTraceEnabled()){
										log.trace("����ִ��ibatisSQL�������:"+sql);
									}
									sqlMapClient.startBatch();
									while(queue.peek()!=null){
										Object obj = queue.poll();
										sqlMapClient.update(sql, obj);
									}
									sqlMapClient.executeBatch();
								}catch (Exception e) {
									log.error("ִ�и������:"+sql+" ʱ����!",e);
								}finally{
									taskCounter.decrementAndGet();
									updateSqlSttsMap.get(sql).set(false);
								}
							}
						});
					}
				}
				//������������������̳߳�
				if(taskCounter.get()/5>5){
					pool.setMaximumPoolSize(taskCounter.get()/5);
					if(log.isWarnEnabled()){
						log.warn("������������:"+taskCounter.get()+"  �����̳߳ظ���Ϊ:"+pool.getMaximumPoolSize());
					}
				}
			}
		});
	}
	
}
