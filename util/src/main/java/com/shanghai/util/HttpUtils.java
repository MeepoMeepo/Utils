package com.ctrip.market.dmp.first.order.util;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http请求辅助类
 * Created by jinlv on 2017/7/25.
 */
@Component
public class HttpUtils{

    private static final String GET_STRING_MESSAGE = "HttpUtils.doGetString";
    private static final String GET_BYTES_MESSAGE = "HttpUtils.doGetBytes";
    private static final String POST_MESSAGE = "HttpUtils.doPost";
    private static final String RESPONSE_CODE = "responseCode";
    private static final int SUCCESS_CODE = 200;
    private static final int TIME_OUT = 500;
    private static ILog logManager = LogManager.getLogger(HttpUtils.class);

    private CloseableHttpClient httpClient;

    /**
     * get请求，返回string
     * @param url 请求url
     * @param headers 请求头
     * @return
     * @throws IOException
     */
    public String doGetString(String proxyHost,Integer proxyPort,
                                     String url, Map<String,String> headers) throws IOException {
        Map<String,String> logMaps = new HashMap<>();
        logMaps.put("url",url);
        CloseableHttpResponse response = executeHttpRequest(httpClient,proxyHost,proxyPort,url,headers);
        if (response == null) {
            logManager.error(GET_STRING_MESSAGE, "request fail!! response is null!", logMaps);
            return null;
        }
        //获取响应对象中的响应码
        StatusLine statusLine = response.getStatusLine();//获取请求对象中的响应行对象
        int responseCode=statusLine.getStatusCode();//从状态行中获取状态码
        logMaps.put(RESPONSE_CODE,String.valueOf(responseCode));
        if (responseCode == SUCCESS_CODE){
            return EntityUtils.toString(response.getEntity());
        } else {
            logManager.error(GET_STRING_MESSAGE, "response error!! code:"+responseCode, logMaps);
            return null;
        }
    }

    /**
     * 执行请求
     * @param httpClient
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    private CloseableHttpResponse executeHttpRequest(CloseableHttpClient httpClient ,String proxyHost,
                                Integer proxyPort,String url, Map<String,String> headers) throws IOException {
        HttpGet httpGet = null;
        if (!StringUtils.isEmpty(proxyHost) && proxyPort != null) {
            httpGet = getProxyHttpGet(proxyHost,proxyPort,url);
        }else {
            httpGet = getHttpGet(url);
        }
        if (!CollectionUtils.isEmpty(headers)){
            for (Map.Entry<String,String> entry: headers.entrySet()) {
                httpGet.addHeader(entry.getKey(),entry.getValue());
            }
        }
        return httpClient.execute(httpGet);
    }

    /**
     * get请求，返回byte数组
     * @param url  请求url
     * @param headers 请求头
     * @return
     * @throws IOException
     */
    public byte[] doGetBytes(String proxyHost,Integer proxyPort,
                                    String url, Map<String,String> headers) throws IOException {
        Map<String,String> logMaps = new HashMap<>();
        logMaps.put("url",url);
        CloseableHttpResponse response = executeHttpRequest(httpClient,proxyHost,proxyPort,url,headers);
        if (response == null) {
            logManager.error(GET_BYTES_MESSAGE, "request fail!! response is null!!", logMaps);
            return new byte[0];
        }
        //获取响应对象中的响应码
        StatusLine statusLine=response.getStatusLine();//获取请求对象中的响应行对象
        int responseCode=statusLine.getStatusCode();//从状态行中获取状态码
        logMaps.put(RESPONSE_CODE,String.valueOf(responseCode));
        if (responseCode == SUCCESS_CODE){
            return EntityUtils.toByteArray(response.getEntity());
        } else {
            logManager.error(GET_BYTES_MESSAGE, "request fail!! code:"+responseCode, logMaps);
            return new byte[0];
        }
    }

