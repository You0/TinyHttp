package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import http.body.Response;

public class FileUtils {
	
	/**
	 * path �����ļ��ĸ�Ŀ¼
	 * response Ҫ���л���Ϊ�ļ��Ķ���
	 * endWith �ļ��ĺ�׺��
	 * */
	public File CreateFile(File path,Response response,String filename)
	{
		File file = new File(path.getAbsolutePath()+"//"+filename);
		byte[] bs = ObjectToByte(response);
		
		FileOutputStream fos = null;
		
		try{
			fos = new FileOutputStream(file);
			fos.write(bs);
			fos.flush();
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return file;
	}
	
	
	/**
	 * path �����ļ��ĸ�Ŀ¼
	 * data �ļ����ֽ���
	 * endWith �ļ��ĺ�׺��
	 * */
	public File CreateFile(File path,Byte[] data,String filename)
	{
		File file = null;
		
		
		return file;
	}
	
	
	
	public static Object ByteToObject(byte[] bytes) {  
	    Object obj = null;  
	    try {  
	        // bytearray to object  
	        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
	        ObjectInputStream oi = new ObjectInputStream(bi);  
	      
	        obj = oi.readObject();  
	        bi.close();  
	        oi.close();  
	    } catch (Exception e) {  
	        System.out.println("translation" + e.getMessage());  
	        e.printStackTrace();  
	    }  
	     return obj;  
	}  



	public static byte[] ObjectToByte(java.lang.Object obj) {  
	    byte[] bytes = null;  
	    try {  
	        // object to bytearray  
	        ByteArrayOutputStream bo = new ByteArrayOutputStream();  
	        ObjectOutputStream oo = new ObjectOutputStream(bo);  
	        oo.writeObject(obj);  

	        bytes = bo.toByteArray();  

	        bo.close();  
	        oo.close();  
	    } catch (Exception e) {  
	        System.out.println("translation" + e.getMessage());  
	        e.printStackTrace();  
	    }  
	    return bytes;  
	}  
	
	
    public static byte[] File2Bytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }  

	
}
