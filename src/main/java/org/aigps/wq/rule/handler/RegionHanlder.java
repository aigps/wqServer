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
			GisPosition ymGpsModel,MapLocation mapPoint) throws Exception {
		GisPosition preGps = RuleCache.getLastGps(staffId);
		if(preGps!=null && preGps.getRptTime().substring(0, 8).compareTo(ymGpsModel.getRptTime().substring(0, 8))<0){
			RuleCache.removeOverDayVisit(preGps);
		}
		RuleCache.refreshLastGps(ymGpsModel);
		/**
		 * 遍历所有当前与终端绑定，生效的区域
		 */
		Set<String> regionList = RuleCache.getStaffRegionMap().get(staffId);
		
		if(regionList!=null){
			for (Iterator iterator = regionList.iterator(); iterator
					.hasNext();) {
				//区域ID
				String regionId = (String) iterator.next();
				//区域
				WqMapRegion mapRegion = RuleCache.getRegionMap().get(regionId);
				if(mapRegion==null){
					log.warn("员工:".concat(staffId).concat(" 取不到区域:"+regionId));
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
				//如果在当前区域内
				if(isInRegion){
					//如果之前不在区域内
					if(!RuleCache.isAlreadyInRegion(staffId, regionId)){
						//新进入区域
						WqRegionVisit wqRegionVisit = new WqRegionVisit();
						wqRegionVisit.setStaffId(staffId);
						wqRegionVisit.setRegionId(regionId);
						wqRegionVisit.setEnterTime(ymGpsModel.getRptStrTime());
						//增加所属公司
						wqRegionVisit.setCompanyId(companyId);
						RuleCache.addNewInRegion(wqRegionVisit);
					}else{//如果之前也在区域内
						WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, regionId);
						//但是时间比前一次的进入的时间还在前面，那么，可能是补报或者重新生成GPS定位，忽略
						if(wqRegionVisit.getEnterTime().compareTo(ymGpsModel.getRptStrTime())>0){//如果
							RuleCache.removeTrackBackGpsRegion(staffId, regionId);
							wqRegionVisit = new WqRegionVisit();
							wqRegionVisit.setStaffId(staffId);
							wqRegionVisit.setRegionId(regionId);
							wqRegionVisit.setEnterTime(ymGpsModel.getRptStrTime());
							//增加所属公司
							wqRegionVisit.setCompanyId(companyId);
							RuleCache.addNewInRegion(wqRegionVisit);
						}
					}
				}else{//不在区域内
					if(RuleCache.isAlreadyInRegion(staffId, regionId)){
						//离开区域
						WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, regionId);
						//开始时间必须小于结束时间
						if(wqRegionVisit!=null && wqRegionVisit.getEnterTime().compareTo(ymGpsModel.getRptStrTime())<0){
							wqRegionVisit.setLeaveTime(ymGpsModel.getRptStrTime());
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
		 * 取到正在访问，但无效的区域
		 */
		List<String> inValidRegion = RuleCache.getInValidRegion(staffId, regionList);
		if(inValidRegion!=null && inValidRegion.size()>0){
			for (Iterator iterator = inValidRegion.iterator(); iterator
					.hasNext();) {
				String inValidRegionId = (String) iterator.next();
				WqRegionVisit wqRegionVisit = RuleCache.getAlreadyInRegion(staffId, inValidRegionId);
				if(wqRegionVisit.getEnterTime().compareTo(ymGpsModel.getRptStrTime())<0){
					wqRegionVisit.setLeaveTime(ymGpsModel.getRptStrTime());
				}else{
					wqRegionVisit.setLeaveTime(DateUtil.sysDateTime);
				}
				wqRegionVisit.setStayLong(BigDecimal.valueOf(DateUtil.getBetweenTime(wqRegionVisit.getEnterTime(), wqRegionVisit.getLeaveTime(), DateUtil.YMD_HMS)/1000));
				RuleCache.addNewOutRegion(wqRegionVisit);
			}
		}
	}

}
