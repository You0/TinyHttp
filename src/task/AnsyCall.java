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
    
    public AnsyCall(Request request){
        this.request = request;
    }


    //�첽���񣬰��Լ����������ִ��
    public void enqueue(CallBack callBack){
        this.callBack = callBack;
        Dispatcher.getInstance().dispatch(this);
    }

    //ͬ����ʼ
    public void start(){
        this.start();
    }


    @Override
    public void run() {
        //�õ�socket������requet�����õ�responseȻ�����response����
        //����response�������callback��success��������error������(���ж�callback�Ƿ���null)
    	Connection connection = null;
        try{
        	URL url = request.getUrl();
        	int port = url.getPort() == -1 ? 80 : url.getPort();
            //code....
        	connection = Dispatcher.getInstance().socketPool
        			.getConnection(url.getHost(), port);
        	
        	//���û�и��õ�socket���½�һ����
        	if(connection == null){
        		InetAddress nInetAddress = InetAddress.getByName(url.getHost());
        		System.out.println(nInetAddress.getHostAddress());
        		Socket socket = new Socket(nInetAddress,port);
        		
        		connection = new Connection(socket,url);
        		connection.isUsing = true;
        		Dispatcher.getInstance().socketPool.addConnection(connection);
        		
        	}
        	
        	Request2hex reallyCall = new Request2hex(connection, request);
        	
        	connection.setReallyCall(reallyCall);
        	//�����ڽ���reallyCall֮�󣬽�һ���ж��Ƿ���Խ��л��棬����л���Ļ���ֱ�Ӿͷ��ػ��档
        	//����������Ժ���д
        	
        	
        	
        	
        	
        	reallyCall.connect();
        	
        	
        	//�õ������������������ת��Ϊresponse����
        	InputStream ins = reallyCall.getInputStream();
        	Response response = new hex2Response(ins).getResponse();
        		
        	callBack.Success(response);


        }catch (Exception e){
            //�����׳����쳣��socket����ʱ��ɵ��쳣��
        	//�ص�callback��error��������
        	//callBack.Error(new hex2Response().getResponse());

        }finally {
            //һ��Ҫ����finish������ʼ����һ��������뵽�̳߳��С�
        	connection.setIdelTime();
            OverControl.release(connection);
            Log.E("finally");
            Log.E("��ǰconnectionʹ����ϣ����ͷ�"+connection.isUsing());
            Dispatcher.getInstance().finish();
            
        }

    }
}
