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
					.url("http://210.45.215.236:80")
					.build();
		
			AnsyCall call = new AnsyCall(request);
			
			//ï¿½ì²½ï¿½ï¿½Ê¼
			call.enqueue(new CallBack() {
				
				@Override
				public void Success(Response response) {
					// TODO Auto-generated method stub
					System.out.println("·ÃÎÊ³É¹¦");
				}
				
				@Override
				public void Error(Response response) {
					// TODO Auto-generated method stub
					System.out.println("·ÃÎÊÊ§°Ü");
				}
			});
			
			
			//Í¬ï¿½ï¿½ï¿½ï¿½Ê¼
			//call.start();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
