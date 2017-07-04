package cache;


import http.body.Response;



public class CacheManager {

	private static RuntimeCache runtimeCache = RuntimeCache.getInstance();
	private static DiskCache diskCache = null;
	
	
	public static boolean set(String hash,Response response){
		if(runtimeCache!=null){
			runtimeCache.set(hash, response);
		}
		//TODO
		if(diskCache!=null){
			
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
