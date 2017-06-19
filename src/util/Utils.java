package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static byte[] toByteArray(InputStream input) throws IOException{
    	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
    	byte[] patter = "\r\n".getBytes();
    	byte[] bs= new byte[512];
    	ArrayList<Byte> CurrentLine = new ArrayList<>(); 
    	int headlen = 0;
    	int contentlen = -1;
		int len;
		int divive = 0;
		boolean flag = false;

		while ((len = input.read(bs)) != -1) {  
			//连续2个换行则表示到了数据部分了
			if(!flag){
				for(int i=0;i<len;i++){
					if(bs[i] == patter[0]){
						if(i!=len-1 && bs[i+1] == patter[1]){
							
							//遇到换行了将此行读出
							StringBuilder stringBuilder = new StringBuilder();
							for(int j=0;j<CurrentLine.size();j++){
								stringBuilder.append((char)(CurrentLine.get(j).byteValue()));
							}
							String line = stringBuilder.toString();
							System.out.println(line);
							if(contentlen!=-1){
								contentlen = findContentLength(line);
							}
							System.out.println(line.length());
							if(line.length()==0){
								flag=true;
							}
							
							CurrentLine.clear();
							i++;
						}
					}else{
						CurrentLine.add(bs[i]);
					}
				}
				headlen += len;
			}else{
				System.out.println("HeadLen:"+headlen+";"+"contentlen:"+contentlen);
			}
		   outputStream.write(bs, 0, len);
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
