package task;

import java.util.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import http.body.Response;
import socket.SocketPool;
import util.Log;
import util.Utils;


/**
 * Created by Me on 2017/5/19.
 */

//��������ַ�������࣬�û��ύ�첽����֮�����������񽫽��������������
//ֻ��Ҫ��һ������ַ����࣬��������Ϊ����ģʽ���ɡ�
public class Dispatcher {
    //���������Ŀ,Ĭ��Ϊ64
    private int maxRequest = 5;

    private LinkedList<AnsyCall> Dequeue = new LinkedList<>();
    //��ǰ�������е�task
    private volatile AtomicInteger runningTasks = new AtomicInteger(0);
    //��ʼ��socket����أ���������������е�socket
    public SocketPool socketPool = new SocketPool(5*1000,5);


    //��������http���ݷ��͵����ӳ�
    private ExecutorService executorService = new ThreadPoolExecutor(
            0,Integer.MAX_VALUE,60, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),
            Utils.threadFactor("TinyHttp Dispatcher",false));

    private static Dispatcher single = new Dispatcher();




    private Dispatcher(){

    }
    //��������ģʽ
    public static Dispatcher getInstance(){
        return single;
    }

    //�����ǰ�����������ĿС��maxRequest��ֱ�ӷ���
    public  void dispatch(AnsyCall task){
        if(runningTasks.get() < maxRequest){
            runningTasks.addAndGet(1);
            executorService.submit(task);
        }else{
        	Log.E(runningTasks.get());
        	Log.E("���������������뻺�������");
            Dequeue.add(task);
        }
    }

    //����������е����ݷ��뵽�̳߳���ȥ
    public void finish(){
        runningTasks.addAndGet(-1);
        if(!Dequeue.isEmpty()){
        	AnsyCall call = Dequeue.getFirst();
            Dequeue.removeFirst();
            executorService.submit(call);
            runningTasks.addAndGet(1);
        }
    }


    //�첽�������֮�󽫵��õĻص�����
    public interface CallBack{
        void Error(Response response);
        void Success(Response response);
    }


}
