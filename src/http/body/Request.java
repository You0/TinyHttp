package http.body;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Me on 2017/5/19.
 */

public class Request {
    private URL url;
    private int port;
    private String method;
    private HashMap<String,String> headers;
    private HashMap<String,File> files;
    private HashMap<String,String> textparams;

    public URL getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public String getMethod() {
        return method;
    }

    public HashMap<String, File> getFiles() {
        return files;
    }

    public HashMap<String, String> getTextparams() {
        return textparams;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public class Builder{
        private String url;
        private int port;
        private String method;
        private HashMap<String,String> headers = new HashMap<>();
        private HashMap<String,File> files = new HashMap<>();
        private HashMap<String,String> textparams = new HashMap<>();
        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder post(){
            this.method = "post";
            return this;
        }

        public Builder get(){
            this.method = "get";
            return this;
        }

        public Builder addheader(String key,String val){
            headers.put(key,val);
            return this;
        }

        public Builder addParam(String key,String val){
            textparams.put(key,val);
            return this;
        }

        public Builder addFile(String key,File val){
            files.put(key,val);
            return this;
        }


        public Request build() throws MalformedURLException {
            Request request = new Request();
            request.url = new URL(url);
            request.headers = headers;
            request.method = method;
            request.port = request.url.getPort();
            request.files = files;
            request.textparams = textparams;
            //添加一些默认参数
            if(headers.get("User-Agent")==null){
                headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36 OPR/45.0.2552.812");
            }
            if(headers.get("Connection")==null){
                headers.put("Connection","keep-alive");
            }

            if(headers.get("Accept-Language")==null){
                headers.put("Accept-Language","zh-CN");
            }

            if(headers.get("Host")==null){
                headers.put("Host",getUrl().getHost());
            }

//            if(method.equals("get")){
//            	
//            }else{
//
//            }




            return request;
        }


        //Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
        //Accept-Encoding:gzip, deflate, sdch, br
        //Accept-Language:zh-CN,zh;q=0.8
        //Connection:keep-alive
        //Cookie:
        //User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36 OPR/45.0.2552.812


    }


}
