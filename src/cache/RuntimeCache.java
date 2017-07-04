package cache;

import java.util.HashMap;
import java.util.LinkedList;

import com.sun.org.apache.bcel.internal.generic.NEW;

import http.body.Response;

//使用LRU算法在内存里进行第一步缓存
/**
 * 直接缓存Response对象就行了
 * 
 * */
public class RuntimeCache {
	//缓存的键，用来进行LRU淘汰
	private LinkedList<String> key = new LinkedList<>();
	//缓存的数据
	private HashMap<String,Response> cacheMap = new HashMap<>();
	//内存中缓存数量，超过此数量的cache将被清理
	private long cacheByteLen = 16;
	//默认的缓存大小是5MB
	private long ByteSize = 5*1024*1024;
	//当前缓存的大小
	private long currentSize = 0;
	//内存缓存应当是单例类型
	private static RuntimeCache instance = new RuntimeCache();
	
	private RuntimeCache(){
		
	}
	
	public static RuntimeCache getInstance(){
		return instance;
	}
	

	/**
	 * 根据key返回缓存,并更新一下key的位置，如果不存在返回NULL
	 * */
	public Response get(String hash)
	{
		if(key.contains(hash)){
			key.remove(hash);
			key.addFirst(hash);
			return cacheMap.get(hash);
		}else{
			return null;
		}
	}
	
	/**
	 * 设置缓存，如果缓存已满则删除最后一个然后将此对象加入到队列首部
	 * */
	public boolean set(String hash,Response response){
		//剩余容量够的情况下才进行缓存操作，否则拒绝此操作
		if(ByteSize - currentSize > response.body().length){
			currentSize += response.body().length;
		}else{
			return false;
		}

		//如果本来就还在缓存队列中则更新该队列
		if(key.contains(hash)){
			key.remove(hash);
			key.addFirst(hash);
		}else{
			if(key.size()<cacheByteLen){
				key.add(hash);
			}else{
				//末位淘汰
				String lastKey = key.getLast();
				key.removeLast();
				cacheMap.remove(hash);
				key.addFirst(hash);
			}

		}
		cacheMap.put(hash, response);
		return true;
	}
	
	/**
	 * 清理内存中的缓存
	 * */
	public boolean clean()
	{
		key.clear();
		cacheMap.clear();
		currentSize = 0;
		return true;
	}
	
	/**
	 * @param cacheByteLen 
	 * 			缓存的个数
	 * 		  ByteSize 
	 *  		允许占用内存的大小
	 * */
	public void setCacheParam(long cacheByteLen,long ByteSize) {
		this.cacheByteLen = cacheByteLen;
		this.ByteSize = ByteSize;
	}

}
