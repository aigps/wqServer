package org.aigps.wq.join;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty4.server.http.IHttpService;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

/**
 * @Title��http ����  �ֻ��ϱ������ݡ�
 * @Description��<������>
 *
 * @author ccq
 * @version 1.0
 *
 * Create Date��  2012-3-2����11:27:44
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company������Ԫ��Ƽ����޹�˾
 */
@Component
public class WqJoinHttpService  implements IHttpService{
	protected static final Log log = LogFactory.getLog(WqJoinHttpService.class);
	
	HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }
	
	public String execute(ChannelHandlerContext ctx, Object request) throws Exception {
		if(request instanceof HttpContent){
			HttpContent httpContent = (HttpContent) request;
			ByteBuf content  = httpContent.content();
			String xmlString = convertXmlToLowerCase(content.toString(CharsetUtil.UTF_8));
			XStream xstream = new XStream();
			try{
				if(xmlString.indexOf("<lia>") != -1){//��λ��Ϣ�ظ�
				}else if(xmlString.indexOf("<lta>") != -1){//����Ӧ����Ϣ
				}else if(xmlString.indexOf("<status>")!=-1){//�ֻ�״̬��Ϣ
				}else if(xmlString.indexOf("<messages>")!=-1){//�ϱ�����Ϣ
				}else if(xmlString.indexOf("<net_test>")!=-1){//���������Ƿ�����
				}else{
					log.error("δ֪�ṹXML:\n" + xmlString);
				}
			}catch(Exception e){
				log.error("�쳣XML:\n" + xmlString);
				log.error(e.getMessage(),e);
			}
			return xmlString;
		}
		return null;
	}

	
	//��XML���<XXXX>��ǩת��Сд
	private static String convertXmlToLowerCase(String xml){
		Pattern pattern = Pattern.compile("<.+?>");
		StringBuilder sb = new StringBuilder();
		int lastIdx = 0;
		Matcher matcher = pattern.matcher(xml);
		while (matcher.find()) {
			String str = matcher.group();
			sb.append(xml.substring(lastIdx, matcher.start()));
			sb.append(str.toLowerCase());
			lastIdx = matcher.end();
		}
		return sb.append(xml.substring(lastIdx)).toString();
	}

	public static String getEndTime(String startTime,int interval,int times) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
		long start = format.parse(startTime).getTime();
		long end = start + interval*times*1000;
		return format1.format(new Date(end));
	}
}
