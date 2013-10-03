package org.aigps.wq.join.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.aigps.wq.join.client.handler.HandleActive;
import org.aigps.wq.join.client.handler.HandleKeepAlive;
import org.aigps.wq.join.client.handler.HandlePosition;
import org.aigps.wq.join.client.handler.HandleResponse;
import org.aigps.wq.join.client.handler.HandleSetting;
import org.aigps.wq.join.client.handler.HandleSign;
import org.aigps.wq.join.client.handler.HandleStateGps;
import org.aigps.wq.join.client.handler.HandleStateNet;
import org.aigps.wq.join.client.handler.HandleStatePhone;
import org.aigps.wq.join.client.handler.IHandler;
import org.aigps.wq.join.common.ChannelUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	private static final Log log = LogFactory.getLog(ClientHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	log.error("�յ��ֻ�ͻ�������");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ChannelUtil.removeDevice(ctx.channel());
    	ctx.close();
    	log.error("�ֻ�ͻ��˹ر�����");
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(!(msg instanceof String)){
    		return;
    	}
    	// [cmd,water,imsi,...]
    	String[] items = ((String)msg).split("\\|");
		ChannelUtil.setChannel(items[2], ctx.channel());
		
		IHandler handler = getHandler(items[0]);
		if (handler != null) {
			handler.receive(ctx.channel(), items);
		}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


	private static IHandler getHandler(String cmd) {
		if (HandleResponse.CMD.equals(cmd)) {//ͨ��Ӧ��
			return HandleResponse.getInstance();
		}
		if (HandleActive.CMD.equals(cmd)) {//����
			return HandleActive.getInstance();
		}
		if (HandleSetting.CMD.equals(cmd)) {//����
			return HandleSetting.getInstance();
		}
		if (HandleKeepAlive.CMD.equals(cmd)) {//����
			return HandleKeepAlive.getInstance();
		}
		if (HandlePosition.CMD.equals(cmd)) {//��λ
			return HandlePosition.getInstance();
		}
		if (HandleSign.CMD.equals(cmd)) {//ǩ��
			return HandleSign.getInstance();
		}
		if (HandleStatePhone.CMD.equals(cmd)) {//�ֻ�ػ�
			return HandleStatePhone.getInstance();
		}
		if (HandleStateNet.CMD.equals(cmd)) {//�ֻ������
			return HandleStateNet.getInstance();
		}
		if (HandleStateGps.CMD.equals(cmd)) {//�ֻ��GPS
			return HandleStateGps.getInstance();
		}
		
		return null;
	}
}
