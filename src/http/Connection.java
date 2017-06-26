package http;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import http.body.Request2hex;

/**
 * Created by Me on 2017/5/14.
 */

public class Connection {
    private Socket socket;
    private Request2hex reallyCall;
    public boolean isUsing = false;
    private URL url;



    long IdleTime;
    //进入空闲状态时的时间
    
    public Connection(Socket socket,URL url){
    	this.socket = socket;
    	this.url = url;
    }
    
    
    public Request2hex getReallyCall() {
		return reallyCall;
	}

    
    public void setReallyCall(Request2hex reallyCall) {
		this.reallyCall = reallyCall;
	}
    

    public Socket getSocket() {
        return socket;
    }

    public long getIdleTime() {
        return IdleTime;
    }

    public void setIdelTime() {
        IdleTime = System.currentTimeMillis();
    }
    
    public void setIdelTime(long usr) {
        IdleTime = usr;
    }

    public boolean isUsing(){
        return isUsing;
    }

    //对连接进行释放,关闭socket连接。
    public boolean release(){
        try {
        	if(reallyCall.getInputStream()!=null){
        		reallyCall.getInputStream().close();
        	}
        	if(reallyCall.getOutputStream()!=null){
        		reallyCall.getInputStream().close();
        	}
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAddr(){
        return url.getHost();
    }

    public int getPort(){
    	return url.getPort() == -1 ? 80 : url.getPort();
    }

    public URL getUrl(){
        return url;
    }
    
    
    
    
    @Override
    public int hashCode() {
    	// TODO Auto-generated method stub
    	return super.hashCode();
    }

    


}
