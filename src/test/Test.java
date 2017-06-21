package test;

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
					System.out.println(response.string("utf-8"));
					
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
					.url("http://115.159.159.65:8080/")
					.build();
			
			Request request1 = new Request.Builder()
					.get()
					.url("http://www.cnblogs.com/ljhdo/p/5068072.html")
					.build();
			
		
			AnsyCall call0 = new AnsyCall(request);
			call0.enqueue(callBack);
//			AnsyCall call1 = new AnsyCall(request1);
//			AnsyCall call1 = new AnsyCall(request);
//			AnsyCall call2 = new AnsyCall(request);
//			AnsyCall call3 = new AnsyCall(request);
//			AnsyCall call4 = new AnsyCall(request);
//			AnsyCall call5 = new AnsyCall(request);
//			AnsyCall call6 = new AnsyCall(request);
//			AnsyCall call7 = new AnsyCall(request);
//			AnsyCall call8 = new AnsyCall(request);
//			AnsyCall call9 = new AnsyCall(request);
//			//异步任务

			
			//call1.enqueue(callBack);
			
			for(int i=0;i<100;i++){
				call0.enqueue(callBack);
				
			}
			
			
			
			
			
			//不放入队列的话则是同步的
			//call.start();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
