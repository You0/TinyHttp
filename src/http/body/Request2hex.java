package http.body;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import http.Connection;

/**
 * Created by Me on 2017/5/19.
 */
//将Request对象变成流对象发送给服务器。
//自己使用raw文本拼接http协议
public class Request2hex {
    private Connection connection;
    private Request request;
    //socket的输出流
    private OutputStream outputStream;
    //socket的输入流
    private InputStream inputStream;
    private String bound = null;

    public Request2hex(Connection connection,Request request){
        this.connection = connection;
        this.request = request;
    }

    //开始连接
    public void connect() throws Exception {
        //拿到输出流,开始对socket进行写入
        try {
            outputStream = connection.getSocket().getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始链接。
        httpHeader(request.getMethod());


    }




    /*
    * GET /PCcontrolServer/ImageGet?pwd=5678 HTTP/1.1
    Host: 115.159.159.65:8080
    Connection: keep-alive
    Cache-Control: max-age=0
    Upgrade-Insecure-Requests: 1
    User-Agent:
    Accept:
    Accept-Encoding: gzip, deflate, sdch
    Accept-Language: zh-CN,zh;q=0.8
 */
    private void httpHeader(String method) throws Exception {
        if(outputStream==null){
            throw new Exception("socket未打开");
        }
        //直接遍历Request的头就行
        request.getHeaders()



//        URL url = connection.getUrl();
//        //决定是get还是post方法，如果是get方法的话，
//        //则非常简单直接。
//        String requestMethod = "";
//        if(method.equals("get")){
//            requestMethod = "GET "+ url.getPath() +"?"+ url.getQuery()+" HTTP/1.1\r\n";
//        }else{
//            requestMethod = "POST " + url.getPath() +" HTTP/1.1\r\n";
//        }
//
//        outputStream.write(requestMethod.getBytes());
//        String language = "Accept-Language: zh-CN\r\n";




    }






}
