package com.zhao.common.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @ClassName: HttpAsyncUtils
 * @Author: zhaolianqi
 * @Date: 2022/11/18 14:38
 * @Version: v1.0
 */
public class HttpAsyncUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpAsyncUtils.class);
    //从池中获取链接超时时间(ms)
    private static final int CONNECTION_REQUEST_TIMEOUT = 15000;
    //建立链接超时时间(ms)
    private static final int CONNECT_TIMEOUT = 60000;
    //读取超时时间(ms)
    private static final int SOCKET_TIMEOUT = 180000;
    //连接数
    private static final int MAX_TOTAL = 100;
    private static final int MAX_PER_ROUTE = 100;

    private static final CloseableHttpAsyncClient httpclient;

    static {
        httpclient = init();
        assert httpclient != null;
        httpclient.start();
    }

    private static CloseableHttpAsyncClient init() {
        CloseableHttpAsyncClient client;
        try {
            // 绕过证书验证，处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();

            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder
                    .<SchemeIOSessionStrategy> create().register("http", NoopIOSessionStrategy.INSTANCE)
                    .register("https", new SSLIOSessionStrategy(sslcontext, new AllowAllHostnameVerifier())).build();

            //配置io线程
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                    setIoThreadCount(Runtime.getRuntime().availableProcessors())
                    .setSoKeepAlive(true)
                    .build();
            //创建一个ioReactor
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
//            poolManager=new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            PoolingNHttpClientConnectionManager poolManager = new PoolingNHttpClientConnectionManager(ioReactor, null, sessionStrategyRegistry, (DnsResolver) null);
            //设置连接池大小
            poolManager.setMaxTotal(MAX_TOTAL);
            poolManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
            // 配置请求的超时设置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build();

            client= HttpAsyncClients.custom()
                    .setConnectionManager(poolManager)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            return client;
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Future<HttpResponse> get(String url){
        return get(url, null);
    }

    public static void close(){
        try {
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MyTrustManager implements NHttpClientConnectionManager {
        @Override
        public Future<NHttpClientConnection> requestConnection(HttpRoute httpRoute, Object o, long l, long l1, TimeUnit timeUnit, FutureCallback<NHttpClientConnection> futureCallback) {
            return null;
        }

        @Override
        public void releaseConnection(NHttpClientConnection nHttpClientConnection, Object o, long l, TimeUnit timeUnit) {

        }

        @Override
        public void startRoute(NHttpClientConnection nHttpClientConnection, HttpRoute httpRoute, HttpContext httpContext) throws IOException {

        }

        @Override
        public void upgrade(NHttpClientConnection nHttpClientConnection, HttpRoute httpRoute, HttpContext httpContext) throws IOException {

        }

        @Override
        public void routeComplete(NHttpClientConnection nHttpClientConnection, HttpRoute httpRoute, HttpContext httpContext) {

        }

        @Override
        public boolean isRouteComplete(NHttpClientConnection nHttpClientConnection) {
            return false;
        }

        @Override
        public void closeIdleConnections(long l, TimeUnit timeUnit) {

        }

        @Override
        public void closeExpiredConnections() {

        }

        @Override
        public void execute(IOEventDispatch ioEventDispatch) throws IOException {

        }

        @Override
        public void shutdown() throws IOException {

        }
    }

    public static Future<HttpResponse> get(String url, List<NameValuePair> ns) {
        HttpGet httpget;
        URIBuilder uri = new URIBuilder();
        try {
            if (ns != null){
                uri.setPath(url);
                uri.addParameters(ns);
                httpget = new HttpGet(uri.build());
            }else{
                httpget = new HttpGet(url);
            }

            httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");

            // One most likely would want to use a callback for operation result
            return httpclient.execute(httpget, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    logger.info("{} -> {}", httpget.getRequestLine(), response.getStatusLine());
                }

                public void failed(final Exception ex) {
                    logger.error(httpget.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
                    logger.error(httpget.getRequestLine() + " cancelled");
                }
            });
        } catch (Exception e){
            logger.error("[发送get请求失败]URL:{},异常:", uri.getUserInfo(), e);
        }
        return null;
    }

    public static Future<HttpResponse> post(String url, String jsonParams){
        return post(url, null, jsonParams, null, null, null);
    }
    public static Future<HttpResponse> post(String url, String jsonParams, Function<String, String> callback, final Function<Exception, String> onFailed){
        return post(url, null, jsonParams, null, callback, onFailed);
    }

    public static Future<HttpResponse> postFormData(String url, Map<String, String> headers, String formData,
                                                    Function<String, String> callback,
                                                    final Function<Exception, String> onFailed){
        return post(url, headers, formData, "application/x-www-form-urlencoded;charset=UTF-8", callback, onFailed);
    }

    public static Future<HttpResponse> post(String url, Map<String, String> headers, String jsonParams, String contentType,
                                            final Function<String, String> callback,
                                            final Function<Exception, String> onFailed) {
        HttpPost httpPost;
        URIBuilder uri = new URIBuilder();
        try {
            httpPost = new HttpPost(url);
            if (jsonParams != null){
                ByteArrayEntity entity;
                entity = new ByteArrayEntity(jsonParams.getBytes(StandardCharsets.UTF_8));
                if (contentType == null){
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                } else {
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
                }
                httpPost.setEntity(entity);
            }

            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");

            if (headers != null){
                for (String s : headers.keySet()) {
                    httpPost.addHeader(s, headers.get(s));
                }
            }

            // One most likely would want to use a callback for operation result
            return httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    logger.info("{} -> {}", httpPost.getRequestLine(), response.getStatusLine());
                    if (callback != null){
                        try {
                            callback.apply(EntityUtils.toString(response.getEntity()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                public void failed(final Exception ex) {
                    logger.error(httpPost.getRequestLine() + "->" + ex);
                    if (onFailed != null){
                        onFailed.apply(ex);
                    }
                }

                public void cancelled() {
                    logger.error(httpPost.getRequestLine() + " cancelled");
                }
            });
        } catch (Exception e){
            logger.error("[发送post请求失败]URL:{},异常:", uri.getUserInfo(), e);
        }
        return null;
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sc.init(null, new TrustManager[] { trustManager }, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return sc;

    }

//    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
//        String url = "https://sickw.com/api.php?format=html&key=233-SF1-TQS-K0X-ASD-HT2-TPH-2ZC&service=8&imei=C9kgfw7796";
////        url = "https://zdgx.gxgllib.org.cn/zdgx-home/scenicSpot";
//        Future<HttpResponse> future = get(url);
//        System.out.println(EntityUtils.toString(future.get().getEntity()));
////        for (int i=0; i<10;i++){
////            System.out.println("第" + i +"次：");
////            Future<HttpResponse> future = get("http://httpbin.org/get",null);
////            try {
////                assert future != null;
////                System.out.println(EntityUtils.toString(future.get().getEntity()));
////            } catch (IOException | InterruptedException | ExecutionException e) {
////                e.printStackTrace();
////            }
////        }
////        String url = "https://hxzj.teetrons.com.cn/zs/spool/v1/digitalRes/getPages";
////        Map<String, String> headers = new HashMap<>(2);
////        headers.put("sysCode", "WCLOUD-SN");
////        Future<HttpResponse> future = post(url, headers, "{\"cpage\":11,\"pageSize\":20,\"isCount\":1,\"classify\":\"\",\"firstWord\":\"\",\"lang\":\"xxx\",\"sort\":3}");
////        System.out.println(EntityUtils.toString(future.get().getEntity()));
////
//        close();
//    }

}
