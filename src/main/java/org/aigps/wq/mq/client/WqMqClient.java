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
    //接收消息的消费者
    private transient MessageConsumer msgConsumer;
    //接收消息队列名字
    private static String MSG_DEST_NAME ="WQ.MSG";
    //接收消息的目的地
    private transient Destination msgDest;
    
    //发送指令的队列名字
    //指令队列名字
    private static String CMD_DEST_NAME ="WQ.CMD";
    //指令目的地
    private transient Destination cmdDest;
    //消息创建者
    private transient MessageProducer producer;
	//发送消息队列
    private  transient Queue<TextMessage> msgSendQueue = new ConcurrentLinkedQueue<TextMessage>();
    //待发送消息计数器
    private  AtomicLong prepareSendCounter = new AtomicLong();
    //已经发送消息计算数
    private  AtomicLong sendCounter = new AtomicLong(); 
    //发送消息的任务触发器
    private ScheduledExecutorService sendScheJob ;
    
    //接收消息的工作池
    private ThreadPoolExecutor workPool = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ScheduledExecutorService recScheJob;
    //待处理的任务计数器
    private AtomicLong poolTaskCounter = new AtomicLong();
    
    
	public void start()throws Exception{
		if(StringUtils.isBlank(brokerUrl)){
			throw new Exception("brokerUrl can't be null, please confirm!");
		}
		//创建链接和会话
		factory = new ActiveMQConnectionFactory(brokerUrl);
    	connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        
        //创建消息消费者
        msgDest = getSession().createTopic(MSG_DEST_NAME);
        msgConsumer = getSession().createConsumer(msgDest);
        msgConsumer.setMessageListener(this);
        
        
        //创建消息生产者
        cmdDest = getSession().createTopic(CMD_DEST_NAME);
        producer = getSession().createProducer(null);
        
        
        //创建发送消息任务
        sendScheJob = Executors.newSingleThreadScheduledExecutor();
        sendScheJob.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(prepareSendCounter.get()-sendCounter.get()>10000){
						log.warn("---------------大量CAN原始消息未发送出去，请关注！");
						clearMsg();
					}
					while(msgSendQueue.peek()!=null){
						producer.send(cmdDest, msgSendQueue.poll());
						sendCounter.incrementAndGet();
					}
				} catch (Throwable e) {
					log.error("CAN始消息分发异常",e);
				}finally{
				}
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
        
        //创建接收消息的监控器
        recScheJob = Executors.newSingleThreadScheduledExecutor();
        recScheJob.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if(workPool == null){
						return;
					}
					long notRunTask = poolTaskCounter.get();
					if(notRunTask >10000){
						log.error("大量消息堆积，未能及时处理，请关注！！！待处理消息数-->"+notRunTask);
					}
					if(notRunTask >50000){
						log.error("----------清理未处理的消息！！！");
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
	 * 关闭链接
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
	 * 加入终端原始消息,分发给监控人员调试时查看
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
	 * 消除消息队列，防止内存溢出。
	 * @throws Exception
	 */
	private void clearMsg()throws Exception{
		log.error("清除缓存的消息");
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
