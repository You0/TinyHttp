package test;

import java.io.UnsupportedEncodingException;

import http.body.Request;
import http.body.Response;
import task.AnsyCall;
import task.Dispatcher.CallBack;

public class Test {
	public static void main(String[] args) {
		CallBack callBack = new CallBack() {
			
			@Override
			public void Success(Response response) {
				// TODO Auto-generated method stub
				try {
					//System.out.println(response.string("utf-8"));
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("访问成功");
			}
			
			@Override
			public void Error(Response response) {
				// TODO Auto-generated method stub
				System.out.println("访问失败");
			}
		};
		
		try {
			
			//首先新建一个request对象，用连缀的方式增加参数
			Request request = new Request.Builder()
					.get()
					.url("http://www.cnblogs.com/kaiwen/p/6542168.html")
					.build();
			
		
			AnsyCall call = new AnsyCall(request);
			
			

			
			
			//异步任务
			for(int i=0;i<100;i++){
				call.enqueue(callBack);
				Thread.sleep(2000);
			}
			
			
			
			//不放入队列的话则是同步的
			//call.start();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
