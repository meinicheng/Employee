package com.zbar.lib.decode;

import java.util.concurrent.CountDownLatch;

import com.sdbnet.hywy.employee.ui.CaptureActivity;
import com.zbar.lib.ICaptureHandler;

import android.os.Handler;
import android.os.Looper;

/**
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {

	CaptureActivity activity;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
	private ICaptureHandler captureHandler;

	DecodeThread(CaptureActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}
	
	DecodeThread(ICaptureHandler captureHandler) {
		this.captureHandler = captureHandler;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(captureHandler);
//		handler = new DecodeHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
