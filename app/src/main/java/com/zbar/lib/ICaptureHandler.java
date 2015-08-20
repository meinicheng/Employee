package com.zbar.lib;

import android.os.Handler;

public interface ICaptureHandler {

	public Handler getHandler();
	
	public void handleDecode(String result);
	
	public boolean isNeedCapture();

	public void setNeedCapture(boolean isNeedCapture);

	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getCropWidth();

	public void setCropWidth(int cropWidth);

	public int getCropHeight();

	public void setCropHeight(int cropHeight);
}
