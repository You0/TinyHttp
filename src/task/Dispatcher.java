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

//用于任务分发管理的类，用户提交异步任务之后其连接任务将交由这个类管理分派
//只需要有一个任务分发的类，所以设置为单例模式即可。
public class Dispatcher {
    //最大请求数目,默认为64
    private int maxRequest = 5;

    private LinkedList<AnsyCall> Dequeue = new LinkedList<>();
    //当前正在运行的task
    private volatile AtomicInteger runningTasks = new AtomicInteger(0);
    //初始化socket管理池，其任务是清除空闲的socket
    public SocketPool socketPool = new SocketPool(5*1000,5);


    //真正进行http数据发送的连接池
    private ExecutorService executorService = new ThreadPoolExecutor(
            0,Integer.MAX_VALUE,60, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),
            Utils.threadFactor("TinyHttp Dispatcher",false));

    private static Dispatcher single = new Dispatcher();




    private Dispatcher(){

    }
    //懒汉单例模式
    public static Dispatcher getInstance(){
        return single;
    }

    //如果当前正在请求的数目小于maxRequest则直接放入
    public  void dispatch(AnsyCall task){
        if(runningTasks.get() < maxRequest){
            runningTasks.addAndGet(1);
            executorService.submit(task);
        }else{
        	Log.E(runningTasks.get());
        	Log.E("并发池已满，放入缓冲队列中");
            Dequeue.add(task);
        }
    }

    //将任务队列中的内容放入到线程池中去
    public void finish(){
        runningTasks.addAndGet(-1);
        if(!Dequeue.isEmpty()){
        	AnsyCall call = Dequeue.getFirst();
            Dequeue.removeFirst();
            executorService.submit(call);
            runningTasks.addAndGet(1);
        }
    }


    //异步任务完成之后将调用的回调任务。
    public interface CallBack{
        void Error(Response response);
        void Success(Response response);
    }


}
