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
@Component
@DependsOn("vmMqFactory")
public class WqMqServer implements MessageListener{
	private static final Log log = LogFactory.getLog(WqMqServer.class);
	//����
    private static transient Connection connection;
    //�Ự
    private static transient Session session;
    
    
    //��Ϣ������
    private static transient MessageProducer producer;
    //����Ŀ�ĵ�
    private static transient Destination sendDest;
    //������Ϣ��������
    private static String SEND_DEST_NAME ="WQ.MSG";
	//������Ϣ����
    private static transient Queue<TextMessage> msgSendQueue = new ConcurrentLinkedQueue<TextMessage>();
    //��������Ϣ������
    private static AtomicLong prepareSendCounter = new AtomicLong();
    //�Ѿ�������Ϣ������
    private static AtomicLong sendCounter = new AtomicLong();
    
    //ָ���������
    private static String CMD_DEST_NAME ="WQ.CMD";
    //ָ��Ŀ�ĵ�
    private static transient Destination cmdDest;
    //ָ��������
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
						log.warn("---------------����CANԭʼ��Ϣδ���ͳ�ȥ�����ע��");
						clearMsg();
					}
					while(msgSendQueue.peek()!=null){
						getProducer().send(getSendDest(), msgSendQueue.poll());
						sendCounter.incrementAndGet();
					}
				} catch (Throwable e) {
					log.error("CANʼ��Ϣ�ַ��쳣",e);
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
	 * �����ն�ԭʼ��Ϣ,�ַ��������Ա����ʱ�鿴
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
	 * ������Ϣ���У���ֹ�ڴ������
	 * @throws Exception
	 */
	private static void clearMsg()throws Exception{
		log.error("����������Ϣ");
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
