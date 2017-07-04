package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import http.body.hex2Response;

/**
 * Created by Me on 2017/5/14.
 */

public class Utils {


    //线程工厂函数,做一点封装的工作，让他能显示当前是第几次new出的Thread
    public static ThreadFactory threadFactor(final String name, final boolean daemon)
    {
        final AtomicInteger count = new AtomicInteger(0);
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread result = new Thread(r,name+":"+count.addAndGet(1));
                result.setDaemon(daemon);
                return result;
            }
        };
    }
    
    //利用bytearrayOutStream将input转化成byte数组
    public static byte[] toByteArray(InputStream input,hex2Response response) throws IOException{
    	ArrayList<String> headerStr = new ArrayList<>();
    	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
    	byte[] patter = "\r\n".getBytes();
    	byte[] bs= new byte[512];
    	ArrayList<Byte> CurrentLine = new ArrayList<>(); 
    	int headlen = 0;
    	int contentlen = -1;
		int len;
		int divive = 0;
		boolean flag = false;
		int totalLen = 0;

		while ((len = input.read(bs)) != -1) { 
			
			//连续2个换行则表示到了数据部分了
			int i = 0;
			if(!flag){
				for(i=0;i<len;i++){
					if(bs[i] == patter[0]){
						if(i!=len-1 && bs[i+1] == patter[1]){
							headlen+=2;
							//遇到换行了将此行读出
							StringBuilder stringBuilder = new StringBuilder();
							for(int j=0;j<CurrentLine.size();j++){
								stringBuilder.append((char)(CurrentLine.get(j).byteValue()));
							}
							String line = stringBuilder.toString();
							//System.out.println(line);
							Log.E(line);
							//将http头保存到list里，不需要再自己解析
							headerStr.add(line);
							if(contentlen == -1){
								contentlen = findContentLength(line);
							}
							//System.out.println(line.length());
							if(line.length()==0){
								flag=true;
								break;
							}
							
							CurrentLine.clear();
							i++;
						}
					}else{
						CurrentLine.add(bs[i]);
						headlen++;
					}
				}
				
			}
			if(flag)
			{
				response.setHeaderStr(headerStr);
				response.setHeadlen(headlen);
				response.setBodylen(contentlen);
				//Log.E("HeadLen:"+headlen+";"+"contentlen:"+contentlen);
			}
		   outputStream.write(bs, 0, len);
		   totalLen += len;
		   if(totalLen== (headlen+contentlen)){
				break;
			}
		}
    	return outputStream.toByteArray();
    }



    public static int findContentLength(String line){
    	if(line.contains(":")){
        	String[] strs = line.split(":");
        	if(strs.length<1){
        		return -1;
        	}
        	if(strs[0].contains("Content-Length")){
        		strs[1] =  strs[1].substring(1, strs[1].length());
        		return Integer.valueOf(strs[1]);
        	}
    	}
    	
    	return -1;
    }




}
