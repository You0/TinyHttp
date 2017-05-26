package task;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import Over.OverControl;
import http.Connection;
import http.body.Request;
import http.body.Request2hex;
import http.body.Response;
import task.Dispatcher.CallBack;

public class AnsyCall implements Runnable{
    private CallBack callBack;
    private Request request;
    private Connection connection;
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
        try{
        	URL url = request.getUrl();
            //code....
        	Connection connection = Dispatcher.getInstance().socketPool
        			.getConnection(url.getHost(), url.getPort());
        	
        	//���û�и��õ�socket���½�һ����
        	if(connection == null){
        		int port = url.getPort() == -1 ? 8080 : url.getPort();
        		SocketAddress address = new InetSocketAddress(url.getHost(), port);
        		System.out.println(url.getHost());
        		Socket socket = new Socket();
        		socket.connect(address, 5000);
        		connection = new Connection(socket,url);
        		OverControl.acquire(connection);
        		Dispatcher.getInstance().socketPool.addConnection(connection);
        	}
        	
        	Request2hex reallyCall = new Request2hex(connection, request);
        	
        	
        	//�����ڽ���reallyCall֮�󣬽�һ���ж��Ƿ���Խ��л��棬����л���Ļ���ֱ�Ӿͷ��ػ��档
        	//����������Ժ���д
        	
        	
        	
        	
        	
        	
        	reallyCall.connect();
        	
        	
        	//���new�����Ŀ�response�����á�
        	callBack.Success(new Response());


        }catch (Exception e){
            //�����׳����쳣��socket����ʱ��ɵ��쳣��
        	//�ص�callback��error��������
        	//callBack.Error(new hex2Response().);

        }finally {
            //һ��Ҫ����finish������ʼ����һ��������뵽�̳߳��С�
            Dispatcher.getInstance().finish();
            OverControl.release(connection);
        }

    }
}
