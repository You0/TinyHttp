package http.body;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import util.Utils;

/**
 * Created by Me on 2017/5/19.
 */

public class hex2Response {
	private Response response;
	private InputStream ins;
	private String code = "UTF-8";
	private StringBuilder body;
	private StringBuilder header;
	
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
			byte[] bytes = Utils.toByteArray(ins);
//			
//			
//			//�ָ�header��body
//			departHeadAndBody(bytes);
			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(ins, code));
//			String line;
//			while((line = reader.readLine())!=null){
//				System.out.println(line);
//				//System.out.println(line.getBytes());
//			}
			
			//分离http中header和body
			
			
			
//			while((len=ins.read(bs))!=-1){
//				for(int i=0;i<len;i++){
//					System.out.println(bs[i]);
//				}
//			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		
	}

	
	//����body��http header֮����2��/r/n����
	private void departHeadAndBody(byte[] bytes) {
		// TODO Auto-generated method stub
		for(byte b:bytes){
			System.out.println(b);
		}
		
		
		
	}



	
	
}
