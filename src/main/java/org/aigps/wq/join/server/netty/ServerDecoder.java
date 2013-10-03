package org.aigps.wq.join.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * Title:��ʢЭ����������
 * Description: 
 * </pre>
 * @author chencongquan  chencongquan@gmail.com
 * @version 1.00.00
 * <pre>
 * �޸ļ�¼
 *    �޸ĺ�汾:     �޸��ˣ�  �޸�����:     �޸�����: 
 * </pre>
 */
@SuppressWarnings("unused")
public class ServerDecoder extends ByteToMessageDecoder{
	private static final Log log = LogFactory.getLog("PhoneDecoder");
	
	private static char SPLIT_CHART = '&';
	
	private static int indexOf(ByteBuf in, int startIndex, int length){
		for (int i = startIndex; i < length; i++) {
			if (in.getByte(i) == SPLIT_CHART) {
				return i;
			}
		}
		return -1;
	}
	
	//...&cmd1&&cmd2&...
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			if (in.readableBytes() < 4) {
				return;
			}
			in.markReaderIndex();
			int length = in.readableBytes();
			
			int startIndex = indexOf(in, 0, length);
			if (startIndex == -1) {
				in.readBytes(new byte[length]);
				return;
			}

			int endIndex = indexOf(in, startIndex + 1, length);
			if (endIndex == -1) {
				Object o = (startIndex > 0) ? in.readBytes(new byte[startIndex]) : in.resetReaderIndex();
				return;
			}
			
			//����&&������ȥ����һ����
			if(endIndex - startIndex == 1) {
				in.readBytes(new byte[startIndex + 1]);
				return;
			}

			byte[] bytes = new byte[endIndex + 1];
			in.readBytes(bytes);

			//ȥ��ͷ��β��&
			byte[] array = new byte[endIndex - startIndex - 1];
			System.arraycopy(bytes, startIndex + 1, array, 0, array.length);
			out.add(new String(array));
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				in.clear();
			} catch (Exception e1) {
				ctx.close();
			}
		}
	}
	
}
