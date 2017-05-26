package test;

import http.body.Request;
import http.body.Response;
import task.AnsyCall;
import task.Dispatcher.CallBack;

public class Test {
	public static void main(String[] args) {
		try {
			Request request = new Request.Builder()
					.get()
					.url("http://115.159.159.65:8080")
					.build();
		
			AnsyCall call = new AnsyCall(request);
			
			//异步开始
			call.enqueue(new CallBack() {
				
				@Override
				public void Success(Response response) {
					// TODO Auto-generated method stub
					System.out.println("访问成功");
				}
				
				@Override
				public void Error(Response response) {
					// TODO Auto-generated method stub
					System.out.println("访问失败");
				}
			});
			
			
			//同步开始
			//call.start();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
