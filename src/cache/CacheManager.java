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
		//如果内存没有缓存，则去磁盘中查找
		//从磁盘查找到的文件需要经过反序列化的一步
		if(response==null && diskCache!=null){
			File file = diskCache.get(hash);
			if(file==null){
				return null;
			}
			Log.E("磁盘命中成功:"+file.getAbsolutePath());
			byte[] bytes = FileUtils.File2Bytes(file.getAbsolutePath());
			response = (Response) FileUtils.ByteToObject(bytes);
		}

		return response;
	}
	
}
