package org.aigps.wq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.aigps.wq.dao.GpsDataDao;
import org.aigps.wq.entity.DcCmdTrace;
import org.aigps.wq.entity.DcRgAreaHis;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqStaffInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
/**
 * �������ĵ��ڴ滺��
 * @author Administrator
 *
 */
public class DcGpsCache{
	private static final Log log = LogFactory.getLog(DcGpsCache.class);
	
	
	/**
	 * �ն���ҵ��ϵͳ��ID����
	 */
	public static Map<String,String> tmnStaffIdMap = new HashMap<String, String>();
	public static Map<String, String> getTmnSysIdMap() {
		return tmnStaffIdMap;
	}

	public static void setTmnSysIdMap(HashMap<String, String> tmnSysIdMap) {
		DcGpsCache.tmnStaffIdMap = tmnSysIdMap;
	}
	
	
	
	
	/**
	 * Ա��ID��Ա��ģ��ʵ���Ķ���
	 */
	private static Map<String, WqStaffInfo> staffMap = new ConcurrentLinkedHashMap.Builder<String,WqStaffInfo>().build();
	public static Map<String, WqStaffInfo> getStaffMap()throws Exception {
		if(staffMap.size() == 0){
			staffMap = loadStaffMap();
		}
		return staffMap;
	}
	public static Map<String,WqStaffInfo> loadStaffMap()throws Exception{
		Map<String, WqStaffInfo> tempMap = new ConcurrentLinkedHashMap.Builder<String,WqStaffInfo>().build();
		tempMap.putAll(WqJoinContext.getBean("gpsDataDao", GpsDataDao.class).loadWqStaffInfo());
		return tempMap;
	}
	
	
	/**
	 * �ֻ����к�  ��  ��˾ְԱ  ����
	 */
	private static Map<String, String> msidStaffMap = new ConcurrentLinkedHashMap.Builder<String, String>().build();
	public static Map<String, String> getMsidStaffMap()throws Exception {
		if(msidStaffMap.size() == 0){
			msidStaffMap = loadMsidStaffMap();
		}
		return msidStaffMap;
	}
	public static Map<String, String> loadMsidStaffMap()throws Exception {
		Map<String, WqStaffInfo> staffMap = getStaffMap();
		Map<String,String> tempMap = new ConcurrentLinkedHashMap.Builder<String, String>().build();
		Iterator<String> staffIdIt = staffMap.keySet().iterator();
		while(staffIdIt.hasNext()){
			String staffId = staffIdIt.next();
			WqStaffInfo wqStaffInfo = staffMap.get(staffId);
			String msid = wqStaffInfo.getMsid();
			if(StringUtils.isNotBlank(msid)){
				tempMap.put(msid, staffId);
			}
		}
		return tempMap;
	}

	
	
	
	/**
	 * ��������ϱ��Ķ�λ��Ϣ����key--tmnCode, value--YmGpsModel
	 */
	private static Map<String,GisPosition> gpsLastCache = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * ȡ�������ն˵����¶�λ����
	 * @return
	 */
	public static Map<String, GisPosition> getLastGpsReport() {
		return gpsLastCache;
	}
	/**
	 * �����ն˵����¶�λ
	 * @param tmnCode
	 * @param gisPos
	 */
	public static void updateLastGps(String tmnCode,GisPosition gisPos){
		gpsLastCache.put(tmnCode, gisPos);
		gpsChangeCache.put(tmnCode, gisPos);
	}
	/**
	 * ȡ���ն˵����¶�λ
	 * @param tmnCode
	 * @return
	 */
	public static GisPosition getLastGps(String tmnCode){
		return gpsLastCache.get(tmnCode);
	}
	
	
	
	
	/**
	 * �䶯���Ķ�λ��Ϣ
	 */
	public static Map<String,GisPosition> gpsChangeCache = new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	/**
	 * ����һ���ӵ��ϱ�GPS��λ��Ϣ<����,�ϱ�gps��λ��Ϣ����>
	 */
	public static Queue<GisPosition> gpsAddCache = new ConcurrentLinkedQueue<GisPosition>();
	
	
	
	/**
	 * ���泵�����µĵ���λ��
	 */
	private static Map<String,GisPosition> gpsLocDescMap =  new ConcurrentLinkedHashMap.Builder<String, GisPosition>().maximumWeightedCapacity(50000).build();
	public static Map<String, GisPosition> getGpsLocDescMap()throws Exception {
		return gpsLocDescMap;
	}
	public static void setVhcLocDesc(String vehicleCode,GisPosition gisPos)throws Exception{
		getGpsLocDescMap().put(vehicleCode, gisPos);
	}
	public static GisPosition getVhcLocDesc(String vehicleCode)throws Exception{
		GisPosition model = null;
		if(getGpsLocDescMap().containsKey(vehicleCode)){
			model = getGpsLocDescMap().get(vehicleCode);
		}
		return model;
	}
	
	
	/**
	 * ������ָ���
	 */
	public static Queue<DcCmdTrace> inCreCmdTrace = new ConcurrentLinkedQueue<DcCmdTrace>();

	
	
