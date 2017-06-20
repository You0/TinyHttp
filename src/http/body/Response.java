package http.body;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Me on 2017/5/19.
 */

public class Response {
    private HashMap<String,String> headers;
    private byte[] data;
    //返回标示200 403 302这种
	private String code;
	
	
	public byte[] body(){
		return data;
	}
	
	public String getHeader(String key) throws Exception{
		if(headers==null){
			throw new Exception();
		}
		return headers.get(key);
	}
	
	public HashMap<String, String> getHeaders() throws Exception{
		if(headers==null){
			throw new Exception();
		}
		return headers;
	}
	
	
	public String string(String character) throws UnsupportedEncodingException
	{
		return new String(data, character);
	}

	
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	
	public byte[] getData() {
		return data;
	}
	
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	public String getCode() {
		return code;
	}
	
	
	public void setCode(String code) {
		this.code = code;
	}
}
