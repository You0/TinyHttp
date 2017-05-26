package socket;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import http.Connection;
import util.Utils;




/**
 * Created by Me on 2017/5/14.
 *��������socket���������ڣ���ʱ��������Ҫ��ʹ�õ�
 * socket�ͻ��沿�ֱȽϻ�Ծ��socket��
 * */

public class SocketPool {

    //��������socket���ӵ�����أ� ������������һ����˵�Ǹ��������̣߳����ǵ���չ��ʹ���̳߳�
    //��Ϊ�̳߳صĿ������񲢲���
    private static final Executor mExecutor = new ThreadPoolExecutor(1,Integer.MAX_VALUE,
            60, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),
            Utils.threadFactor("socket Pool",false));

    //����ǰsocket��Ŀ���ʱ��
    private long maxIdletime;
    //������е�������Ŀ
    private int maxIdleConnections;
    private boolean cleanisRuing = false;
    //���ڱ���connection���ӵ�����
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
                    //ʹ�������������ֻ��һ����������ʱ��פ�����̳߳أ�
                    //���ǻ��ǼӸ������淶һ�¡�
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

    //���û�ָ��socket���ӳص�������ʱ�����������Ŀ
    public SocketPool(int maxIdletime,int maxIdleConnections){
        this.maxIdletime = maxIdletime;
        this.maxIdleConnections = maxIdleConnections;
    }
    
    
    public void addConnection(Connection connection){
    	//ÿ�μ����µ�socket������ִ����������(���ж��Ƿ��Ѿ�ִ���ˣ��������ִ�оͲ�ִ��)
    	mLinkedList.add(connection);
    	if(!cleanisRuing){
    		mExecutor.execute(cleanTask);
    	}
    }
    
    
    
    

    //�Ȳ�ѯһ���Ƿ����Ѿ����ڵ�socket���и��á�
    public Connection getConnection(String addr,int port)
    {
        //���Ѿ����ڵ�URL���б����ҳ��Ƿ��Ѿ�����
        for(int i=0;i<mLinkedList.size();i++){
            if(mLinkedList.get(i).getAddr().toString().equals(addr)
                    &&mLinkedList.get(i).getPort()==port){
                //��connection���ظ��á�
            	if(mLinkedList.get(i).isUsing==false){
            		mLinkedList.get(i).isUsing = true;
            	}else{
            		return null;
            	}
                return mLinkedList.get(i);
            }
        }
        return null;
    }



    /*
        ִ����������ĺ�����
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
            //�Ӷ�����ɾ���������ͷ�������Դ������ָ��null��ֹ����ı����۸������
            mLinkedList.remove(longestIdleConnection);
            longestIdleConnection.release();
            longestIdleConnection = null;
        }else{
            //û������ʹ�úͿ��е�connection����Ҫ��������
            if(idelConnectionSize==0&&inUseConnection==0){
                return  -1;
            }else if(idelConnectionSize > 0){
                //�����ǰ����Ҫ������ȴ��Ǹ���Ŀ�����涨����ʱ��Ĳ�ֵʱ�䣬
                //������һ�ν�������ʱ�ܵõ�����
                return maxIdletime - maxIdleTime;
            }else{
                return maxIdletime;
            }
        }
        return -1;
    }


}

