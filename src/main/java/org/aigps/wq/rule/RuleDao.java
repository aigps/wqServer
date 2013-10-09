package org.aigps.wq.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.aigps.wq.entity.WqAlarmInfo;
import org.aigps.wq.entity.WqMapRegion;
import org.aigps.wq.entity.WqRegionVisit;
import org.aigps.wq.entity.WqRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ibatis.sqlmap.client.SqlMapClient;
@Component
public class RuleDao {
	private static final Log log = LogFactory.getLog(RuleDao.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Autowired
	private  SqlMapClient sqlMapClient;
	public  SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}
	
	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}
	
	
	/**
	 * 获取到外勤地理区域集合
	 * @return
	 * @throws Exception
	 */
	public Map<String, WqMapRegion> getWqMapRegion()throws Exception{
		HashMap<String, WqMapRegion> wqMapRegionMap = new HashMap<String, WqMapRegion>();
		List<WqMapRegion> list = getSqlMapClient().queryForList("WQ_MAP_REGION.selectAll");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			WqMapRegion wqMapRegion = (WqMapRegion) iterator.next();
			wqMapRegionMap.put(wqMapRegion.getId(), wqMapRegion);
		}
		return wqMapRegionMap;
	}
	
	/**
	 * 加载员工有效的区域
	 * @return
	 * @throws Exception
	 */
	public Map<String,Set<String>> loadWqStaffRegion()throws Exception{
		Map<String,Set<String>> tempMap = new HashMap<String, Set<String>>();
		/**
		 * 待丰富
		 */
		return tempMap;
	}
	
	
	/**
	 * 加载员工的规则
	 * @return
	 * @throws Exception
	 */
	public Map<String,List<String[]>> loadWqStaffRule()throws Exception{
		Map<String,List<String[]>> tmpMap = new HashMap<String, List<String[]>>();
		
		/**
		 * 待丰富
		 */
		return tmpMap;
	}
	
	/**
	 * 获取到实时的违规报警
	 * @return
	 * @throws Exception
	 */
	public Map<String,HashMap<String,WqAlarmInfo>> loadRealAlarmMap()throws Exception {
		Map<String,HashMap<String,WqAlarmInfo>> tempMap = new HashMap<String, HashMap<String,WqAlarmInfo>>();
		/**
		 * 待丰富
		 */
		return tempMap;
	}
	
	
	/**
	 * 加载所有规则
	 * @return
	 * @throws Exception
	 */
	public  Map<String,WqRule> loadWqRule()throws Exception{
		Map<String, WqRule> retMap = new HashMap<String, WqRule>();
		List<WqRule> list = getSqlMapClient().queryForList("WQ_RULE.selectAll");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			WqRule wqRule = (WqRule) iterator.next();
			retMap.put(wqRule.getId(), wqRule);
		}
		return retMap;
	}
	
	
	/**
	 * 获取到外勤人员当前所在的地理区域
	 * @return
	 * @throws Exception
	 */
	public Map<String, HashMap<String,WqRegionVisit>> getWqRegionVisit()throws Exception{
		List<WqRegionVisit> list = getSqlMapClient().queryForList("WQ_REGION_VISIT.selectAll");
		Map<String, HashMap<String,WqRegionVisit>> staffVisitRegionMap = new HashMap<String, HashMap<String,WqRegionVisit>>();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			WqRegionVisit wqRegionVisit = (WqRegionVisit) iterator.next();
			if(staffVisitRegionMap.containsKey(wqRegionVisit.getStaffId())){
				HashMap<String, WqRegionVisit> regionVisitMap = staffVisitRegionMap.get(wqRegionVisit.getStaffId());
				regionVisitMap.put(wqRegionVisit.getRegionId(), wqRegionVisit);
			}else{
				HashMap<String, WqRegionVisit> regionVisitMap = new HashMap<String, WqRegionVisit>();
				regionVisitMap.put(wqRegionVisit.getRegionId(), wqRegionVisit);
				staffVisitRegionMap.put(wqRegionVisit.getStaffId(), regionVisitMap);
			}
		}
		return staffVisitRegionMap;
	}
	
	
	/**
	 * 删除已经离开的区域
	 * @param queue
	 * @throws Exception
	 */
	public  void delWqRegionVisit(Collection<WqRegionVisit> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			getSqlMapClient().startBatch();
			for (WqRegionVisit wqRegionVisit : queue) {
				getSqlMapClient().delete("WQ_REGION_VISIT.deleteByPrimaryKey", wqRegionVisit);
			}
			getSqlMapClient().executeBatch();
		}
	}
	
	/**
	 * 保存进出区域历史
	 * @param list
	 * @throws Exception
	 */
	public void saveWqRegionVisitHis(Queue<WqRegionVisit> queue)throws Exception{
		if(queue!=null && !queue.isEmpty()){
			getSqlMapClient().startBatch();
			while(queue.peek() != null){
				WqRegionVisit wqRegionVisit = queue.poll();
				getSqlMapClient().insert("WQ_REGION_VISIT_HIS.insert", wqRegionVisit);
			}
			getSqlMapClient().executeBatch();
		}
	}
	
	/**
	 * 保存刚进入的区域
	 * @param list
	 * @throws Exception
	 */
	public void saveWqRegionVisit(List<WqRegionVisit> list)throws Exception{
		if(list!=null && list.size()>0){
			getSqlMapClient().startBatch();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				WqRegionVisit wqRegionVisit = (WqRegionVisit) iterator.next();
				if(wqRegionVisit.getLeaveTime()==null || wqRegionVisit.getLeaveTime().trim().equals("")){
					getSqlMapClient().insert("WQ_REGION_VISIT.insert", wqRegionVisit);
				}
			}
			getSqlMapClient().executeBatch();
		}
	}
	
	
	
	/**
	 * 保存历史报警
	 * @param list
	 * @throws Exception
	 */
	public void saveWqAlarmInfo(List<WqAlarmInfo> list)throws Exception{
		if(list!=null && list.size()>0){
			getSqlMapClient().startBatch();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				WqAlarmInfo wqAlarmInfo = (WqAlarmInfo) iterator.next();
				getSqlMapClient().insert("WQ_ALARM_INFO.insert", wqAlarmInfo);
			}
			getSqlMapClient().executeBatch();
		}
	}
	

}