    /**
     * post请求，返回String
     * @param url  请求url
     * @param headers 请求头
     * @param bytes 请求体
     * @return
     * @throws IOException
     */
    public String doPost(String url, Map<String,String> headers, byte[] bytes) throws IOException {
        Map<String,String> logMaps = new HashMap<>();
        logMaps.put("url",url);
        HttpPost httpPost =new HttpPost(url);
        if (!CollectionUtils.isEmpty(headers)){
            for (Map.Entry<String,String> entry: headers.entrySet()) {
                httpPost.addHeader(entry.getKey(),entry.getValue());
            }
        }
        //装填参数
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
        //设置参数到请求对象中
        httpPost.setEntity(byteArrayEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if (response == null) {
            logManager.error(POST_MESSAGE, "request fail!! response is null:", logMaps);
            return null;
        }
        //获取响应对象中的响应码
        StatusLine statusLine=response.getStatusLine();//获取请求对象中的响应行对象
        int responseCode=statusLine.getStatusCode();//从状态行中获取状态码
        logMaps.put(RESPONSE_CODE,String.valueOf(responseCode));
        if (responseCode == SUCCESS_CODE){
            return EntityUtils.toString(response.getEntity());
        } else {
            logManager.error(POST_MESSAGE, "request fail!!! code:"+responseCode, logMaps);
            return null;
        }
    }

    /**
     * post请求，返回String
     * @param proxyHost
     * @param proxyPort
     * @param url
     * @param headers
     * @param param
     * @return
     * @throws IOException
     */
    public String doPost(String proxyHost,Integer proxyPort,String url, Map<String,String> headers, Map<String,String> param) throws IOException {

        Map<String,String> logMaps = new HashMap<>();
        logMaps.put("url",url);
        HttpPost httpPost = null;
        if (!StringUtils.isEmpty(proxyHost) && proxyPort != null) {
            httpPost = getProxyHttpPost(proxyHost,proxyPort,url);
        }else {
            httpPost = getHttpPost(url);
        }
        if (!CollectionUtils.isEmpty(headers)){
            for (Map.Entry<String,String> entry: headers.entrySet()) {
                httpPost.addHeader(entry.getKey(),entry.getValue());
            }
        }
        //装填参数
        if (!CollectionUtils.isEmpty(param)){
            List<NameValuePair> list = new ArrayList<>();
            for (Map.Entry<String,String> entry : param.entrySet()) {
                NameValuePair nvp = new BasicNameValuePair(entry.getKey(),entry.getValue());
                list.add(nvp);
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"UTF-8");
            //设置参数到请求对象中
            httpPost.setEntity(entity);
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if (response == null) {
            logManager.error(POST_MESSAGE, "request fail!! response is null:", logMaps);
            return null;
        }
        //获取响应对象中的响应码
        StatusLine statusLine=response.getStatusLine();//获取请求对象中的响应行对象
        int responseCode=statusLine.getStatusCode();//从状态行中获取状态码
        logMaps.put(RESPONSE_CODE,String.valueOf(responseCode));
        if (responseCode == SUCCESS_CODE){
            return EntityUtils.toString(response.getEntity());
        } else {
            logManager.error(POST_MESSAGE, "request fail!! code:"+responseCode, logMaps);
            return null;
        }
    }

    /**
     * get HttpPost
     * @param url
     * @return
     */
    private HttpPost getHttpPost(String url){

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).build();
        // 请求地址
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        return httpPost;
    }

    /**
     * get proxy HttpPost
     * @param proxyHost
     * @param proxyPort
     * @param url
     * @return
     */
    private HttpPost getProxyHttpPost(String proxyHost,int proxyPort,String url){

        // 依次是代理地址，代理端口号，协议类型
        HttpHost proxy = new HttpHost(proxyHost, proxyPort,"http");
        RequestConfig config = RequestConfig.custom().setProxy(proxy)
                .setConnectTimeout(TIME_OUT).build();
        // 请求地址
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        return httpPost;
    }

    /**
     * get HttpGet
     * @param url
     * @return
     */
    private HttpGet getHttpGet(String url){
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIME_OUT).build();
        HttpGet httpGet =new HttpGet(url);
        httpGet.setConfig(config);
        return httpGet;
    }

    /**
     * get proxy HttpGet
     * @param proxyHost
     * @param proxyPort
     * @param url
     * @return
     */
    private HttpGet getProxyHttpGet(String proxyHost,int proxyPort,String url){

        // 依次是代理地址，代理端口号，协议类型
        HttpHost proxy = new HttpHost(proxyHost, proxyPort,"http");
        RequestConfig config = RequestConfig.custom().setProxy(proxy)
                .setConnectTimeout(TIME_OUT).build();
        // 请求地址
        HttpGet httpGet =new HttpGet(url);
        httpGet.setConfig(config);
        return httpGet;
    }

    @PostConstract
    public void initialize() {

        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加到100
        poolConnManager.setMaxTotal(100);
        // 将每个路由基础的连接增加到100
        poolConnManager.setDefaultMaxPerRoute(100);
        SocketConfig config = SocketConfig.custom().setSoKeepAlive(true).setSoTimeout(TIME_OUT).build();
        httpClient = HttpClients.custom().setConnectionManager(poolConnManager).setDefaultSocketConfig(config).build();
    }
}
