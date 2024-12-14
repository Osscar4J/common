package com.zhao.common.utils;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static OkHttpClient httpClient;
    private static OkHttpClient httpsClient;
    private static OkHttpClient client;
    private static int READ_TIMEOUT = 120;
    private static int CONNECT_TIMEOUT = 120;
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    /**
     * 上传文件
     * @param url 接口地址
     * @param filePath 本地文件地址
     * @param fileName 文件名称
     * @return
     * @throws Exception
     */
    public static String upload(String url, String filePath, String fileName) throws Exception {
        return upload(url, new File(filePath), fileName);
    }

    /**
     * 上传文件
     * @param url 接口地址
     * @param file 文件
     * @param fileName 文件名称
     * @return
     * @throws Exception
     */
    public static String upload(String url, File file, String fileName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response.body().string();
    }

    /**
     * GET请求
     * @param url 地址
     * @param callback 回调，如果是同步的请求就传null
     * @return
     */
    public static String get(String url, Callback callback){
        String result = null;

        if (httpClient == null)
            initHttpClient();

        client = httpClient;
        if (url.startsWith("https:")){
            if (httpsClient == null)
                initHttpsClient();
            client = httpsClient;
        }

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        if (callback != null){ // 异步请求
            call.enqueue(callback);
        } else { // 同步请求
            try {
                Response resp = call.execute();
                if (resp != null)
                    result = resp.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    /**
     * 同步GET请求
     */
    public static String get(String url){
        return get(url, null);
    }

    public static String post(String url, String requestBody, Callback callback){
        return post(url, requestBody, callback, null, null);
    }

    public static String postJson(String url, String requestBody, Map<String, String> headers){
        return post(url, requestBody, null, headers, "application/json;charset=UTF-8");
    }

    /**
     * POST请求
     * @param url 地址
     * @param requestBody POST参数
     * @param callback 回调，如果是同步的请求就传null
     */
    public static String post(String url, String requestBody, Callback callback, Map<String, String> headers, String contentType){
        if (StringUtils.isEmpty(contentType))
            contentType = "application/x-www-form-urlencoded";
        String result = null;

        if (httpClient == null)
            initHttpClient();

        client = httpClient;
        if (url.startsWith("https:")){
            if (httpsClient == null)
                initHttpsClient();
            client = httpsClient;
        }

        MediaType mediaType = MediaType.parse(contentType);
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()){
            for (String s : headers.keySet()) {
                builder.header(s, headers.get(s));
            }
        }
        Request request = builder.post(RequestBody.create(mediaType, requestBody)).build();
        if (callback != null){
            client.newCall(request).enqueue(callback);
        } else {
            try {
                Response resp = client.newCall(request).execute();
                if (resp != null)
                    result = resp.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 同步POST
     */
    public static String post(String url, String requestBody){
        return post(url, requestBody, null);
    }

    private synchronized static void initHttpsClient(){
        if (httpsClient == null){
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();//设置读取超时时间;//设置连接超时时间;
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                httpsClient = builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private synchronized static void initHttpClient(){
        if (httpClient == null)
            httpClient = new OkHttpClient.Builder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();
    }

}
