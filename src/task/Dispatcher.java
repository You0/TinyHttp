package task;

import java.util.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import Over.OverControl;
import http.Connection;
import http.body.Request;
import http.body.Response;
import socket.SocketPool;
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
    private volatile AtomicInteger runningTasks;
    //��ʼ��socket����أ���������������е�socket
    private SocketPool socketPool = new SocketPool(5*60*1000,5);


    //��������http���ݷ��͵����ӳ�
    private ExecutorService executorService = new ThreadPoolExecutor(
            0,Integer.MAX_VALUE,60, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),
            Utils.threadFactor("OkHttp Dispatcher",false));

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
            Dequeue.add(task);
        }
    }

    //����������е����ݷ��뵽�̳߳���ȥ
    public void finish(){
        runningTasks.addAndGet(-1);
        AnsyCall call = Dequeue.getFirst();
        Dequeue.removeFirst();
        executorService.submit(call);
        runningTasks.addAndGet(1);
    }


    //�첽����
    class AnsyCall implements Runnable{
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

                //code....



            }catch (Exception e){
                //ignore
            }finally {
                //һ��Ҫ����finish������ʼ����һ��������뵽�̳߳��С�
                Dispatcher.getInstance().finish();
                OverControl.release(connection);
            }

        }
    }

    //�첽�������֮�󽫵��õĻص�����
    public interface CallBack{
        void Error(Response response);
        void Success(Response response);
    }


}
