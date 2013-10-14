package org.aigps.wq.mq.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.aigps.wq.mq.MqMsg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.TestMethod;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * @Title：can历史数据消息转发
 * @Description：
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date：  2012-12-5上午10:26:57
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州元码科技有限公司
 */
@Component
@DependsOn("vmMqFactory")
public class WqMqServer implements MessageListener{
	private static final Log log = LogFactory.getLog(WqMqServer.class);
	//链接
    private static transient Connection connection;
    //会话
    private static transient Session session;
    
    
    //消息创建者
    private static transient MessageProducer producer;
    //发送目的地
    private static transient Destination sendDest;
    //发送消息队列名字
    private static String SEND_DEST_NAME ="WQ.MSG";
	//发送消息队列
    private static transient Queue<TextMessage> msgSendQueue = new ConcurrentLinkedQueue<TextMessage>();
    //待发送消息计数器
    private static AtomicLong prepareSendCounter = new AtomicLong();
    //已经发送消息计算数
    private static AtomicLong sendCounter = new AtomicLong();
    
    //指令队列名字
    private static String CMD_DEST_NAME ="WQ.CMD";
    //指令目的地
    private static transient Destination cmdDest;
    //指令消费者
    private static transient MessageConsumer cmdConsumer;
    /**
	 * 
	 */
	public WqMqServer()throws Exception {
        getProducer();
        getCmdConsumer().setMessageListener(this);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(prepareSendCounter.get()-sendCounter.get()>10000){
						log.warn("---------------大量CAN原始消息未发送出去，请关注！");
						clearMsg();
					}
					while(msgSendQueue.peek()!=null){
						getProducer().send(getSendDest(), msgSendQueue.poll());
						sendCounter.incrementAndGet();
					}
				} catch (Throwable e) {
					log.error("CAN始消息分发异常",e);
				}finally{
				}
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
	}    
	
	/**
	 * 加入终端原始消息,分发给监控人员调试时查看
	 * @param tmnCode
	 * @param msg
	 * @throws Exception
	 */
	public static void addMsg(MqMsg mqMsg)throws Exception{
		if(getSession() != null){
			TextMessage message = getSession().createTextMessage();
			message.setText(JSON.toJSONString(mqMsg));
			msgSendQueue.add(message);
			prepareSendCounter.incrementAndGet();
		}
	}
	/**
	 * 消除消息队列，防止内存溢出。
	 * @throws Exception
	 */
	private static void clearMsg()throws Exception{
		log.error("清除缓存的消息");
		while(msgSendQueue.peek()!=null){
			msgSendQueue.poll();
			sendCounter.incrementAndGet();
		}
	}
	
	public static void main(String[] args) {
		try {
			VmMqFactory factory = new VmMqFactory();
			factory.setUrl("tcp://127.0.0.1:12300");
			factory.init();
			WqMqServer canMsgMqService = new WqMqServer();
			while(true){
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	public void onMessage(Message message) {
		try {
			if(message instanceof TextMessage){
				TextMessage textMsg = (TextMessage)message;
				String jsonStr = textMsg.getText();
				MqMsg mqMsg = JSON.parseObject(jsonStr, MqMsg.class);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
	}

	public static Connection getConnection()throws Exception{
		if(connection == null){
			connection = VmMqFactory.getFactory().createConnection();
	        connection.start();
		}
		return connection;
	}
	
	public static Session getSession()throws Exception{
		if(session == null){
			session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
		}
		return session;
	}

	public static MessageProducer getProducer()throws Exception {
		if(producer == null){
			producer = getSession().createProducer(null);
		}
		return producer;
	}

	public static Destination getSendDest()throws Exception {
		if(sendDest == null){
			 sendDest = getSession().createTopic(SEND_DEST_NAME);
		}
		return sendDest;
	}

	public static Destination getCmdDest()throws Exception {
		if(cmdDest==null){
        	cmdDest = getSession().createTopic(CMD_DEST_NAME);
    	}
		return cmdDest;
	}

	public static MessageConsumer getCmdConsumer()throws Exception {
		if(cmdConsumer == null){
			cmdConsumer = getSession().createConsumer(getCmdDest());
		}
		return cmdConsumer;
	}
	
}
