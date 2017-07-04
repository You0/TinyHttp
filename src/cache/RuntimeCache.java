package cache;

import java.util.HashMap;
import java.util.LinkedList;

import com.sun.org.apache.bcel.internal.generic.NEW;

import http.body.Response;

//ʹ��LRU�㷨���ڴ�����е�һ������
/**
 * ֱ�ӻ���Response���������
 * 
 * */
public class RuntimeCache {
	//����ļ�����������LRU��̭
	private LinkedList<String> key = new LinkedList<>();
	//���������
	private HashMap<String,Response> cacheMap = new HashMap<>();
	//�ڴ��л���������������������cache��������
	private long cacheByteLen = 16;
	//Ĭ�ϵĻ����С��5MB
	private long ByteSize = 5*1024*1024;
	//��ǰ����Ĵ�С
	private long currentSize = 0;
	//�ڴ滺��Ӧ���ǵ�������
	private static RuntimeCache instance = new RuntimeCache();
	
	private RuntimeCache(){
		
	}
	
	public static RuntimeCache getInstance(){
		return instance;
	}
	

	/**
	 * ����key���ػ���,������һ��key��λ�ã���������ڷ���NULL
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
	 * ���û��棬�������������ɾ�����һ��Ȼ�󽫴˶�����뵽�����ײ�
	 * */
	public boolean set(String hash,Response response){
		//ʣ��������������²Ž��л������������ܾ��˲���
		if(ByteSize - currentSize > response.body().length){
			currentSize += response.body().length;
		}else{
			return false;
		}

		//��������ͻ��ڻ������������¸ö���
		if(key.contains(hash)){
			key.remove(hash);
			key.addFirst(hash);
		}else{
			if(key.size()<cacheByteLen){
				key.add(hash);
			}else{
				//ĩλ��̭
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
	 * �����ڴ��еĻ���
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
	 * 			����ĸ���
	 * 		  ByteSize 
	 *  		����ռ���ڴ�Ĵ�С
	 * */
	public void setCacheParam(long cacheByteLen,long ByteSize) {
		this.cacheByteLen = cacheByteLen;
		this.ByteSize = ByteSize;
	}

}
