package com.sdbnet.hywy.employee.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sdbnet.hywy.employee.utils.LogUtil;

import android.os.AsyncTask;
import android.util.Log;

public class CustomerHttpClient {
	// private static final String BASE_URL =
	// "http://218.17.218.106:5475/HJSERVER";
	// private static final String BASE_URL =
	// "http://192.168.1.103:8080/HJSERVER";
	private static final String BASE_URL = "http://www.sdbnet.com:8000/HJSERVER";

	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static ICallBackHandler mHandler;

	private static final String TAG = "CustomerHttpClient";

	public static void registrationListener(ICallBackHandler handler) {
		mHandler = handler;
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 1000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 2000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 4000);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	public static String post(String url, NameValuePair... params) {
		try {
			// 编码参数
			List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
			for (NameValuePair p : params) {
				formparams.add(p);
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					CHARSET);

			// 创建POST请求
			HttpPost request = new HttpPost(getAbsoluteUrl(url));
			request.setEntity(entity);
			// 发送请求
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("请求失败");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity,
					CHARSET);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("连接失败", e);
		}

	}

	private static String mCooder;

	public static void executeGet(String url) {
		try {
			String[] param = new String[] { url };
			MyTask task = new MyTask();
			task.execute(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class MyTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			BufferedReader in = null;

			String content = null;
			try {
				// 定义HttpClient
				HttpClient client = getHttpClient();
			
				HttpClientParams.setCookiePolicy(client.getParams(),
						CookiePolicy.BROWSER_COMPATIBILITY);
				// 实例化HTTP方法
				HttpGet request = new HttpGet();
				request.setURI(new URI(params[0]));
				HttpResponse response = client.execute(request);

				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					LogUtil.d(TAG, "result: 请求失败");
				}

				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				content = sb.toString();
			} finally {
				if (in != null) {
					try {
						in.close();// 最后要关闭BufferedReader
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return content;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				// System.out.println("result: " + result);

				if (mHandler != null) {
					mHandler.onReceived(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public interface ICallBackHandler {
		void onReceived(String result);
	}
}
