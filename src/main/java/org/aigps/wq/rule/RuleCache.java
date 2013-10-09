package org.aigps.wq.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqAlarmInfo;
import org.aigps.wq.entity.WqMapRegion;
import org.aigps.wq.entity.WqRegionVisit;
import org.aigps.wq.entity.WqRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

@Component
public class RuleCache implements ApplicationContextAware{
	private static final Log log = LogFactory.getLog(RuleCache.class);
	
	public static ApplicationContext context;
	
	/**
	 * ���򼯺�
	 */
	private static Map<String, WqMapRegion> regionMap = new ConcurrentLinkedHashMap.Builder<String, WqMapRegion>().maximumWeightedCapacity(10000).build();
	public static Map<String, WqMapRegion> getRegionMap()throws Exception {
		if(regionMap.size() == 0){
			regionMap = loadRegionMap();
		}
		return regionMap;
	}
	public static Map<String,WqMapRegion> loadRegionMap()throws Exception{
		Map<String, WqMapRegion> tempMap = new ConcurrentLinkedHashMap.Builder<String, WqMapRegion>().maximumWeightedCapacity(10000).build();
		RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class); 
		tempMap.putAll(ruleDao.getWqMapRegion());
		return tempMap;
	}

	
	/**
	 * Ա����Ч������ռ���(������Ա���󶨵�����ID����)
	 */
	private static Map<String,Set<String>> staffRegionMap = new ConcurrentLinkedHashMap.Builder<String, Set<String>>().maximumWeightedCapacity(10000).build();
	public static Map<String, Set<String>> getStaffRegionMap()throws Exception {
		if(staffRegionMap.size() == 0){
			staffRegionMap = loadStaffRegionMap();
		}
		return staffRegionMap;
	}
	public static Map<String,Set<String>> loadStaffRegionMap()throws Exception{
		Map<String, Set<String>> tempMap = new ConcurrentLinkedHashMap.Builder<String, Set<String>>().maximumWeightedCapacity(10000).build();
		RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class); 
		tempMap.putAll(ruleDao.loadWqStaffRegion());
		return tempMap;
	}
	
	
	/**
	 * Ա��Ŀǰ�������򼯺�
	 */
	private static Map<String, HashMap<String, WqRegionVisit>> staffVisitRegionMap = new ConcurrentLinkedHashMap.Builder<String, HashMap<String,WqRegionVisit>>().maximumWeightedCapacity(10000).build();
	public static Map<String, HashMap<String, WqRegionVisit>> getStaffVisitRegionMap()throws Exception {
		if(staffVisitRegionMap.size() == 0){
			staffVisitRegionMap = loadStaffVisitRegionMap();
		}
		return staffVisitRegionMap;
	}
	public static Map<String, HashMap<String, WqRegionVisit>> loadStaffVisitRegionMap()throws Exception{
		Map<String, HashMap<String, WqRegionVisit>> tempMap = new ConcurrentLinkedHashMap.Builder<String, HashMap<String,WqRegionVisit>>().maximumWeightedCapacity(10000).build();
		RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class); 
		tempMap.putAll(ruleDao.getWqRegionVisit());
		return tempMap;
	}
	/**
	 * �ж��Ƿ��Ѿ���������
	 * @param staffId
	 * @param regionId
	 * @return
	 * @throws Exception
	 */
	public static boolean isAlreadyInRegion(String staffId,String regionId)throws Exception{
		boolean retFlag = false;
		if(getStaffVisitRegionMap().containsKey(staffId)){
			HashMap<String, WqRegionVisit> staffRegionVsMap = getStaffVisitRegionMap().get(staffId);
			if(staffRegionVsMap.containsKey(regionId)){
				retFlag = true;
			}
		}
		return retFlag;
	}
	
	/**
	 * �õ�����������¼
	 * @param staffId
	 * @param regionId
	 * @return
	 * @throws Exception
	 */
	public static WqRegionVisit getAlreadyInRegion(String staffId,String regionId)throws Exception{
		if(getStaffVisitRegionMap().containsKey(staffId)){
			HashMap<String, WqRegionVisit> staffRegionVsMap = getStaffVisitRegionMap().get(staffId);
			if(staffRegionVsMap.containsKey(regionId)){
				return staffRegionVsMap.get(regionId);
			}
		}
		return null;
	}
	/**
	 * ���Ե����ڷ��ʵ��������������ϱ���λ��
	 * @param staffId
	 * @param regionId
	 * @throws Exception
	 */
	public static void removeTrackBackGpsRegion(String staffId,String regionId)throws Exception{
		if(getStaffVisitRegionMap().containsKey(staffId)){
			HashMap<String, WqRegionVisit> staffRegionVsMap = getStaffVisitRegionMap().get(staffId);
			if(staffRegionVsMap.containsKey(regionId)){
				staffRegionVsMap.remove(regionId);
			}
		}
	}
	/**
	 * ȡ�����ڷ�����Ч������
	 * @param staffId
	 * @param validRegionList
	 * @return
	 * @throws Exception
	 */
	public static List<String> getInValidRegion(String staffId,Set<String> validRegionList)throws Exception{
		List<String> retList = new ArrayList<String>();
		if(getStaffVisitRegionMap().containsKey(staffId)){
			HashMap<String, WqRegionVisit> staffRegionVsMap = getStaffVisitRegionMap().get(staffId);
			Iterator<String> regionIt = staffRegionVsMap.keySet().iterator();
			while(regionIt.hasNext()){
				String regionId = regionIt.next();
				//��Ч������
				if(validRegionList==null || !validRegionList.contains(regionId)){
					retList.add(regionId);
				}
			}
		}
		return retList;
	}
	
	
	
	
	//�½�������򼯺�
	public static Queue<WqRegionVisit> newInRegionQueue = new ConcurrentLinkedQueue<WqRegionVisit>();
	private static Queue<WqRegionVisit> swapInRegionQueue = new ConcurrentLinkedQueue<WqRegionVisit>();
	
	/**
	 * �����½��������
	 * @param wqRegionVisit
	 * @throws Exception
	 */
	public static void addNewInRegion(WqRegionVisit wqRegionVisit)throws Exception{
		if(staffVisitRegionMap.containsKey(wqRegionVisit.getStaffId())){
			HashMap<String, WqRegionVisit> regionVisitMap = staffVisitRegionMap.get(wqRegionVisit.getStaffId());
			regionVisitMap.put(wqRegionVisit.getRegionId(), wqRegionVisit);
		}else{
			HashMap<String, WqRegionVisit> regionVisitMap = new HashMap<String, WqRegionVisit>();
			regionVisitMap.put(wqRegionVisit.getRegionId(), wqRegionVisit);
			staffVisitRegionMap.put(wqRegionVisit.getStaffId(), regionVisitMap);
		}
		newInRegionQueue.add(wqRegionVisit);
	}
	/**
	 * ȡ����ǰ�½��������ȷ��ÿ����ÿ������ֻ��һ����¼
	 * @return
	 * @throws Exception
	 */
	public static List<WqRegionVisit> getNewInRegionAndRemove()throws Exception{
		List<WqRegionVisit> list = new ArrayList<WqRegionVisit>();
		Queue<WqRegionVisit> newInQueue = RuleCache.newInRegionQueue;
		RuleCache.newInRegionQueue = swapInRegionQueue;
		swapInRegionQueue = newInQueue;
		HashMap<String,WqRegionVisit> newMap = new HashMap<String,WqRegionVisit>();
		while(swapInRegionQueue.peek() != null) {
			WqRegionVisit wqRegionVisit = swapInRegionQueue.poll();
			if(wqRegionVisit.getLeaveTime()==null || wqRegionVisit.getLeaveTime().trim().equals("")){
				newMap.put(wqRegionVisit.getStaffId()+wqRegionVisit.getRegionId(), wqRegionVisit);
			}
		}
		
		Iterator<String> newInIt = newMap.keySet().iterator();
		while(newInIt.hasNext()){
			String key = newInIt.next();
			WqRegionVisit wqRegionVisit = newMap.get(key);
			list.add(wqRegionVisit);
		}
		return list;
	}
	
	public static Queue<WqRegionVisit> newOutRegionQueue = new ConcurrentLinkedQueue<WqRegionVisit>();
	
	/**
	 * �����µ��뿪����
	 * @param wqRegionVisit
	 * @throws Exception
	 */
	public static void addNewOutRegion(WqRegionVisit wqRegionVisit)throws Exception{
		if(staffVisitRegionMap.containsKey(wqRegionVisit.getStaffId())){
			HashMap<String, WqRegionVisit> regionVisitMap = staffVisitRegionMap.get(wqRegionVisit.getStaffId());
			regionVisitMap.remove(wqRegionVisit.getRegionId());
		}
		newOutRegionQueue.add(wqRegionVisit);
	}
	
	/**
	 * �Ƴ�����ķ��ʣ���һ�η��ʷֳ�����
	 * @throws Exception
	 */
	public static void removeOverDayVisit(GisPosition preGps)throws Exception{
		String staffId = preGps.getTmnAlias();
		HashMap<String, WqRegionVisit> regionVisitMap = staffVisitRegionMap.get(staffId);
		if(regionVisitMap != null){
			Iterator<String> regionIdIt = regionVisitMap.keySet().iterator();
			while(regionIdIt.hasNext()){
				String regionId = regionIdIt.next();
				WqRegionVisit wqRegionVisit = regionVisitMap.get(regionId);
				wqRegionVisit.setLeaveTime(preGps.getRptTime());
				wqRegionVisit.setStayLong(BigDecimal.valueOf(DateUtil.getBetweenTime(wqRegionVisit.getEnterTime(), wqRegionVisit.getLeaveTime(),DateUtil.YMDHMS)/1000));
				RuleCache.addNewOutRegion(wqRegionVisit);
			}
		}
	}
	
	
	/**
	 * ���һ����λ
	 */
	public static Map<String, GisPosition> staffGpsMap = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(10000).build();
	public static void refreshLastGps(GisPosition gpsModel){
		staffGpsMap.put(gpsModel.getTmnAlias(), gpsModel);
	}
	public static GisPosition getLastGps(String staffId)throws Exception{
		if(staffGpsMap.containsKey(staffId)){
			return staffGpsMap.get(staffId);
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * Ա��������ռ���key-Ա��ID, value-[����ID,����ID]
	 */
	private static Map<String,List<String[]>> staffRuleMap = null;
	public static Map<String, List<String[]>> getStaffRuleMap()throws Exception {
		if(staffRuleMap==null){
			RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class);
			Map<String,List<String[]>> tempMap = ruleDao.loadWqStaffRule();
			if(tempMap!=null){
				staffRuleMap = tempMap;
			}else{
				return new HashMap<String, List<String[]>>();
			}
		}
		return staffRuleMap;
	}
	public static Map<String, List<String[]>> loadStaffRuleMap()throws Exception {
		RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class);
		return ruleDao.loadWqStaffRule();
	}
	
	
	/**
	 * ���򼯺�
	 */
	private static Map<String,WqRule> ruleMap ;
	public static Map<String, WqRule> getRuleMap()throws Exception {
		if(ruleMap==null){
			ruleMap = loadRuleMap();
		}
		return ruleMap;
	}
	public static Map<String, WqRule> loadRuleMap()throws Exception{
		RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class);
		return ruleDao.loadWqRule();
	}


	//ʵʱ��Υ�汨��
	private static Map<String,  HashMap<String,WqAlarmInfo>> realAlarmMap;
	
	/**
	 * ��ȡ��ʵʱ��Υ�汨��
	 * @return
	 * @throws Exception
	 */
	public static Map<String,HashMap<String,WqAlarmInfo>> getRealAlarmMap()throws Exception {
		if(realAlarmMap==null){
			RuleDao ruleDao = context.getBean("ruleDao", RuleDao.class);
			Map<String, HashMap<String,WqAlarmInfo>> retMap = ruleDao.loadRealAlarmMap();
			if(retMap==null){
				realAlarmMap = new HashMap<String, HashMap<String,WqAlarmInfo>>();
			}else{
				realAlarmMap = retMap;
			}
		}
		return realAlarmMap;
	}
	public static List<WqAlarmInfo> newWqAlarmList = new ArrayList<WqAlarmInfo>(1000);
	
	/**
	 * �����µı���
	 * @param wqAlarmInfo
	 * @throws Exception
	 */
	public static void addNewWqAlarm(WqAlarmInfo wqAlarmInfo)throws Exception{
		if(wqAlarmInfo!=null){
			String staffId = wqAlarmInfo.getStaffId();
			String ruleId = wqAlarmInfo.getRuleId();
			newWqAlarmList.add(wqAlarmInfo);
			if(getRealAlarmMap().containsKey(staffId)){
				HashMap<String, WqAlarmInfo> ruleAlarmMap = getRealAlarmMap().get(staffId);
				ruleAlarmMap.put(ruleId, wqAlarmInfo);
			}else{
				HashMap<String, WqAlarmInfo> ruleAlarmMap = new HashMap<String, WqAlarmInfo>();
				ruleAlarmMap.put(ruleId, wqAlarmInfo);
				getRealAlarmMap().put(staffId, ruleAlarmMap);
			}
		}
	}
	/**
	 * �Ƿ��Ѿ����ڱ���
	 * @param staffId
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	public static boolean isAlreadyAlarm(String staffId,String ruleId)throws Exception{
		boolean retFlag = false;
		if(getRealAlarmMap().containsKey(staffId)){
			HashMap<String, WqAlarmInfo> ruleAlarmMap = getRealAlarmMap().get(staffId);
			if(ruleAlarmMap.containsKey(ruleId)){
				retFlag = true;
			}
		}
		return retFlag;
	}

	/**
	 * ȡ��Ա��ʧЧ�Ĺ���
	 * @param staffId
	 * @param ruleSet
	 * @return
	 * @throws Exception
	 */
	public static List<String> getInValidAlarm(String staffId,Set<String> ruleSet)throws Exception{
		List<String> retList = new ArrayList<String>();
		//���ʵʱ�����д��ڸ�Ա���ı���
		if(getRealAlarmMap().containsKey(staffId)){
			HashMap<String, WqAlarmInfo> ruleAlarmMap = getRealAlarmMap().get(staffId);
			//ȡ������Υ��Ĺ���Id����
			Iterator<String> ruleIt = ruleAlarmMap.keySet().iterator();
			while(ruleIt.hasNext()){
				String ruleId = ruleIt.next();
				//���ʵʱ�����д���ʧЧ�Ĺ���
				if(!ruleSet.contains(ruleId)){
					retList.add(ruleId);
				}
			}
		}
		return retList;
	}
	
	/**
	 * �Ƴ�ʧЧ�Ĺ���
	 * @param staffId
	 * @param ruleList
	 * @throws Exception
	 */
	public static void removeInValidAlarm(String staffId,List<String> ruleList)throws Exception{
		if(getRealAlarmMap().containsKey(staffId)){
			HashMap<String, WqAlarmInfo> ruleAlarmMap = getRealAlarmMap().get(staffId);
			for (Iterator iterator = ruleList.iterator(); iterator.hasNext();) {
				String ruleId = (String) iterator.next();
				ruleAlarmMap.remove(ruleId);
			}
		}
	}

	
	/**
	 * ��ʼ����������
	 * @throws Exception
	 */
	@PostConstruct
	public void init()throws Exception{
		
		refresh();
	}
	
	public void refresh() throws Exception {
		regionMap = loadRegionMap();
		staffRegionMap = loadStaffRegionMap();
		ruleMap = loadRuleMap();
		staffRuleMap = loadStaffRuleMap();
		staffVisitRegionMap = loadStaffVisitRegionMap();
	}
	
	
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		RuleCache.context = context;
	}

}
