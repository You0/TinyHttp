package test;
import util.MD5;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/10/21.
 */
public class UploadUtils {
    final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
    final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
    int fileDataLength;
    int dataLength;
    int currentlength;
    String[] names;
    String url;
    String param;
    //这里的2个参数是上传完毕之后，请求的url和参数
    public UploadUtils(String url,String param)
    {
        this.url = url;
        this.param = param;
    }

    public UploadUtils(){

    }


    private UpdateProgress updateProgress;

    public void SetListener(UpdateProgress updateProgress) {
        this.updateProgress = updateProgress;
    }

    public String Update(String url, HashMap<String, String> TextParams, File[] files) {
        StringBuilder[] fileExplains = new StringBuilder[files.length];
        names = new String[files.length];

        //封装文本域对象并获得http头的长度
        StringBuilder textEntity = CoverTexeParamAndGetDataLength(TextParams, files, fileExplains);

        URL Posturl = null;
        OutputStream outStream = null;
        try {
            Posturl = new URL(url);
            int port = Posturl.getPort() == -1 ? 8080 : Posturl.getPort();
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(Posturl.getHost(), port);
            socket.connect(socketAddress, 5000);
            outStream = socket.getOutputStream();


            //下面完成HTTP请求头的发送
            httpHeader( dataLength, Posturl, outStream, port);

            //把所有文本类型的实体数据发送出来
            outStream.write(textEntity.toString().getBytes());

            //把所有文件类型的实体数据发送出来
            UploadFile(files, fileExplains, outStream);


            //下面发送数据结束标志，表示数据已经结束
            outStream.write(endline.getBytes());
            outStream.flush();

           

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private StringBuilder CoverTexeParamAndGetDataLength(HashMap<String, String> TextParams, File[] files, StringBuilder[] fileExplains) {
        for (int i = 0; i < files.length; i++) {
            File uploadFile = files[i];
            fileExplains[i] = new StringBuilder();
            StringBuilder fileExplain = fileExplains[i];
            fileExplain.append("--");
            fileExplain.append(BOUNDARY);
            fileExplain.append("\r\n");
            String temp = MD5.getMD5(uploadFile.getName()+System.currentTimeMillis())+".jpg";
            names[i] = temp;
            fileExplain.append("Content-Disposition: form-data;name=\"img\";filename=\"" + names[i]  + "\"\r\n");
            fileExplain.append("Content-Type: application/octet-stream" + "\r\n\r\n");
            fileDataLength += fileExplain.length();
            fileDataLength += uploadFile.length();
            fileDataLength += 2;
        }
        StringBuilder textEntity = new StringBuilder();
        for (Map.Entry<String, String> entry : TextParams.entrySet()) {//构造文本类型参数的实体数据
            textEntity.append("--");
            textEntity.append(BOUNDARY);
            textEntity.append("\r\n");
            textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            textEntity.append(entry.getValue());
            textEntity.append("\r\n");
        }
        //计算传输给服务器的实体数据总长度
        dataLength = textEntity.toString().getBytes().length + fileDataLength + endline.getBytes().length;
        return textEntity;
    }

    private void UploadFile(File[] files, StringBuilder[] fileExplains, OutputStream outStream) throws IOException {
        for (int i = 0; i < files.length; i++) {
            File uploadFile = files[i];
            outStream.write(fileExplains[i].toString().getBytes());

            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            if (fileInputStream != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    currentlength+=len;
                    updateProgress.update((int)((currentlength/(float)fileDataLength)*100));
                    outStream.write(buffer, 0, len);
                }
                fileInputStream.close();
            }
            outStream.write("\r\n".getBytes());
        }
    }

    private void httpHeader(int dataLength, URL posturl, OutputStream outStream, int port) throws IOException {
        String requestmethod = "POST " + posturl.getPath() + " HTTP/1.1\r\n";
        outStream.write(requestmethod.getBytes());
//        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
//        outStream.write(accept.getBytes());
        String language = "Accept-Language: zh-CN\r\n";
        outStream.write(language.getBytes());
        String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
        outStream.write(contenttype.getBytes());
        String contentlength = "Content-Length: " + dataLength + "\r\n";
        outStream.write(contentlength.getBytes());
        String alive = "Connection: Keep-Alive\r\n";
        outStream.write(alive.getBytes());
        String host = "Host: " + posturl.getHost() + ":" + port + "\r\n";
        outStream.write(host.getBytes());
        //写完HTTP请求头后根据HTTP协议再写一个回车换行
        outStream.write("\r\n".getBytes());
    }


    public interface UpdateProgress {
        void update(int i);
    }


}