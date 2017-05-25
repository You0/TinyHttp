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
//��Request�������������͸���������
//�Լ�ʹ��raw�ı�ƴ��httpЭ��
public class Request2hex {
    private Connection connection;
    private Request request;
    //socket�������
    private OutputStream outputStream;
    //socket��������
    private InputStream inputStream;
    private String bound = null;

    public Request2hex(Connection connection,Request request){
        this.connection = connection;
        this.request = request;
    }

    //��ʼ����
    public void connect() throws Exception {
        //�õ������,��ʼ��socket����д��
        try {
            outputStream = connection.getSocket().getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //��ʼ���ӡ�
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
            throw new Exception("socketδ��");
        }
        //ֱ�ӱ���Request��ͷ����
        request.getHeaders()



//        URL url = connection.getUrl();
//        //������get����post�����������get�����Ļ���
//        //��ǳ���ֱ�ӡ�
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