	/**
	 * �ն˵�ǰ��������
	 */
	public static Map<String, DcRgAreaHis> dcRgAreaRealMap = new ConcurrentLinkedHashMap.Builder<String, DcRgAreaHis>().maximumWeightedCapacity(20000).build();
	/**
	 * �������Ѿ�ʻ������������
	 */
	public static Queue<DcRgAreaHis> dcRgAreaHisQueue = new ConcurrentLinkedQueue<DcRgAreaHis>();
	public static void updateDcRgAreaHis(GisPosition preGps,GisPosition gisPos)throws Exception{
		String tmnCode = gisPos.getTmnKey();
		String zCode = gisPos.getzCode();
		DcRgAreaHis oldDcRgAreaHis = dcRgAreaRealMap.get(tmnCode);
		if(oldDcRgAreaHis == null){
			//��һ�ν�����������
			/**
			 * ˢ���µ���������
			 */
			DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
			dcRgAreaHis.setTmnCode(tmnCode);
			dcRgAreaHis.setRgAreaCode(zCode);
			dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
			dcRgAreaHis.setStartTime(gisPos.getRptTime());
			dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			return;
		}
		//˵���Ѿ���ĳ����������
		
		//���죬�Ƚ���ǰһ�����������,�����ɵ������������ 
		if(preGps.getRptTime().substring(0, 8).compareTo(gisPos.getRptTime().substring(0, 8))<0){
			/**
			 * �����ϵ���������ʻ��ʱ��
			 */
			oldDcRgAreaHis.setEndTime(preGps.getRptTime());
			dcRgAreaHisQueue.add(oldDcRgAreaHis);
			/**
			 * ˢ���µ���������
			 */
			DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
			dcRgAreaHis.setTmnCode(tmnCode);
			dcRgAreaHis.setRgAreaCode(zCode);
			dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
			dcRgAreaHis.setStartTime(gisPos.getRptTime());
			dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			return;
		}
		oldDcRgAreaHis = dcRgAreaRealMap.get(tmnCode);
		String oldZcode = oldDcRgAreaHis.getRgAreaCode();
		if(StringUtils.isNotBlank(oldZcode) && StringUtils.isNotBlank(zCode) && !zCode.equalsIgnoreCase(oldZcode)){
			//���µ�����������
			//��ǰʱ������ڴ�ʱ�䣬�������
			if(gisPos.getRptTime().compareToIgnoreCase(oldDcRgAreaHis.getStartTime())>0){
				/**
				 * �����ϵ���������ʻ��ʱ��
				 */
				oldDcRgAreaHis.setEndTime(preGps.getRptTime());
				dcRgAreaHisQueue.add(oldDcRgAreaHis);
				/**
				 * ˢ���µ���������
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
				dcRgAreaHis.setStartTime(gisPos.getRptTime());
				dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			}else{//��ǰʱ��С���ڴ�ʱ�䣬�����ڴ����������
				/**
				 * ˢ���µ���������
				 */
				DcRgAreaHis dcRgAreaHis = new DcRgAreaHis();
				dcRgAreaHis.setTmnCode(tmnCode);
				dcRgAreaHis.setRgAreaCode(zCode);
				dcRgAreaHis.setTmnAlias(gisPos.getTmnAlias());
				dcRgAreaHis.setStartTime(gisPos.getRptTime());
				dcRgAreaRealMap.put(tmnCode, dcRgAreaHis);
			}
		}
	}
	
	/**
	 * ˢ���ڴ�
	 * @throws Exception
	 */
	public static void refresh()throws Exception{
		GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
		tmnStaffIdMap = gpsDataDao.getTmnSysIdMap(WqJoinContext.getBean("wqConfig", WqConfig.class).getTmnSysIdSql());
		staffMap  = loadStaffMap();
		msidStaffMap = loadMsidStaffMap();
	}

	/**
	 * ��ʼ����������
	 * @throws Exception
	 */
	public void init()throws Exception{
		refresh();
		
		
		GpsDataDao gpsDataDao = WqJoinContext.getBean("gpsDataDao", GpsDataDao.class);
		List<GisPosition> lastGps = gpsDataDao.loadDbGps();
		if(lastGps!=null){
			for (GisPosition gisPosition : lastGps) {
				gpsLastCache.put(gisPosition.getTmnKey(), gisPosition);
				gpsLocDescMap.put(gisPosition.getTmnKey(), gisPosition);
			}
		}
		List<DcRgAreaHis> gpsDistrictMap = gpsDataDao.loadDcRgAreaReal();
		for (DcRgAreaHis dcRgAreaHis : gpsDistrictMap) {
			dcRgAreaRealMap.put(dcRgAreaHis.getTmnCode(), dcRgAreaHis);
		}
	}
	
}
