package org.aigps.wq.service;

import java.util.ArrayList;
import java.util.List;
import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.GisPosition;
import org.aigps.wq.entity.WqTmnSttsHis;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqJoinMqService;
import org.aigps.wq.util.DistrictUtil;
import org.aigps.wq.xmlmodel.LiaModel;
import org.aigps.wq.xmlmodel.Picture;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;
import org.gps.util.common.LonLatUtil;
import com.thoughtworks.xstream.XStream;


public class GpsService {
	private static final Log log = LogFactory.getLog(GpsService.class);
	public static boolean correctGpsTime(String maxFutureTime,GisPosition gisPos)throws Exception{
		boolean retFlag = true;
		String gpsTime = gisPos.getRptTime();
		//��λʱ�䳬��ϵͳʱ��
		if(maxFutureTime.compareTo(gpsTime)<=0){
			retFlag = false;
			if(log.isErrorEnabled()){
				log.error("�ն�:"+gisPos.getTmnKey()+"�ϱ���δ��ʱ��-->"+gpsTime+"��λ, ����!");
			}
		}
		return retFlag;
	}
	
	public static String execute(String xmlString, XStream xstream) throws Exception {
		xstream.processAnnotations(LiaModel.class);
		LiaModel model = (LiaModel) xstream.fromXML(xmlString);
		List<GisPosition> gpsList = model.toGps();
		if(gpsList == null || gpsList.isEmpty()){
			log.error("�޶�λ��ϢXML:\n" + xmlString);
			return xmlString;
		}
		String phone = model.getPhone(), msId = model.getMsid();
		log.error("�ж�λ��ϢXML:\n" + xmlString);
		Picture pic = model.getLia().getPicture();
		
		//�ϱ���λ��Ϣ
		for(GisPosition gps : gpsList){
			String tmnKey = StringUtils.isBlank(phone) ? msId : phone;
			gps.setTmnKey(tmnKey);
			//��λ����
			receiveGpsInfo(tmnKey, gps);
		}
		
		String staffId = DcGpsCache.getTmnSysIdMap().get(phone);
		//����ǩ��ǩ�ˣ�����״̬
		if(model.isSignIn() || model.isSignOut()){
			String status = model.isSignIn()?"98":"99";
			
			WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
			wqTmnSttsHis.setStaffId(msId);
			wqTmnSttsHis.setRptTime(DateUtil.converDateFormat(model.getTime(), DateUtil.YMDHMS, DateUtil.YMD_HMS));
			wqTmnSttsHis.setStts(status);
			
			MqMsg mqMsg = new MqMsg(msId, "IMSI", 0, "CMD","UploadStatus");
			mqMsg.setData(wqTmnSttsHis);
			WqJoinMqService.addMsg(mqMsg);
			
			IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
			ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
		}else if(model.isPicture() && pic!=null){//����
			pic.setTime(model.getTime());
			pic.setMsid(msId);
		}
		return null;
	}
	
	
	/**
	 * ������λ����
	 * @param tmnCode
	 * @param ymGpsModel
	 * @throws Exception
	 */
	public static void receiveGpsInfo(String tmnCode,GisPosition gisPos)
			throws Exception {
		//��������ն���ҵ��ID�Ķ���,���ö���
		if(DcGpsCache.getTmnSysIdMap().containsKey(tmnCode)){
			gisPos.setTmnAlias(DcGpsCache.getTmnSysIdMap().get(tmnCode));
		}
		String sysTime = DateUtil.sysNumDateTime;
		String nextHour = DateUtil.sysNumNextHour;
		//�����λ
		GisPosition preGps = DcGpsCache.getLastGps(tmnCode);
		if(preGps!=null){
			if(StringUtils.isNotBlank(preGps.getRptTime()) && preGps.getRptTime().equalsIgnoreCase(gisPos.getRptTime())){
				log.warn(tmnCode+" �ϱ��ظ���λ��Ϣ:"+gisPos.getRptTime());
				return;
			}
		}

		//�������ȷʱ������ܾ�����ʱ�䣬���ն�λ
		if(! correctGpsTime(nextHour, gisPos)){
			return;
		}
		//���ý���ʱ��
		gisPos.setServerTime(sysTime);
		//�������Ч��λ
		if(LonLatUtil.isValidLocation(gisPos.getLon(), gisPos.getLat())){
			//��������
			String zCode = DistrictUtil.getCityZcode(gisPos.getLon(), gisPos.getLat());
			gisPos.setzCode(zCode);
		}else{//��Ч��λ��ȡ�����Ч��λ
			if(preGps!=null){//�Ѿ����������Ч��λ
				/**
				 * ��ʱ��������Ч��λ
				 */
				gisPos.setLon(preGps.getLon());
				gisPos.setLat(preGps.getLat());
				gisPos.setzCode(preGps.getzCode());
			}
		}
		
		//������������
		DcGpsCache.updateDcRgAreaHis(preGps,gisPos);
		//�������λ
		DcGpsCache.updateLastGps(tmnCode, gisPos);
		
		//������λ��Ϣ
		DcGpsCache.gpsAddCache.add(gisPos);
		
		MqMsg mqMsg = new MqMsg(tmnCode, "WQ", 0, "GPS","RPT");
		mqMsg.setData(gisPos);
		WqJoinMqService.addMsg(mqMsg);
	}
}
