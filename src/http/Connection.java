package http;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

/**
 * Created by Me on 2017/5/14.
 */

public class Connection {
    private Socket socket;
    public boolean isUsing = false;
    private URL url;



    long IdleTime;
    //�������״̬ʱ��ʱ��
    
    public Connection(Socket socket,URL url){
    	this.socket = socket;
    	this.url = url;
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

    public boolean isIdel(){
        return false;
    }

    //�����ӽ����ͷ�,�ر�socket���ӡ�
    public boolean release(){
        try {
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public SocketAddress getAddr(){
        return socket.getRemoteSocketAddress();
    }

    public int getPort(){
        return socket.getPort();
    }

    public URL getUrl(){
        return url;
    }


}
