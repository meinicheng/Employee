//package com.sdbnet.hywy.employee.net;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request.Method;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.Response.Listener;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.ImageLoader.ImageCache;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.sdbnet.hywy.employee.utils.PreferencesUtil;
//
//public class VolleyManager {
//	public static final String BASE_URL = "http://182.18.31.50/HYWY";
//	// public static final String BASE_URL = "http://192.168.1.102:8080/HYWY";
//	// private static final String BASE_URL = "http://172.28.33.1:8080/HYWY";
//
//	private static final String TAG = "AsyncHttpService";
//	private static final String HYWY_API_LOGIN = BASE_URL + "/uselogin";
//
//	private VolleyManager() {
//
//	}
//
//	private static VolleyManager manager;
//	private static Context mContext;
//
//	public static VolleyManager getInstance(Context context) {
//		if (manager == null) {
//			manager = new VolleyManager();
//		}
//		mContext = context;
//		return manager;
//
//    }
//
//	public void login(final String tel, final String pwd) {
//		StringRequest mRequest = new StringRequest(Method.POST, HYWY_API_LOGIN,
//				new Listener<String>() {
//
//					@Override
//					public void onResponse(String arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				}, new ErrorListener() {
//
//					@Override
//					public void onErrorResponse(VolleyError arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				}
//
//		) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> loginMap = new HashMap<String, String>();
//				loginMap.put("tel", tel);
//				loginMap.put("pwd", pwd);
////				(Constants.Feild.KEY_USER_TYPE, PreferencesUtil.user_type);
//				loginMap.put("userType", PreferencesUtil.user_type);
//				return loginMap;
//			}
//		};
//
//	}
//
//	private StringRequest stringRequest;
//
//	public void stringtRequest(StringRequest stringRequest) {
//		this.stringRequest = stringRequest;
//		Volley.newRequestQueue(mContext).add(stringRequest);
//	}
//
//	public void Request(String url) {
//		RequestQueue queue = Volley.newRequestQueue(mContext);
//
//		StringRequest stringRequest = new StringRequest(Method.POST, url,
//				new Listener<String>() {
//
//					@Override
//					public void onResponse(String arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				}, new ErrorListener() {
//
//					@Override
//					public void onErrorResponse(VolleyError arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//		StringRequest stringRequestMap = new StringRequest(Method.POST, url,
//				mListener, mErrorListener) {
//			@Override
//			protected Map<String, String> getParams() throws AuthFailureError {
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("params1", "value1");
//				map.put("params2", "value2");
//				return map;
//			}
//		};
//
//		// ImageRequest imageRequest=new ImageRequest(url, new
//		// Listener<Bitmap>() {
//		// }, maxWidth, maxHeight, decodeConfig, new )
//
//		// 1. 创建一个RequestQueue对象。
//
//		RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
//
//		// 2. 创建一个ImageLoader对象。
//
//		ImageLoader mImageLoader = new ImageLoader(mRequestQueue,
//				new ImageCache() {
//
//					@Override
//					public void putBitmap(String arg0, Bitmap arg1) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public Bitmap getBitmap(String arg0) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//				});
//
//		// 3. 获取一个ImageListener对象。
//
//		// ImageListener imageListener = ImageLoader.getImageListener(imageView,
//		// R.drawable.image_default, R.drawable.image_error);
//
//		// 4. 调用ImageLoader的get()方法加载网络上的图片。
//
//		// mImageLoader.get("http://www.baidu.com/img/bdlogo.png",
//		// imageListener);
//		//
//		// mImageLoader.get("http://www.baidu.com/img/bdlogo.png",
//		// imageListener,
//		// 200, 200);
//
//	}
//
//	private Listener<String> mListener = new Listener<String>() {
//
//		@Override
//		public void onResponse(String arg0) {
//			// TODO Auto-generated method stub
//
//		}
//	};
//
//	private ErrorListener mErrorListener = new ErrorListener() {
//
//		@Override
//		public void onErrorResponse(VolleyError arg0) {
//			// TODO Auto-generated method stub
//
//		}
//	};
//
//}
