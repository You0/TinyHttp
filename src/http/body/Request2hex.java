package http.body;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import http.Connection;
import util.Log;

/**
 * Created by Me on 2017/5/19.
 */
//将Request对象变成流对象发送给服务器。
//自己使用raw文本拼接http协议
//目前还不支持https协议，等之后支持之后将放在其他的类里面
public class Request2hex {
    private Connection connection;
    private Request request;
    //socket的输出流
    private OutputStream outputStream;
    //socket的输入流
    private InputStream inputStream;
    private Listener listener;
    private String boundary="OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp";
    private String endLine = "--" + boundary + "--\r\n";
    //post发送参数的时候每个参数都有自己的参数头
    private StringBuilder[] FilePlains;
    //这个文本直接自己拼接了,所以直接使用一个StringBuilder就行了。
    //private StringBuilder[] textPlains;
    private StringBuilder textPlains;
	private long contentLength;

    public Request2hex(Connection connection,Request request){
        this.connection = connection;
        this.request = request;
    }
    
    
    public void setListener(Listener listener) {
		this.listener = listener;
	}
    

    //开始连接
    public void connect() throws Exception {
        //拿到输出流,开始对socket进行写入
        try {
            outputStream = connection.getSocket().getOutputStream();
            inputStream = connection.getSocket().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始链接。
        httpHeader(request.getMethod());
        if(request.getMethod().equals("post")||request.getMethod().equals("POST")){
        	postBody();
        }
        
        outputStream.flush();
    }
    
    
    public OutputStream getOutputStream() {
		return outputStream;
	}
    
    public InputStream getInputStream() {
		return inputStream;
	}




    /*
    * GET /PCcontrolServer/ImageGet?pwd=5678 HTTP/1.1
    Host: 115.159.159.65:8080
    Connection: keep-alive
    Cache-Control: max-age=0
    Upgrade-Insecure-Requests: 1
    User-Agent:
    Accept:
    Accept-Encoding: gzip, deflate, sdch
    Accept-Language: zh-CN,zh;q=0.8
 */
    private void httpHeader(String method) throws Exception {
        if(outputStream==null){
            throw new Exception("socket未打开");
        }
        //直接遍历Request的头就行
        HashMap<String, String> headers = request.getHeaders();
        
        //httpHeader用来发送的http头部。
        StringBuilder httpHeader = new StringBuilder();
        
        
        //GET /PCcontrolServer/ImageGet?pwd=5678 HTTP/1.1
        //首先是对方法的判断,先只支持get和post方法
        URL url = connection.getUrl();
        if(request.getMethod().equals("get")
        		||request.getMethod().equals("GET")){
        	
        	if(url.getQuery()!=null){
        		httpHeader.append("GET " + url.getPath()+"?"
            			+url.getQuery() + " HTTP/1.1\r\n");
        	}else{
        		httpHeader.append("GET " + url.getPath()+" HTTP/1.1\r\n");
        	}

        }else if(request.getMethod().equals("POST")||
        		request.getMethod().equals("post")){
        	httpHeader.append("POST " + url.getPath() + " HTTP/1.1\r\n");
        
        	contentLength = CalcDataLength();
        	
        	//如果是post则默认使用这个content-Type发送
        	httpHeader.append("Content-Type: multipart/form-data; boundary="
        			+boundary+"\r\n");
        	
        	httpHeader.append("Content-Length: " + contentLength + "\r\n");
        
        }
        
        //其次对其他header头参数进行拼接。其中contentlength这个只有post方法才支持。要独立判断
        Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
        
        //Cache-Control: max-age=0
        while(iterator.hasNext()){
        	Entry<String, String> current = iterator.next();
        	httpHeader.append(current.getKey()+
        			": " + current.getValue() + "\r\n");
        }
        
        Log.E(httpHeader.toString());
        //这个/r/n表示消息头结束，否则服务器会一直阻塞在那里不会将结果返回。
        httpHeader.append("\r\n");
        //将Http头写入socket，发起http请求
        outputStream.write(httpHeader.toString().getBytes());
        

//        URL url = connection.getUrl();
//        //决定是get还是post方法，如果是get方法的话，
//        //则非常简单直接。
//        String requestMethod = "";
//        if(method.equals("get")){
//            requestMethod = "GET "+ url.getPath() +"?"+ url.getQuery()+" HTTP/1.1\r\n";
//        }else{
//            requestMethod = "POST " + url.getPath() +" HTTP/1.1\r\n";
//        }
//
//        outputStream.write(requestMethod.getBytes());
//        String language = "Accept-Language: zh-CN\r\n";


    }
    
    //这个方法把数据发送出去。
    public void postBody() throws IOException
    {
    	//如果socket输出流没开就报个异常
    	if(outputStream==null){
    		throw new IOException();
    	}
    	
    	
    	
    	//发送用异常包出来
    	try{
    		int size = request.getFiles().keySet().size();
    		
    		//先把文本字段写出去
    		outputStream.write(textPlains.toString().getBytes());
    		
    		if(request.getMethod().equals("get")){
        		return;
        	}
    		
    		UploadFiles();
    		
    		
    		outputStream.write(endLine.getBytes());
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    
    
    

	private void UploadFiles() throws IOException {
    	//这里在上传的时候时刻计算上传的百分比,虽然有点不精确但是一般文件的长度要大很多，所以影响不大
		
		Iterator<Entry<String, File>> iterator =  request.getFiles().entrySet().iterator();
		int i=0;
		int currentLen = 0;
		while(iterator.hasNext()){
			File file = iterator.next().getValue();
			
			outputStream.write(FilePlains[i].toString().getBytes());
			FileInputStream is = new FileInputStream(file);
			byte[] bys = new byte[512];
			int len;
			while((len=is.read(bys)) != -1){
				outputStream.write(bys, 0, len);
				currentLen += len;
				//回调不为0就回调
				if(listener != null){
					int progress = (int) (currentLen*1.0 / contentLength *100);
					listener.Upload(progress);
				}
			}
			//上传完一个文件记得后面补上\r\n
			outputStream.write("\r\n".getBytes());
		}
		
	}


	private long CalcDataLength() {

		HashMap<String, File> files = request.getFiles();
		HashMap<String, String> textParams = request.getTextparams();
		
		long DataLengh = 0;
		int fs = files.keySet().size();
		
		if(FilePlains == null){
			if(fs>0){
				FilePlains = new StringBuilder[fs];
			}
		}
		
		if(textPlains == null){
			textPlains = new StringBuilder();
		}
		
		for(Entry<String,String> text : textParams.entrySet()){
			textPlains.append("--" + boundary + "\r\n");
			textPlains.append("Content-Disposition: form-data;name=" + "\"" +text.getKey() +"\""
					+"\r\n\r\n");
			
			textPlains.append(text.getValue() + "\r\n");
		}
		//文本类型的长度
		DataLengh += textPlains.toString().getBytes().length;
		int i = 0;
		//遍历上传的参数去获取计算长度长度
		for(Entry<String,File> file : files.entrySet()){
			StringBuilder current = new StringBuilder();
			
			current.append("--" + boundary + "\r\n");
			current.append("Content-Disposition: form-data; name="
					+"\"" + file.getKey() + "\"; filename=" 
					+ "\"" + file.getValue().getName() + "\"" + "\r\n\r\n");
			
			DataLengh += current.toString().getBytes().length;
			FilePlains[i++] = current;
			//不要忘记最后还有2个字节的\r\n
			DataLengh = DataLengh + file.getValue().length() + 2;
		}
		
		return DataLengh + endLine.getBytes().length;
	}
	
	


	public interface Listener
	{
		public void Upload(int i);
	}



}
