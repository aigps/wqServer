package org.aigps.wq.join;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����11:17:12
 * Modified By��  <�޸����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
public class WqReceiveServerMsgHandler implements Observer {
	private static final Log log = LogFactory.getLog(WqReceiveServerMsgHandler.class);

	public void update(Observable obs, Object args) {
			try {
				String cmdType = "";
				String phone = "";
				String[] params = null;
				String mobileType = params[0];
				String classId = "";
				
				if("LCSNow".equalsIgnoreCase(cmdType)){//���ζ�λ
//					SmsClient.sendSms(classId, phone, "01", "1", params[1], "0", "#0",params[2]);
				}
				else if("ActiveLCS".equalsIgnoreCase(cmdType)){//��λ����
					int secondInterval = Integer.parseInt(params[1]);//��λ���
					String startTime = params[2];//��ʼʱ��
					String endTime = params[3];//����ʱ��
					String fixModel = params[4];//��λ����
					String workWeekDays = params[5];//�ϰ�����
					String smsSender = params[6];//���
					long diffTime = DateUtil.getBetweenTime(startTime, endTime, "HHmmss");
					if(diffTime <= 0){
						diffTime = 24*60*60*1000 + diffTime;
					}
					long gpsCount = diffTime/1000/secondInterval;
					
					String otherParams = "#" + startTime + "#" + secondInterval + "#" + gpsCount + "#1";
					if("hxylSmsSender".equals(smsSender)){
						otherParams += "#" + endTime + "#" + workWeekDays + "#";
					}
//					SmsClient.sendSms(classId, phone, "02", "3", fixModel, "0", otherParams,smsSender);
				}
				else if("CancelActiveLCS".equalsIgnoreCase(cmdType)){//��λȡ���
//					SmsClient.sendSms(classId, phone, "04", "4", null, null, null,params[1]);
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
	}
}
