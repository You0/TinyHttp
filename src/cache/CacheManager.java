package cache;


import java.io.File;

import http.body.Response;
import util.FileUtils;



public class CacheManager {
	private static File cacheDir = new File("C:\\");
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
		//TODO
		if(diskCache!=null){
			
		}
		
		
		return response;
	}
	
	
	
}
