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
 * ʹ��LRU�㷨���д��̻��� ��Ҫ���û�ָ�������Ŀ¼
 */
public class DiskCache {
	// ��¼�����ļ��Ĵ�С
	private final AtomicInteger cacheSize;
	// �����ļ������ֵ
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

	// ��һ���߳�ȥɨ���Ѿ�������ļ�,����������䵽map��
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
				Log.E("ɨ�����");
			}
		}).start();
	}

	public void put(Response response,String filename) {
		File file = fileUtil.CreateFile(cacheDir, response, filename);
		long valueSize = file.getTotalSpace();
		// ��õ�ǰ������ļ���С
		int currentCacheSize = cacheSize.get();
		Log.E("currentCacheSize" + currentCacheSize);
		// ʣ��Ļ���ռ��Ƿ����㻺���ļ�
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
		Log.E("DiskCache ����ɹ�");
	}
	
	
	public File get(String filename){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		Log.E("�������̻���");
		for(Entry<File, Long> file : lastUsageDates.entrySet()){
			if(file.getKey().getName().equals(filename)){
				Log.E("��������");
				return file.getKey();
			}
		}
		Log.E("����δ����");
		return null;
	}
	
	
	
	

	private int removeNext() {
		// ��ȡ�������Ļ����ļ�����֮ɾ��

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
	
	
	//ɾ����Ӧ�ļ�Ȼ�����map
	public void clear(){
		for(Entry<File, Long> entry : lastUsageDates.entrySet()){
			entry.getKey().delete();
		}
		lastUsageDates.clear();
		cacheSize.set(0);
	}
	
}
