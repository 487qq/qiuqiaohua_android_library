package com.qqh.library.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	/**
	 * 获取标准日期时间字符串 时区为"GMT+8:00"
	 * 
	 * @return "2012-08-27 10:58:45"
	 */
	public static String getDateTime(int multiple) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(new Date().getTime() - 60 * 60 * 24 * 7
				* 1000L * multiple + 60 * 60 * 24 * 1000));
	}

    /**
     * 获取当前时间
     * @return
     */
	public static String getNowDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME = "HH:mm:ss";
	public static final String DATE = "yyyy-MM-dd";

	public static java.sql.Date string2Date(String dateString, String format) {
		try {
			DateFormat dateFormat;
			dateFormat = new SimpleDateFormat(format);
			dateFormat.setLenient(false);
			Date timeDate = dateFormat.parse(dateString);// util类型
			java.sql.Date dateTime = new java.sql.Date(timeDate.getTime());// sql类型
			return dateTime;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 日期转换成Java字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date, String type) {

		SimpleDateFormat format = new SimpleDateFormat(type);
		String str = format.format(date);
		return str;
	}

    /**
     * 格式化日期字符串
     * @param value
     * @param type
     * @return
     */
	public static String formateDate(String value, String type) {
		String date = "";
		try {
			date = DateToStr(string2Date(value, type), type);
		} catch (Exception e) {
			date = value;
		}
		return date;
	}
}
