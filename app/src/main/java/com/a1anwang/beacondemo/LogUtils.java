package com.a1anwang.beacondemo;

import android.util.Log;

public class LogUtils {

	
	public static boolean open=true;
	
	
	public static void e(String TAG,String msg){
		if(open)
		Log.e(TAG, msg);
	}
	
}
