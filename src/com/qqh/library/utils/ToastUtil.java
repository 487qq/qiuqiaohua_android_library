package com.qqh.library.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void showToast(String msg,Context context){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
