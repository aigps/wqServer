package org.aigps.wq.join.client.handler;

import io.netty.channel.Channel;

import java.util.Arrays;

import org.aigps.wq.DcGpsCache;
import org.aigps.wq.WqJoinContext;
import org.aigps.wq.entity.WqTmnSttsHis;
import org.aigps.wq.ibatis.IbatisUpdateJob;
import org.aigps.wq.mq.MqMsg;
import org.aigps.wq.mq.WqMqProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.common.DateUtil;

public class HandleSign extends IHandler{
	private static final Log log = LogFactory.getLog(HandleSign.class);
	
	public static String CMD = "60";
	private static IHandler handler = new HandleSign();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time] (state 0:签退 1:签到)
	public void receive(Channel channel, String[] msg) {
		log.info("接收签到信息:" + Arrays.toString(msg));
		//通用应答回复
		this.response(channel, msg[1], msg[0]);
		try {
			String tmnKey = msg[2];
			String status = msg[3];
			String rptTime = msg[4];
			String staffId = DcGpsCache.getTmnSysIdMap().get(tmnKey);
			if(StringUtils.isBlank(staffId)){
				staffId = tmnKey;
			}
			//主动签到签退，发送状态
				
			WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
			wqTmnSttsHis.setStaffId(staffId);
			wqTmnSttsHis.setRptTime(rptTime);
			wqTmnSttsHis.setStts(status);
			wqTmnSttsHis.setCmd(CMD);
			MqMsg mqMsg = new MqMsg(tmnKey, "IMSI", 0, "CMD","UploadStatus");
			mqMsg.setData(wqTmnSttsHis);
			WqMqProducer.addMsg(mqMsg);
			
			IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
			ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
			//.....
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void send(String imsi, String[] msg) {
	}

}
