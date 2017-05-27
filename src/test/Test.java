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
					.url("http://www.rsks.czs.gov.cn:80")
					.build();
		
			AnsyCall call = new AnsyCall(request);
			
			//�첽��ʼ
			call.enqueue(new CallBack() {
				
				@Override
				public void Success(Response response) {
					// TODO Auto-generated method stub
					System.out.println("���ʳɹ�");
				}
				
				@Override
				public void Error(Response response) {
					// TODO Auto-generated method stub
					System.out.println("����ʧ��");
				}
			});
			
			
			//ͬ����ʼ
			//call.start();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
