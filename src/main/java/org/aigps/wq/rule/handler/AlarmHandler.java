package org.aigps.wq.rule.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqAlarmInfo;
import org.aigps.wq.entity.WqMapRegion;
import org.aigps.wq.entity.WqRule;
import org.aigps.wq.rule.RuleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.model.MapLocation;
import org.gps.map.model.ShapeEnum;
import org.gps.map.util.MapUtil;
import org.gps.util.common.DateUtil;

import com.sunleads.dc.GeoCache;

public class AlarmHandler {
	private static final Log log = LogFactory.getLog(AlarmHandler.class);
	
	public static void handle( String staffId,String companyId,
			GisPosition ymGpsModel, MapLocation mapPoint) throws Exception {
		/**
		 * ���Ա�����ڹ���
		 */
		//��ǰ��Ч�Ĺ��򼯺�
		Set<String> ruleSet = new HashSet<String>();
		String gpsDate = ymGpsModel.getRptStrTime().substring(0,11).trim();
		String gpsHms = ymGpsModel.getRptStrTime().substring(11).trim();
		if(RuleCache.getStaffRuleMap().containsKey(staffId)){
			List<String[]> ruleList = RuleCache.getStaffRuleMap().get(staffId);
			if(ruleList!=null && ruleList.size()>0){
				int weekDay = DateUtil.parse(ymGpsModel.getRptStrTime(), DateUtil.YMD_HMS).getDay();
				if(weekDay==0){//������
					weekDay = 7;
				}
				for (Iterator iterator = ruleList.iterator(); iterator.hasNext();) {
					boolean isWantAlarm = false;
					boolean isInRegion = false;
					String[] ruleArray = (String[]) iterator.next();
					String ruleId = ruleArray[0];
					String regionId = ruleArray[1];
					ruleSet.add(ruleId);
					WqRule wqRule = RuleCache.getRuleMap().get(ruleId);
					try {
						WqMapRegion wqMapRegion = RuleCache.getRegionMap().get(regionId);
						if(wqRule==null){
							continue;
						}
						//����������ڷ�Χ��
						if(gpsDate.compareTo(wqRule.getStartDate())<0 || gpsDate.compareTo(wqRule.getEndDate())>0){
							continue;
						}
						//����������ڷ�Χ��
						if(wqRule.getWeekDays() ==null || wqRule.getWeekDays().indexOf(weekDay+"")<0){
							continue;
						}
						//�������ʱ�䷶Χ��
						
						if(!DateUtil.isBetween(wqRule.getStartTime(), wqRule.getEndTime(),gpsHms)){
							continue;
						}
						ShapeEnum mapShape = wqMapRegion.getShapeEnum();
						
						if(mapShape.equals(ShapeEnum.CIRCLE)){
							if(MapUtil.isInsideCircle(mapPoint, wqMapRegion.getLocations().get(0), wqMapRegion.getRadius().doubleValue(), 0)){
								isInRegion = true;
							}
						}else{
							if(MapUtil.isPointInPolygon(mapShape, mapPoint, wqMapRegion.getLocations(), 0)){
								isInRegion = true;
							}
						}
						if("00".equalsIgnoreCase(wqRule.getType())){//����ǽ�������
							if(!isInRegion){//�����������
								isWantAlarm = true;
							}
						}else if("01".equalsIgnoreCase(wqRule.getType())){//����ǽ�������
							if(isInRegion){
								isWantAlarm = true;
							}
						}else{
							continue;
						}
						//������µı���
						if(isWantAlarm && !RuleCache.isAlreadyAlarm(staffId, ruleId)){
							WqAlarmInfo wqAlarmInfo = new WqAlarmInfo();
							wqAlarmInfo.setStaffId(staffId);
							wqAlarmInfo.setAlarmTime(ymGpsModel.getRptStrTime());
							wqAlarmInfo.setRuleId(ruleId);
							wqAlarmInfo.setCompanyId(companyId);
							wqAlarmInfo.setLocDesc(ymGpsModel.getLocDesc());
							if(wqAlarmInfo.getLocDesc()==null || wqAlarmInfo.getLocDesc().equals("")){
								wqAlarmInfo.setLocDesc(GeoCache.getGeo(ymGpsModel.getLon(), ymGpsModel.getLat()));
							}
							RuleCache.addNewWqAlarm(wqAlarmInfo);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						//������ϵı���������Ҫ������
						if(!isWantAlarm && RuleCache.isAlreadyAlarm(staffId, ruleId)){
							List<String> endRuleList = new ArrayList<String>();
							endRuleList.add(ruleId);
							RuleCache.removeInValidAlarm(staffId, endRuleList);
						}
					}
				}
			}
		}
		/**
		 * ȡ����Ч�Ĺ��򼯺�
		 */
		List<String> inValidRule = RuleCache.getInValidAlarm(staffId, ruleSet);
		/**
		 * �Ƴ�����Ч�Ĺ��򼯺�
		 */
		RuleCache.removeInValidAlarm(staffId, inValidRule);
	}

}
