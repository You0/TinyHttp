package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.swing.internal.plaf.basic.resources.basic;

import http.body.hex2Response;
import jdk.internal.dynalink.beans.StaticClass;

/**
 * Created by Me on 2017/5/14.
 */

public class Utils {
	
	// 线程工厂函数,做一点封装的工作，让他能显示当前是第几次new出的Thread
	public static ThreadFactory threadFactor(final String name, final boolean daemon) {
		final AtomicInteger count = new AtomicInteger(0);
		return new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread result = new Thread(r, name + ":" + count.addAndGet(1));
				result.setDaemon(daemon);
				return result;
			}
		};
	}

	// 利用bytearrayOutStream将input转化成byte数组
	public static byte[] toByteArray(InputStream input, hex2Response response) throws IOException {
		ArrayList<String> headerStr = new ArrayList<>();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] patter = "\r\n".getBytes();

		byte[] bs = new byte[512];
		ArrayList<Byte> CurrentLine = new ArrayList<>();
		int headlen = 0;
		int contentlen = -1;
		int len;
		int divive = 0;
		boolean flag = false;
		int totalLen = 0;
		Boolean Error = true;
		while ((len = input.read(bs)) != -1) {

			// 连续2个换行则表示到了数据部分了
			int i = 0;
			if (!flag) {
				for (i = 0; i < len; i++) {
					if (bs[i] == patter[0]) {
						if (i != len - 1 && bs[i + 1] == patter[1]) {
							headlen += 2;
							// 遇到换行了将此行读出
							StringBuilder stringBuilder = new StringBuilder();
							for (int j = 0; j < CurrentLine.size(); j++) {
								stringBuilder.append((char) (CurrentLine.get(j).byteValue()));
							}
							String line = stringBuilder.toString();
							Log.E(line);
							// 将http头保存到list里，不需要再自己解析
							headerStr.add(line);
							if (contentlen == -1) {
								contentlen = findContentLength(line);
							}
							// System.out.println(line.length());
							if (line.length() == 0) {
								flag = true;
								break;
							}

							CurrentLine.clear();
							i++;
						}
					} else {
						CurrentLine.add(bs[i]);
						headlen++;
					}
				}

			}
			if (flag) {
				response.setHeaderStr(headerStr);
				response.setHeadlen(headlen);
				response.setBodylen(contentlen);
				// Log.E("HeadLen:"+headlen+";"+"contentlen:"+contentlen);
			}

			outputStream.write(bs, 0, len);
			totalLen += len;

			// 表明是chunked模式到结尾了
			
			if (contentlen == -1) {
				int tag;
				if((tag=ChunkedEnd(bs))==1){
					System.out.println("trunked");
					break;
				}else if(tag==-1){
					Error = false;
					break;
				}
				
			}

			if (totalLen == (headlen + contentlen)) {
				break;
			}
		}
		//System.out.println("trunkedddddd");
		//System.out.println(new String(outputStream.toByteArray(), "utf-8"));
		
		if(contentlen==-1 && Error){
			List<Byte> bytes = readGzipBody(parse(outputStream,headlen),0);
			System.out.println("递归结束");
			response.setBodylen(bytes.size());
			response.setHeadlen(0);
			byte[] bs2 = new byte[bytes.size()];
			for(int i=0;i<bs2.length;i++){
				bs2[i] = bytes.get(i);
			}
			return bs2;
		}
		
		
		Error = true;
		return outputStream.toByteArray();
	}

	public static int findContentLength(String line) {
		if (line.contains(":")) {
			String[] strs = line.split(":");
			if (strs.length < 1) {
				return -1;
			}
			if (strs[0].contains("Content-Length")) {
				strs[1] = strs[1].substring(1, strs[1].length());
				return Integer.valueOf(strs[1]);
			}
		}

		return -1;
	}

	private static int ChunkedEnd(byte[] bs) {
		// System.out.println("ChunkedEnd");
		byte[] Chunkedend = "0\r\n\r\n".getBytes();
		boolean tag = true;
		for (int i = 0; i < bs.length; i++) {
			for (int j = 0; j < Chunkedend.length; j++) {

				if (i + j >= bs.length) {
					System.out.println("outIndex");
					return 0;
					
				}
				if (bs[i + j] == Chunkedend[j]) {
					tag = true;
				} else {
					tag = false;
					break;
				}
			}
			if (tag == true) {
				break;
			}
		}

		return tag?1:0;
	}

	private static List<Byte> readGzipBody(InputStream is,int deep) throws IOException {
		// 压缩块的大小，由于chunked编码块的前面是一个标识压缩块大小的16进制字符串，在开始读取前，需要获取这个大小
		
		int chunk = getChunkSize(is);
		List<Byte> bodyByteList = new ArrayList<Byte>();
		if(deep>10){
			return bodyByteList;
		}
		byte readByte = 0;
		int count = 0;

		while (count < chunk) { // 读取消息体，最多读取chunk个byte
			readByte = (byte) is.read();
			bodyByteList.add(Byte.valueOf(readByte));
			count++;
		}
		if (chunk > 0) { // chunk为读取到最后，如果没有读取到最后，那么接着往下读取。
			List<Byte> tmpList = readGzipBody(is,deep+1);
			bodyByteList.addAll(tmpList);
		}
		
		return bodyByteList;
	}

	private static int getChunkSize(InputStream is) throws IOException {
		String sLength = readLine(is).trim();
		if (sLength.equals("")) { 
			sLength = readLine(is).trim();
		}
		if (sLength.length() < 4) {
			sLength = 0 + sLength;
		}
		// 把16进制字符串转化为Int类型
		int length = Integer.valueOf(sLength, 16);
		return length;
	}
	
	
	
	private static String readLine(InputStream is) {
		byte[] patter = "\r\n".getBytes();
		ArrayList<Byte> CurrentLine = new ArrayList<>();
		byte cb;
		try {
			while((cb=(byte) is.read())!=patter[0]){
				CurrentLine.add(cb);
			}
			is.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (int j = 0; j < CurrentLine.size(); j++) {
			stringBuilder.append((char) (CurrentLine.get(j).byteValue()));
		}
		String line = stringBuilder.toString();
		
		return line;
	}

	public static ByteArrayInputStream parse(OutputStream out,int headlen)
    {
        ByteArrayOutputStream   baos=new   ByteArrayOutputStream();
        baos=(ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        
        for(int i=0;i<headlen;i++){
        	swapStream.read();
        }
        return swapStream;
    }
	

}
