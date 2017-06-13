package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Me on 2017/5/14.
 */

public class Utils {


    //�̹߳�������,��һ���װ�Ĺ�������������ʾ��ǰ�ǵڼ���new����Thread
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
    
    //����bytearrayOutStream��inputת����byte����
    public static byte[] toByteArray(InputStream input) throws IOException{
    	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
    	
    	byte[] bs= new byte[512];
		int len;

		while ((len = input.read(bs)) != -1) {  
			System.out.println(len);
		   outputStream.write(bs, 0, len);
		   
		   
		}  
    	return outputStream.toByteArray();
    }








}
