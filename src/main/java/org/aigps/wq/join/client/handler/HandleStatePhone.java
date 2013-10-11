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

public class HandleStatePhone extends IHandler{
	private static final Log log = LogFactory.getLog(HandleStatePhone.class);
	
	public static String CMD = "70";
	private static IHandler handler = new HandleStatePhone();
	
	public static IHandler getInstance(){
		return handler;
	}

	//[cmd,water,imsi,state,time] (state 0:关机 1:开机)
	public void receive(Channel channel, String[] msg) {
		log.info("接收开关机信息:" + Arrays.toString(msg));
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
			WqTmnSttsHis wqTmnSttsHis = new WqTmnSttsHis();
			wqTmnSttsHis.setCmd(CMD);
			wqTmnSttsHis.setStaffId(staffId);
			wqTmnSttsHis.setRptTime(rptTime);
			wqTmnSttsHis.setStts(status);
			
			MqMsg mqMsg = new MqMsg(tmnKey, "IMSI", 0, "CMD","UploadStatus");
			mqMsg.setData(wqTmnSttsHis);
			WqMqProducer.addMsg(mqMsg);
			
			IbatisUpdateJob ibatisUpdateJob = WqJoinContext.getBean("ibatisUpdateJob", IbatisUpdateJob.class);
			ibatisUpdateJob.addExeSql("WQ_TMN_STTS_HIS.insert", wqTmnSttsHis);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void send(String imsi, String[] msg) {
	}

}
