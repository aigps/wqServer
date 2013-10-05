package org.aigps.wq.rule;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.rule.handler.AlarmHandler;
import org.aigps.wq.rule.handler.RegionHanlder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.model.MapLocation;

public class RuleHandler {
	private static final Log log = LogFactory.getLog(RuleHandler.class);
	
	
	public static void handleGps(GisPosition ymGpsModel) throws Exception {
		String tmnCode = ymGpsModel.getTmnKey();
		String staffId = DcGpsCache.getTmnSysIdMap().get(tmnCode);
		String companyId = "";
		//���û��Ա��ID���˳�
		if(staffId==null){
			return;
		}
		ymGpsModel.setTmnAlias(staffId);
		if(DcGpsCache.getStaffMap().containsKey(staffId)){
			companyId = DcGpsCache.getStaffMap().get(staffId).getCompanyId();
		}
		//��ǰ��λ��
		MapLocation mapPoint = new MapLocation(ymGpsModel.getLonOff(),ymGpsModel.getLatOff());
		RegionHanlder.handle( staffId, companyId,ymGpsModel,mapPoint);
		AlarmHandler.handle(staffId, companyId,ymGpsModel, mapPoint);
	}
	
}
