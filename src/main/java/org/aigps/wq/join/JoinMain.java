package org.aigps.wq.join;

import org.aigps.wq.join.client.netty.ClientStartServer;
import org.aigps.wq.join.common.Cfg;
import org.aigps.wq.join.server.netty.ServerStartServer;
import org.aigps.wq.join.startup.JobServer;
import org.aigps.wq.join.startup.SpringContext;
import org.gps.map.offset.OffSetCache2;



public class JoinMain {

	public static void main(String[] args) throws Exception{
		try {
			// ����spring������
			SpringContext.startup();

			// ����ƫ������
			OffSetCache2.loadData();

			// ����ϵͳJOB
			JobServer.startup();
			
			// ����رռ���
			setShutdownListener();
			
			//�����ֻ��ͻ��˼����˿�
			new ClientStartServer(Cfg.tcpClientPort);

			//�����ֻ�����˼����˿�
			new ServerStartServer(Cfg.tcpServerPort);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// ����ر�ʱ
	private static void setShutdownListener() {
		Thread thread = new Thread() {
			public void run() {
				JobServer.destroy();
			}
		};
		thread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(thread);
	}
	
}
