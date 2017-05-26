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

import com.sun.swing.internal.plaf.metal.resources.metal_zh_TW;

import http.Connection;

/**
 * Created by Me on 2017/5/19.
 */
//��Request�������������͸���������
//�Լ�ʹ��raw�ı�ƴ��httpЭ��
public class Request2hex {
    private Connection connection;
    private Request request;
    //socket�������
    private OutputStream outputStream;
    //socket��������
    private InputStream inputStream;
    private Listener listener;
    private String boundary="OCqxMF6-JxtxoMDHmoG5W5eY9MGRsTBp";
    
    //post���Ͳ�����ʱ��ÿ�����������Լ��Ĳ���ͷ
    private StringBuilder[] FilePlains;
    //����ı�ֱ���Լ�ƴ����,����ֱ��ʹ��һ��StringBuilder�����ˡ�
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
    

    //��ʼ����
    public void connect() throws Exception {
        //�õ������,��ʼ��socket����д��
        try {
            outputStream = connection.getSocket().getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //��ʼ���ӡ�
        httpHeader(request.getMethod());


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
            throw new Exception("socketδ��");
        }
        //ֱ�ӱ���Request��ͷ����
        HashMap<String, String> headers = request.getHeaders();
        
        //httpHeader�������͵�httpͷ����
        StringBuilder httpHeader = new StringBuilder();
        
        
        //GET /PCcontrolServer/ImageGet?pwd=5678 HTTP/1.1
        //�����ǶԷ������ж�,��ֻ֧��get��post����
        URL url = connection.getUrl();
        if(request.getMethod().equals("get")
        		||request.getMethod().equals("GET")){
        	
        	httpHeader.append("GET " + url.getPath()+"?"
        			+url.getQuery() + " HTTP/1.1\r\n");
        	
        }else if(request.getMethod().equals("POST")||
        		request.getMethod().equals("post")){
        	httpHeader.append("POST " + url.getPath() + "HTTP/1.1\r\n");
        
        	contentLength = CalcDataLength();
        	
        	//�����post��Ĭ��ʹ�����content-Type����
        	httpHeader.append("Content-Type: multipart/form-data; boundary"
        			+boundary);
        	
        	httpHeader.append("Content-Length: " + contentLength + "\r\n");
        
        }
        
        //��ζ�����headerͷ��������ƴ�ӡ�����contentlength���ֻ��post������֧�֡�Ҫ�����ж�
        Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
        
        //Cache-Control: max-age=0
        while(iterator.hasNext()){
        	Entry<String, String> current = iterator.next();
        	httpHeader.append(current.getKey()+
        			": " + current.getValue() + "\r\n");
        }
        
        //��Httpͷд��socket������http����
        outputStream.write(httpHeader.toString().getBytes());
        

//        URL url = connection.getUrl();
//        //������get����post�����������get�����Ļ���
//        //��ǳ���ֱ�ӡ�
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
    
    //������������ݷ��ͳ�ȥ��
    public void postBody() throws IOException
    {
    	//���socket�����û���ͱ����쳣
    	if(outputStream==null){
    		throw new IOException();
    	}
    	
    	//�������쳣������
    	try{
    		int size = request.getFiles().keySet().size();
    		
    		//�Ȱ��ı��ֶ�д��ȥ
    		outputStream.write(textPlains.toString().getBytes());
    		
    		
    		UploadFiles();
    		
    		
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    
    
    

	private void UploadFiles() throws IOException {
    	//�������ϴ���ʱ��ʱ�̼����ϴ��İٷֱ�,��Ȼ�е㲻��ȷ����һ���ļ��ĳ���Ҫ��ܶ࣬����Ӱ�첻��
		
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
				//�ص���Ϊ0�ͻص�
				if(listener != null){
					int progress = (int) (currentLen*1.0 / contentLength *100);
					listener.Upload(progress);
				}
			}
			//�ϴ���һ���ļ��ǵú��油��\r\n
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
		//�ı����͵ĳ���
		DataLengh += textPlains.length();
		int i = 0;
		//�����ϴ��Ĳ���ȥ��ȡ���㳤�ȳ���
		for(Entry<String,File> file : files.entrySet()){
			StringBuilder current = new StringBuilder();
			
			current.append("--" + boundary + "\r\n");
			current.append("Content-Disposition: form-data; name="
					+"\"" + file.getKey() + "\"; filename=" 
					+ "\"" + file.getKey() + "\"" + "\r\n\r\n");
			
			DataLengh += current.length();
			FilePlains[i++] = current;
			//��Ҫ���������2���ֽڵ�\r\n
			DataLengh = DataLengh + file.getValue().length() + 2;
		}
		
		return DataLengh;
	}
	
	


	public interface Listener
	{
		public void Upload(int i);
	}



}
