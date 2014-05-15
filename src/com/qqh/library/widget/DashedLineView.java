package com.qqh.library.widget;


import com.qqh.library.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * <垂直和水平虚线>
 * <功能详细描述>
 * 
 * @author  qiuqiaohua
 * @version  [版本号, Apr 21, 2014]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DashedLineView extends View
{
    /**
     * 线条横向
     */
    public static final int HORIZONTAL = 0;
    
    /**
     * 线条竖向
     */
    public static final int VERTICAL = 1;
    
    /**
     * 虚线
     */
    public static final int DASH = 0;
    
    /**
     * 实线
     */
    public static final int REAL = 1;
    
    private static final String DEFAULTLINECOLOR="#939393";
    
    private int mOrientation;
    /**
     * 线条类型
     */
    private int mLineType;
    
    /**
     * 线条颜色
     */
    private int mLineColor;
    
    
    
    public DashedLineView(Context context)
    {
        this(context, null);
    }
    
    public DashedLineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DashedLineView);
        int index = a.getInt(R.styleable.DashedLineView_orientation, HORIZONTAL);
        setOrientation(index);
        int type = a.getInt(R.styleable.DashedLineView_lineType, DASH);
        setType(type);
        int lineColor=a.getColor(R.styleable.DashedLineView_lineColor,-1);
        setLinecolor(lineColor);
        a.recycle();
    }
    
    public void setOrientation(int index)
    {
        if (mOrientation != index)
        {
            mOrientation = index;
            this.invalidate();
        }
    }
    
    public void setType(int type)
    {
        if (mLineType != type)
        {
            mLineType = type;
            this.invalidate();
        }
    }
    
    
    public void setLinecolor(int lineColor)
    {
        if (mLineColor != lineColor)
        {
            mLineColor = lineColor;
            this.invalidate();
        }
    }
    
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mLineColor!=-1?mLineColor:Color.parseColor(DEFAULTLINECOLOR));
        Path path = new Path();
        switch (mOrientation)
        {
            case VERTICAL:
                path.moveTo(0, 0);
                path.lineTo(0, this.getHeight());
                break;
            
            case HORIZONTAL:
                path.moveTo(0, 0);
                path.lineTo(this.getWidth(), 0);
                break;
        }
        
        switch (mLineType)
        {
            case DASH:
                PathEffect effects = new DashPathEffect(new float[] {5, 5, 5, 5}, 1);
                paint.setPathEffect(effects);
                break;
            
            case REAL:
                
                break;
        }
        
        canvas.drawPath(path, paint);
    }


    
    
}
