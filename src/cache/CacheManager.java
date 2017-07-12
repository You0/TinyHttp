package cache;


import java.io.File;
import java.io.FileOutputStream;

import http.body.Response;
import util.FileUtils;
import util.Log;



public class CacheManager {
	private static File cacheDir = new File("D:\\cache");
	private static RuntimeCache runtimeCache = RuntimeCache.getInstance();
	private static DiskCache diskCache = new DiskCache(cacheDir, new FileUtils(), 30*1024*1024);
	
	
	public static void setCacheDir(File cacheDir) {
		CacheManager.cacheDir = cacheDir;
	}
	
	
	public static boolean set(String hash,Response response){
		if(runtimeCache!=null){
			runtimeCache.set(hash, response);
		}

		if(diskCache!=null){
			diskCache.put(response,hash);
		}
		return true;
	}
	
	
	public static Response get(String hash){
		Response response = null;
		if(runtimeCache!=null){
			response = runtimeCache.get(hash);
		}
		//����ڴ�û�л��棬��ȥ�����в���
		//�Ӵ��̲��ҵ����ļ���Ҫ���������л���һ��
		if(response==null && diskCache!=null){
			File file = diskCache.get(hash);
			if(file==null){
				return null;
			}
			Log.E("�������гɹ�:"+file.getAbsolutePath());
			byte[] bytes = FileUtils.File2Bytes(file.getAbsolutePath());
			response = (Response) FileUtils.ByteToObject(bytes);
		}

		return response;
	}
	
}
