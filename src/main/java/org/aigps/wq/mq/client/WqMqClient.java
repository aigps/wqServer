package org.aigps.wq.mq.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.aigps.wq.mq.MqMsg;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

public abstract class WqMqClient implements MessageListener{
	private static final Log log = LogFactory.getLog(WqMqClient.class);
	
	private String brokerUrl ;
	private transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;
    //������Ϣ��������
    private transient MessageConsumer msgConsumer;
    //������Ϣ��������
    private static String MSG_DEST_NAME ="WQ.MSG";
    //������Ϣ��Ŀ�ĵ�
    private transient Destination msgDest;
    
    //����ָ��Ķ�������
    //ָ���������
    private static String CMD_DEST_NAME ="WQ.CMD";
    //ָ��Ŀ�ĵ�
    private transient Destination cmdDest;
    //��Ϣ������
    private transient MessageProducer producer;
	//������Ϣ����
    private  transient Queue<TextMessage> msgSendQueue = new ConcurrentLinkedQueue<TextMessage>();
    //��������Ϣ������
    private  AtomicLong prepareSendCounter = new AtomicLong();
    //�Ѿ�������Ϣ������
    private  AtomicLong sendCounter = new AtomicLong(); 
    //������Ϣ�����񴥷���
    private ScheduledExecutorService sendScheJob ;
    
    //������Ϣ�Ĺ�����
    private ThreadPoolExecutor workPool = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ScheduledExecutorService recScheJob;
    //�����������������
    private AtomicLong poolTaskCounter = new AtomicLong();
    
    
	public void start()throws Exception{
		if(StringUtils.isBlank(brokerUrl)){
			throw new Exception("brokerUrl can't be null, please confirm!");
		}
		//�������ӺͻỰ
		factory = new ActiveMQConnectionFactory(brokerUrl);
    	connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        
        //������Ϣ������
        msgDest = getSession().createTopic(MSG_DEST_NAME);
        msgConsumer = getSession().createConsumer(msgDest);
        msgConsumer.setMessageListener(this);
        
        
        //������Ϣ������
        cmdDest = getSession().createTopic(CMD_DEST_NAME);
        producer = getSession().createProducer(null);
        
        
        //����������Ϣ����
        sendScheJob = Executors.newSingleThreadScheduledExecutor();
        sendScheJob.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(prepareSendCounter.get()-sendCounter.get()>10000){
						log.warn("---------------����CANԭʼ��Ϣδ���ͳ�ȥ�����ע��");
						clearMsg();
					}
					while(msgSendQueue.peek()!=null){
						producer.send(cmdDest, msgSendQueue.poll());
						sendCounter.incrementAndGet();
					}
				} catch (Throwable e) {
					log.error("CANʼ��Ϣ�ַ��쳣",e);
				}finally{
				}
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
        
        //����������Ϣ�ļ����
        recScheJob = Executors.newSingleThreadScheduledExecutor();
        recScheJob.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(workPool == null){
						return;
					}
					long notRunTask = poolTaskCounter.get();
					if(notRunTask >10000){
						log.error("������Ϣ�ѻ���δ�ܼ�ʱ���������ע��������������Ϣ��-->"+notRunTask);
					}
					if(notRunTask >50000){
						log.error("----------����δ��������Ϣ������");
						workPool.getQueue().clear();
						poolTaskCounter.set(0);
					}
				} catch (Throwable e) {
					log.error(e.getMessage(),e);
				}
			}
		}, 10, 5, TimeUnit.SECONDS);
	}
	
	/**
	 * �ر�����
	 * @throws JMSException
	 */
	public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
        if(sendScheJob != null){
        	sendScheJob.shutdown();
        }
	} 
	
	/**
	 * �����ն�ԭʼ��Ϣ,�ַ��������Ա����ʱ�鿴
	 * @param tmnCode
	 * @param msg
	 * @throws Exception
	 */
	public  void addMsg(MqMsg mqMsg)throws Exception{
		if(getSession() != null){
			TextMessage message = getSession().createTextMessage();
			message.setText(JSON.toJSONString(mqMsg));
			msgSendQueue.add(message);
			prepareSendCounter.incrementAndGet();
		}
	}
	
	
	private Session getSession()throws Exception{
		if(factory == null){
			throw new Exception("you haven't start client yet!");
		}
		return session;
	}
	
	/**
	 * ������Ϣ���У���ֹ�ڴ������
	 * @throws Exception
	 */
	private void clearMsg()throws Exception{
		log.error("����������Ϣ");
		while(msgSendQueue.peek()!=null){
			msgSendQueue.poll();
			sendCounter.incrementAndGet();
		}
	}
	
	public abstract void OnMqMsq(MqMsg mqMsg);

	public void onMessage(Message message) {
		try {
			if(message instanceof TextMessage){
				TextMessage textMsg = (TextMessage)message;
				String jsonStr = textMsg.getText();
				final MqMsg mqMsg = JSON.parseObject(jsonStr, MqMsg.class);
				poolTaskCounter.incrementAndGet();
				workPool.execute(new Runnable() {
					public void run() {
						OnMqMsq(mqMsg);
						poolTaskCounter.decrementAndGet();
					}
				});
				
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	
}