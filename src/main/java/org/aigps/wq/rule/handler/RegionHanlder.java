package org.aigps.wq.rule.handler;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqMapRegion;
import org.aigps.wq.entity.WqRegionVisit;
import org.aigps.wq.rule.RuleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.model.MapLocation;
import org.gps.map.model.ShapeEnum;
import org.gps.map.util.MapUtil;
import org.gps.util.common.DateUtil;

public class RegionHanlder {
	private static final Log log = LogFactory.getLog(RegionHanlder.class);
	
	public static void handle(String staffId, String companyId,
			GisPosition gis,MapLocation mapPoint) throws Exception {
		GisPosition preGps = RuleCache.getLastGps(staffId);
		if(preGps!=null && preGps.getRptTime().substring(0, 8).compareTo(gis.getRptTime().substring(0, 8))<0){
			RuleCache.removeOverDayVisit(preGps);
		}
		RuleCache.refreshLastGps(gis);
		/**
		 * �������е�ǰ���ն˰󶨣���Ч������
		 */
		Set<String> regionList = RuleCache.getStaffRegionMap().get(staffId);
		
		if(regionList!=null){
			for (Iterator iterator = regionList.iterator(); iterator
					.hasNext();) {
				//����ID
				String regionId = (String) iterator.next();
				//����
				WqMapRegion mapRegion = RuleCache.getRegionMap().get(regionId);
				if(mapRegion==null){
					log.warn("Ա��:".concat(staffId).concat(" ȡ��������:"+regionId));
					continue;
				}
				ShapeEnum mapShape = mapRegion.getShapeEnum();
				boolean isInRegion = false;
				if(mapShape.equals(ShapeEnum.CIRCLE)){
					if(MapUtil.isInsideCircle(mapPoint, mapRegion.getLocations().get(0), mapRegion.getRadius().doubleValue(), 0)){
						isInRegion = true;
					}
				}else{
					if(MapUtil.isPointInPolygon(mapShape, mapPoint, mapRegion.getLocations(), 0)){
						isInRegion = true;
					}
				}
				//����ڵ�ǰ������
				if(isInRegion){
					//���֮ǰ����������
					if(!RuleCache.isAlreadyInRegion(staffId, regionId)){
						//�½�������
						WqRegionVisit wqRegionVisit = new WqRegionVisit();
						wqRegionVisit.setStaffId(staffId);
						wqRegionVisit.setRegionId(regionId);
						wqRegionVisit.setEnterTime(gis.getRptStrTime());
						//����������˾
						wqRegionVisit.setCompanyId(companyId);
						RuleCache.addNewInRegion(wqRegionVisit);
					}else{//���֮ǰҲ��������
						WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, regionId);
						//����ʱ���ǰһ�εĽ����ʱ�仹��ǰ�棬��ô�������ǲ���������������GPS��λ������
						if(wqRegionVisit.getEnterTime().compareTo(gis.getRptStrTime())>0){//���
							RuleCache.removeTrackBackGpsRegion(staffId, regionId);
							wqRegionVisit = new WqRegionVisit();
							wqRegionVisit.setStaffId(staffId);
							wqRegionVisit.setRegionId(regionId);
							wqRegionVisit.setEnterTime(gis.getRptStrTime());
							//����������˾
							wqRegionVisit.setCompanyId(companyId);
							RuleCache.addNewInRegion(wqRegionVisit);
						}
					}
				}else{//����������
					if(RuleCache.isAlreadyInRegion(staffId, regionId)){
						//�뿪����
						WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, regionId);
						//��ʼʱ�����С�ڽ���ʱ��
						if(wqRegionVisit!=null && wqRegionVisit.getEnterTime().compareTo(gis.getRptStrTime())<0){
							wqRegionVisit.setLeaveTime(gis.getRptStrTime());
							wqRegionVisit.setStayLong(BigDecimal.valueOf(DateUtil.getBetweenTime(wqRegionVisit.getEnterTime(),wqRegionVisit.getLeaveTime(), DateUtil.YMD_HMS)/1000));
							RuleCache.addNewOutRegion(wqRegionVisit);
						}else{//
							RuleCache.removeTrackBackGpsRegion(staffId, regionId);
						}
					}
				}
			}
		}
		/**
		 * ȡ�����ڷ��ʣ�����Ч������
		 */
		List<String> inValidRegion = RuleCache.getInValidRegion(staffId, regionList);
		if(inValidRegion!=null && inValidRegion.size()>0){
			for (Iterator iterator = inValidRegion.iterator(); iterator
					.hasNext();) {
				String inValidRegionId = (String) iterator.next();
				WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, inValidRegionId);
				if(wqRegionVisit.getEnterTime().compareTo(gis.getRptStrTime())<0){
					wqRegionVisit.setLeaveTime(gis.getRptStrTime());
				}else{
					wqRegionVisit.setLeaveTime(DateUtil.sysDateTime);
				}
				wqRegionVisit.setStayLong(BigDecimal.valueOf(DateUtil.getBetweenTime(wqRegionVisit.getEnterTime(), wqRegionVisit.getLeaveTime(), DateUtil.YMD_HMS)/1000));
				RuleCache.addNewOutRegion(wqRegionVisit);
			}
		}
	}

}
