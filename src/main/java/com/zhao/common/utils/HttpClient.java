package com.zhao.common.utils;

import okhttp3.*;
import okhttp3.Request.Builder;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;

public class HttpClient {

	public static final MediaType type = MediaType.parse("application/json;charset=utf-8");
    public static OkHttpClient httpClient = new OkHttpClient();
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    private static MyTrustManager mMyTrustManager;

    private static OkHttpClient client = null;
	private static OkHttpClient httpsClient = null;

	private HttpClient() {
	}

	public static String doGet(String url) {
		final Request request = new Builder().url(url).build();
		return execute(request);
	}
	
	public static String getHttps(String url) {
		final Request request = new Builder().url(url).build();
		return execute(request, getTrustAllClient());
	}

	public static String doPost(String url, String content) {
		return doBodyRequest(METHOD_POST, url, content);
	}
	
	public static String doPut(String url, String content) {
		return doBodyRequest(METHOD_PUT, url, content);
	}
	
	public static String doDelete(String url, String content) {
		return doBodyRequest(METHOD_DELETE, url, content);
	}
	
	public static String doBodyRequest(String method, String url, String content) {
		RequestBody requestBody = RequestBody.create(type, content);
		Builder builder = new Builder().url(url);
        Request request = null;
        switch (method) {
	        case METHOD_POST:
	        	request = builder.post(requestBody).build();
	        	break;
	        case METHOD_PUT:
	        	request = builder.put(requestBody).build();
	        	break;
	        case METHOD_DELETE:
	        	request = builder.delete(requestBody).build();
	        	break;
        	default:
        		throw new RuntimeException("不支持的请求类型");
        }
        Response response = null;
		try {
			response = httpClient.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            mMyTrustManager = new MyTrustManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

	public static String execute(Request request) {
		return execute(request, null);
	}
	
	public static String execute(Request request, OkHttpClient client) {
		try {
			Response response = executeWithResponse(request, client);
			if (response.isSuccessful()) {
				return response.body().string();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return "{\"error\":\"fail\"}";
	}

	public static Response executeWithResponse(Request request, OkHttpClient client) {
		try {
			if (client == null)
				client = httpClient;
			return client.newCall(request).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//实现X509TrustManager接口
    public static class MyTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws java.security.cert.CertificateException {
		}
		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
				throws java.security.cert.CertificateException {
		}
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}
    }

    //实现HostnameVerifier接口
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static OkHttpClient getTrustAllClient() {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(createSSLSocketFactory(), mMyTrustManager)
                .hostnameVerifier(new TrustAllHostnameVerifier());
        return mBuilder.build();
    }

}
