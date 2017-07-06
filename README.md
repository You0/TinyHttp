# TinyHttp
一个小型的http库。作者慢慢完善中。。。

一个Http Client，在底层复用socket，手动拼接HTTP请求，支持多线程和缓存。
因为项目中用了OkHttp这个库，自己稍微了解了原理之后，想自己练练手，所以开始写这个东东。。



使用例子：

			//post请求，文件上传
			Request request = new Request.Builder().post()
					.url("http://115.159.159.65:8080/videoshare-sso/rest/info/userhead")
					.addFile("img", new File("D:\\img25.jpg"))
					.addParam("Token", "32ef20106c4cad6cc903fe9487b371cf")
					.addParam("username", "E41414005")
					.build();
			AnsyCall call = new AnsyCall(request);
			
			call.setListener(new Listener(){
				@Override
				public void Upload(int i) {
					//这个接口提供上传时的进度(0-100 百分比进度)，方便开发者调用,当然不设置也可以
				}
				
				
			});
			
			//加入队列
			call.enqueue(callBack);
			

			//普通的get
			Request request2 = new Request.Builder()
					.get()
					.url("http://avatar.csdn.net/0/B/B/1_u010015108.jpg")
					.build();
	
			AnsyCall call0 = new AnsyCall(request2);
			call0.enqueue(callBack);
			
			
			//不放入队列的话则是同步的
			//call.start();