package com.qqh.library.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtil {

	// 获取sdcard的目录
	public static String getSDPath() {
		// 判断sdcard是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 获取根目录
			File sdDir = Environment.getExternalStorageDirectory();
			return sdDir.getPath();
		}
		return "/sdcard";
	}

	/**
	 * 供文件下载用
	 */
	private static String path = getSDPath() + "/field/";

	public static String createNewFile() {
		File dir = new File(path);
		if (dir.exists()) {
			Log.e("File create", "创建目录" + path + "失败，目标目录已存在！");
			return path;
		}
		if (!path.endsWith(File.separator))
			path = path + File.separator;
		// 创建单个目录
		if (dir.mkdirs()) {
			Log.e("File create", "创建目录" + path + "成功！");
		} else {
			Log.e("File create", "创建目录" + path + "失败！");
		}
		return path;
	}

	/**
	 * 文件是否已存在
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isFileExit(File file) {
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断指定目录是否有文件存在
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File getFiles(String path, String fileName) {
		File f = new File(path);
		File[] files = f.listFiles();
		if (files == null) {
			return null;
		}

		// 获取文件列表
		if (null != fileName && !"".equals(fileName)) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (fileName.equals(file.getName())) {
					return file;
				}
			}
		}
		return null;
	}
}
