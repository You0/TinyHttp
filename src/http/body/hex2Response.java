package http.body;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Me on 2017/5/19.
 */

public class hex2Response {
	private Response response;
	private InputStream ins;
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
			while((len=ins.read(bs))!=-1){
				for(int i=0;i<len;i++){
					System.out.print((char)bs[i]);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		
	}
	
	
}
