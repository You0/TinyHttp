package task;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import Over.OverControl;
import cache.CacheManager;
import http.Connection;
import http.body.Request;
import http.body.Request2hex;
import http.body.Request2hex.Listener;
import http.body.Response;
import http.body.hex2Response;
import socket.SocketPool;
import task.Dispatcher.CallBack;
import util.Log;
import util.MD5;

public class AnsyCall implements Runnable{
    private CallBack callBack;
    private Request request;
    private Listener listener;
    private static int count = 0;
    public AnsyCall(Request request){
        this.request = request;
    }

    
    public void setListener(Listener listener) {
		this.listener = listener;
	}
    

    //异步任务，把自己放入队列中执行
    public void enqueue(CallBack callBack){
        this.callBack = callBack;
        Dispatcher.getInstance().dispatch(this);
    }

    //同步开始
    public void start(){
        this.start();
    }


    @Override
    public void run() {
        //拿到socket，发送requet请求，拿到response然后处理成response对象，
        //根据response对象调用callback的success方法或者error方法。(先判断callback是否是null)
    	Connection connection = null;
        try{
        	URL url = request.getUrl();
        	int port = url.getPort() == -1 ? 80 : url.getPort();
            //code....
        	connection = Dispatcher.getInstance().socketPool
        			.getConnection(url.getHost(), port);
        	
        	//如果没有复用的socket就新建一个。
        	if(connection == null){
        		InetAddress nInetAddress = InetAddress.getByName(url.getHost());
        		System.out.println(nInetAddress.getHostAddress());
        		Socket socket = new Socket(nInetAddress,port);
        		
        		connection = new Connection(socket,url);
        		connection.isUsing = true;
        		
        		
        		Dispatcher.getInstance().socketPool.addConnection(connection);
        			
        	}
        	
        	connection.setUrl(url);
        	Request2hex reallyCall = new Request2hex(connection, request);
        	
        	if(listener!=null){
        		reallyCall.setListener(listener);
        	}
        	
        	
        	connection.setReallyCall(reallyCall);
        	
        	//这里在建立reallyCall之后，进一步判断是否可以进行缓存，如果有缓存的话，直接就返回缓存。
        	String hash = MD5.getMD5(connection.getUrl().toString());
        	//Log.E(hash);
        	Response cache = CacheManager.get(hash);
        	if(cache != null){
        		Log.E("命中成功!");
        		callBack.Success(cache);
        		return ;
        	}
        	
        	
        	reallyCall.connect();
        	
        	
        	//拿到输入流将这个输入流转换为response对象
        	InputStream ins = reallyCall.getInputStream();
        	Response response = new hex2Response(ins).getResponse();
        	
        	cache(hash,response);
        	
        	
        	callBack.Success(response);
        	
        	
        }catch (Exception e){
        	e.printStackTrace();
            //这里抛出的异常是socket连接时造成的异常。
        	//回调callback的error方法。的
        	callBack.Error(new Response());
        	System.out.println("错误，");
        	//当出现错误的时候直接去除掉复用的socket，重新建立socket
        	Dispatcher.getInstance().socketPool.removeAll();
        	connection = null;

        }finally {
            //一定要调用finish方法开始把下一个任务加入到线程池中。
        	
        	if(connection!=null){
        		connection.setIdelTime();
                OverControl.release(connection);
        	}
        	Dispatcher.getInstance().finish();
            //Log.E("finally");
            Log.E("当前connection使用完毕，待释放"+connection.isUsing());
            
        }

    }
    /**
     * 判断是否缓存,以后的逻辑都可以在这里写
     * TODO
     * */
    private void cache(String hash,Response response){
    	String content_type;
		try {
			content_type = response.getHeader("Content-Type");
			if(content_type!=null && content_type.equals("image/jpeg")){
	    		CacheManager.set(hash,response);
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    
    
    
    
    
}
