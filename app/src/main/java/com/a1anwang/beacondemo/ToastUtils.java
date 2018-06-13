package com.a1anwang.beacondemo;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtils {

	public static void showToast(Context context, String content, int duration) {
		if(duration<=0)  return;
		final Toast toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				toast.show();
			}
		}, 0, 3000);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				toast.cancel();
				timer.cancel();
			}
		}, duration);
	}

}
