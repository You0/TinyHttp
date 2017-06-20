package http.body;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import util.Utils;

/**
 * Created by Me on 2017/5/19.
 */

public class hex2Response {
	private Response response;
	private InputStream ins;
	private String code = "UTF-8";
	private byte[] body;
	private byte[] header;
	private long headlen;
	private long bodylen;
	
	
	public void setHeadlen(long headlen) {
		this.headlen = headlen;
	}
	
	public void setBodylen(long bodylen) {
		this.bodylen = bodylen;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	
	public hex2Response(InputStream ins){
		this.ins = ins;
		hex2Str();
	}
	
	
	public Response getResponse(){
		return response;
	}
	
	public void hex2Str()
	{
		try{
			byte[] bytes = Utils.toByteArray(ins,this);
			departHeadAndBody(bytes);
			
		}catch(IOException e){
			e.printStackTrace();
		}

	}
	
	
	public byte[] body()
	{
		return body;
	}
	
	public HashMap<String, String> header()
	{
		HashMap<String, String> map = new HashMap<>();
		
		
		
		return map;
	}

	
	//利用body和http header之间有2个/r/n进行,但这个判断逻辑在转化为字节流的时候已经判断过了
	private void departHeadAndBody(byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("divive");
		body = new byte[(int) bodylen];
		int j=0;
		for(long i=headlen;i<headlen+bodylen;i++){
			body[j++] = bytes[(int) i];
		}
		
		header = new byte[(int)headlen];
		j=0;
		for(long i=0;i<headlen;i++){
			header[j++] = bytes[(int) i];
		}
		
	}
	
}
