package http.body;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Me on 2017/5/19.
 */

public class hex2Response {
	private Response response;
	private InputStream ins;
	private String code = "UTF-8";
	
	
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
		byte[] bs= new byte[512];
		int len;
		
		try{
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins, code));
			String line;
			while((line = reader.readLine())!=null){
				System.out.println(line);
				System.out.println(line.getBytes());
			}
			
//			while((len=ins.read(bs))!=-1){
//				for(int i=0;i<len;i++){
//					System.out.println(bs[i]);
//				}
//			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
}
