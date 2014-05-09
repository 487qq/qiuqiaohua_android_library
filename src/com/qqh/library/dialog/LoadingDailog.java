package com.qqh.library.dialog;

import com.qqh.library.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
public class LoadingDailog {

	private static Dialog mDialog;

	private OnProgressListener onProgressListener;

	private Context context;
	private boolean isCancle = true;

	private static LoadingDailog loadingDailog = null;

	private static boolean isLDShow = false;

	private LoadingDailog(Context context, OnProgressListener onProgressListener) {
		this.context = context;
		this.onProgressListener = onProgressListener;

		// createDialog();
	}

	public static LoadingDailog getLD(Context context,
			OnProgressListener onProgressListener) {
		if (loadingDailog == null) {
			loadingDailog = new LoadingDailog(context, onProgressListener);
		} else {
			loadingDailog.context = context;
			loadingDailog.onProgressListener = onProgressListener;
		}

		return loadingDailog;
	}

	public void loading() {
		try {
			new Thread() {
				public void run() {
					onProgressListener.doInbackground();
					if (context instanceof Activity) {
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								onProgressListener.onComplete();
								HideLoadingD();
							}
						});
					}
				};
			}.start();
		} catch (Exception e) {
		}
	}

	public void showLoadingDg() {
		if (isLDShow) {
			HideLoadingD();
		}
		createDialog();
		mDialog.setCancelable(isCancle);
		mDialog.show();
		isLDShow = true;
		try {
			new Thread() {
				public void run() {
					onProgressListener.doInbackground();
					if (context instanceof Activity) {
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								onProgressListener.onComplete();
								HideLoadingD();
							}
						});

					}

				};
			}.start();
		} catch (Exception e) {
			if (isLDShow) {
				HideLoadingD();
			}
		}
	}

	/**
	 * 隐藏对话框
	 */
	private static void HideLoadingD() {
		isLDShow = false;
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	public void setCancle(boolean cancle) {
		isCancle = cancle;
	}

	/**
	 * 创建对话框
	 */
	private void createDialog() {
		mDialog = null;
		mDialog = new Dialog(context, R.style.loading_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		mDialog.setContentView(view);
	}

	public void showDialog() {
		if (isLDShow) {
			HideLoadingD();
		}
		createDialog();
		mDialog.show();
		isLDShow = true;
	}

	public static void dismissDialog() {
		HideLoadingD();
	}

}
