package task;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import Over.OverControl;
import http.Connection;
import http.body.Request;
import http.body.Request2hex;
import http.body.Response;
import http.body.hex2Response;
import task.Dispatcher.CallBack;
import util.Log;

public class AnsyCall implements Runnable{
    private CallBack callBack;
    private Request request;
    private Connection connection;
    public AnsyCall(Request request){
        this.request = request;
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
        		OverControl.acquire(connection);
        		Dispatcher.getInstance().socketPool.addConnection(connection);
        		
        	}
        	
        	Request2hex reallyCall = new Request2hex(connection, request);
        	
        	connection.setReallyCall(reallyCall);
        	//这里在建立reallyCall之后，进一步判断是否可以进行缓存，如果有缓存的话，直接就返回缓存。
        	//缓存的内容以后再写
        	
        	
        	
        	
        	
        	reallyCall.connect();
        	
        	
        	//拿到输入流将这个输入流转换为response对象
        	InputStream ins = reallyCall.getInputStream();
        	Response response = new hex2Response(ins).getResponse();
        		
        	callBack.Success(response);


        }catch (Exception e){
            //这里抛出的异常是socket连接时造成的异常。
        	//回调callback的error方法。的
        	//callBack.Error(new hex2Response().getResponse());

        }finally {
            //一定要调用finish方法开始把下一个任务加入到线程池中。
        	connection.setIdelTime();
            OverControl.release(connection);
            Log.E("finally");
            Log.E("当前connection使用完毕，待释放"+connection.isUsing());
            Dispatcher.getInstance().finish();
            
        }

    }
}
