package cache;

import java.util.HashMap;
import java.util.LinkedList;

//使用LRU算法在内存里进行第一步缓存
public class RuntimeCache {
	private LinkedList<Object> key;
	private HashMap<Object,Object> cacheMap;
	private long cacheByteLen = 0;
	
	
	
}
