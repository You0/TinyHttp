package http.body;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import util.Utils;

/**
 * Created by Me on 2017/5/19.
 */

public class hex2Response {
	private InputStream ins;
	private String code = "UTF-8";
	private byte[] body;
	private byte[] header;
	private ArrayList<String> headerStr;
	private long headlen;
	private long bodylen;
	
	public void setHeaderStr(ArrayList<String> headerStr) {
		this.headerStr = headerStr;
	}
	
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
		return buildResponse();
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


	//利用body和http header之间有2个/r/n进行,但这个判断逻辑在转化为字节流的时候已经判断过了
	private void departHeadAndBody(byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
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
	
	
	public Response buildResponse()
	{
		HashMap<String, String> map = new HashMap<>();
		Response response = new Response();
		//设置body
		response.setData(body);
		//设置data
		response.setCode(headerStr.get(0).substring(9,11));
		//设置response的header
		for(int i=1;i<headerStr.size();i++){
			String[] strs = headerStr.get(i).split(":");
			if(strs.length<=1){
				continue;
			}
			map.put(strs[0], strs[1].substring(1, strs[1].length()));
		}
		response.setHeaders(map);
		return response;
	}
	
	
	
}
