package Over;


import http.Connection;

/**
 * Created by Me on 2017/5/14.
 */

public class OverControl {
    //每当使用socket时要先去获取请求一下。
    public static void acquire(Connection connection)
    {
    	connection.isUsing = true;
    }

    //每当使用完毕的时候就放弃请求。
    public static void release(Connection connection)
    {
    	connection.isUsing = false;

    }


}
