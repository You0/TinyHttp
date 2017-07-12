# TinyHttp
# 简介
一个小型的http库。作者慢慢完善中。。。

一个Http Client，在底层复用socket，手动拼接HTTP请求，支持多线程和缓存。



# 使用例子：
```javascript
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
```