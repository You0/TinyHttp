package socket;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import http.Connection;
import util.Utils;




/**
 * Created by Me on 2017/5/14.
 *用来管理socket的生命周期，及时丢弃不需要再使用的
 * socket和缓存部分比较活跃的socket。
 * */

public class SocketPool {

    //用来管理socket连接的任务池， 运行清理任务，一般来说是个单任务线程，考虑到扩展就使用线程池
    //因为线程池的开销好像并不大。
    private static final Executor mExecutor = new ThreadPoolExecutor(1,Integer.MAX_VALUE,
            60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(Integer.MAX_VALUE),
            Utils.threadFactor("socket Pool",false));

    //允许当前socket最长的空闲时间
    private long maxIdletime;
    //允许空闲的连接数目
    private int maxIdleConnections;
    private boolean cleanisRuing = false;
    //用于保存connection连接的引用
    public static LinkedList<Connection> mLinkedList = new LinkedList<>();


    private Runnable cleanTask = new Runnable() {
        @Override
        public void run() {
            cleanisRuing = true;
            while(true){
                long Millis = cleanup(System.currentTimeMillis());
                if(Millis==-1){
                    cleanisRuing = false;
                    return;
                }else{
                    //使用类锁，设计上只有一个清理任务长时间驻存在线程池，
                    //但是还是加个锁，规范一下。
                    synchronized(SocketPool.class){
                        try {
                            SocketPool.this.wait(Millis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    //由用户指定socket连接池的最大空闲时间和最大空闲数目
    public SocketPool(int maxIdletime,int maxIdleConnections){
        this.maxIdletime = maxIdletime;
        this.maxIdleConnections = maxIdleConnections;
    }

    //先查询一下是否有已经存在的socket进行复用。
    public Connection getConnection(String addr,int port)
    {
        //对已经存在的URL进行遍历找出是否已经存在
        for(int i=0;i<mLinkedList.size();i++){
            if(mLinkedList.get(i).getAddr().toString().equals("addr")
                    &&mLinkedList.get(i).getPort()==port){
                //将connection返回复用。
                return mLinkedList.get(i);
            }
        }
        return null;
    }



    /*
        执行清理任务的函数。
     */
    private long cleanup(long currentTimeMillis) {
        int idelConnectionSize = 0;
        long maxIdleTime = 0;
        Connection longestIdleConnection = null;
        int inUseConnection = 0;

        maxIdleTime = 0;
        for(int i=0;i<mLinkedList.size();i++)
        {
            Connection connection = mLinkedList.get(i);
            if(connection.isIdel()){
                inUseConnection++;
            }else{
                idelConnectionSize++;
                long gap = System.currentTimeMillis() - connection.getIdleTime();
                if(gap > maxIdleTime){
                    maxIdleTime = gap;
                    longestIdleConnection = connection;
                }
            }
        }


        if(idelConnectionSize>maxIdleConnections || maxIdleTime >maxIdletime){
            //从队列里删除，并且释放他的资源，让他指向null防止特殊的变量槽复用情况
            mLinkedList.remove(longestIdleConnection);
            longestIdleConnection.release();
            longestIdleConnection = null;
        }else{
            //没有正在使用和空闲的connection不需要进行清理。
            if(idelConnectionSize==0&&inUseConnection==0){
                return  -1;
            }else if(idelConnectionSize > 0){
                //如果当前不需要清理则等待那个最长的空闲与规定最大的时间的差值时间，
                //以让下一次进行清理时能得到清理
                return maxIdletime - maxIdleTime;
            }else{
                return maxIdletime;
            }
        }
        return -1;
    }


}

