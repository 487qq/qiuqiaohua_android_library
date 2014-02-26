package com.qqh.library.utils;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: qiuqiaohua
 * Email:qiaohua.qiu@gmail.com
 * Date: 13-10-12
 * Time: 上午11:14
 *
 */
public class DensityUtil {

    /**
     * dip转化为px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     *   px 转化为dip
     * @param context
     * @param pxValue
     * @return
     */

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

}
