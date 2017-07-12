package cache;

import java.io.File;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import http.body.Response;
import util.FileUtils;
import util.Log;

/**
 * 使用LRU算法进行磁盘缓存 需要由用户指定缓存的目录
 */
public class DiskCache {
	// 记录缓存文件的大小
	private final AtomicInteger cacheSize;
	// 缓存文件的最大值
	private final int sizeLimit;

	private final File cacheDir;
	
	private final FileUtils fileUtil;

	private final ConcurrentHashMap<File, Long> lastUsageDates = new ConcurrentHashMap<>();

	public DiskCache(File cacheDir, FileUtils util, int limit) {
		this.cacheDir = cacheDir;
		cacheSize = new AtomicInteger(0);
		sizeLimit = 1;
		fileUtil = util;
		calculateCacheSizeAndFillMap();
	}

	// 开一个线程去扫描已经缓存的文件,并将内容填充到map中
	private void calculateCacheSizeAndFillMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long size = 0;
				File[] cachedFiles = cacheDir.listFiles();
				if (cachedFiles != null) {
					for (File cacheFile : cachedFiles) {
						size = cacheFile.getTotalSpace();
						lastUsageDates.put(cacheFile, cacheFile.lastModified());
					}
				}
				cacheSize.set((int) size);
				Log.E("扫描完成");
			}
		}).start();
	}

	public void put(Response response,String filename) {
		File file = fileUtil.CreateFile(cacheDir, response, filename);
		long valueSize = file.getTotalSpace();
		// 获得当前缓存的文件大小
		int currentCacheSize = cacheSize.get();
		Log.E("currentCacheSize" + currentCacheSize);
		// 剩余的缓存空间是否满足缓存文件
		while (currentCacheSize + valueSize > sizeLimit) {
			int freedSize = removeNext();

			if (freedSize == 0) {
				break;
			} else {
				currentCacheSize = cacheSize.addAndGet(-freedSize);
			}
		}
		
		cacheSize.addAndGet((int) valueSize);
		Long currentTime = System.currentTimeMillis();  
        file.setLastModified(currentTime);  
        lastUsageDates.put(file, currentTime);  
		Log.E("DiskCache 缓存成功");
	}
	
	
	public File get(String filename){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		Log.E("检索磁盘缓存");
		for(Entry<File, Long> file : lastUsageDates.entrySet()){
			if(file.getKey().getName().equals(filename)){
				Log.E("磁盘命中");
				return file.getKey();
			}
		}
		Log.E("磁盘未命中");
		return null;
	}
	
	
	
	

	private int removeNext() {
		// 获取最早加入的缓存文件并将之删除

		if (lastUsageDates.isEmpty()) {
			return 0;
		}

		Long oldestUsage = null;
		File mostLongUsedFile = null;

		Set<Entry<File, Long>> entries = lastUsageDates.entrySet();

		for (Entry<File, Long> entry : entries) {
			if (mostLongUsedFile == null) {
				mostLongUsedFile = entry.getKey();
				oldestUsage = entry.getValue();
			} else {
				Long lastValueUsage = entry.getValue();
				if (lastValueUsage < oldestUsage) {
					oldestUsage = lastValueUsage;
					mostLongUsedFile = entry.getKey();
				}
			}
		}
		
		int fileSize = 0;
		if(mostLongUsedFile != null){
			if(mostLongUsedFile.exists()){
				fileSize = (int) mostLongUsedFile.getTotalSpace();
				if(mostLongUsedFile.delete()){
					lastUsageDates.remove(mostLongUsedFile);
				}
			}else{
				lastUsageDates.remove(mostLongUsedFile);  
			}
		}
		return fileSize;
	}
	
	
	//删除对应文件然后清空map
	public void clear(){
		for(Entry<File, Long> entry : lastUsageDates.entrySet()){
			entry.getKey().delete();
		}
		lastUsageDates.clear();
		cacheSize.set(0);
	}
	
}
