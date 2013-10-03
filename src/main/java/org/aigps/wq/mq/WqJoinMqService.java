package org.aigps.wq.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

/**
 * @Title��can��ʷ������Ϣת��
 * @Description��
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-12-5����10:26:57
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
public class WqJoinMqService {
	private static final Log log = LogFactory.getLog(WqJoinMqService.class);
    private static transient Connection connection;
    private static transient Session session;
    private static transient MessageProducer producer;
    
    private static transient Destination destination;
    
    private static transient Queue<TextMessage> msgQueue = new ConcurrentLinkedQueue<TextMessage>();
    
    
    private static AtomicLong recCounter = new AtomicLong();
    private static AtomicLong sendCounter = new AtomicLong();

	
	
    /**
	 * 
	 */
	public WqJoinMqService()throws Exception {
    	connection = VmMQFactory.getFactory().createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
        destination = session.createTopic("WQ.MSG");
        
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(recCounter.get()-sendCounter.get()>10000){
						log.warn("---------------����CANԭʼ��Ϣδ���ͳ�ȥ�����ע��");
					}
					while(msgQueue.peek()!=null){
						producer.send(destination, msgQueue.poll());
						sendCounter.incrementAndGet();
					}
				} catch (Throwable e) {
					log.error("CANʼ��Ϣ�ַ��쳣",e);
				}finally{
				}
			}
		}, 0, 200, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * �����ն�ԭʼ��Ϣ,�ַ��������Ա����ʱ�鿴
	 * @param tmnCode
	 * @param msg
	 * @throws Exception
	 */
	public static void addMsg(MqMsg mqMsg)throws Exception{
		if(session != null){
			TextMessage message = session.createTextMessage();
			message.setText(JSON.toJSONString(mqMsg));
			msgQueue.add(message);
			recCounter.incrementAndGet();
		}
	}
	
	public static void main(String[] args) {
		
		try {
			VmMQFactory factory = new VmMQFactory("tcp://127.0.0.1:12300");
			WqJoinMqService canMsgMqService = new WqJoinMqService();
			while(true){
				Thread.sleep(100);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
