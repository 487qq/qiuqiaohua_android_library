package com.qqh.library.widget;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;

import com.qqh.library.R;


/**
 * Created by qiuqiaohua on 2/11/14.
 * 使用该组件时,如果使用Activity，调用的Activity必须为ActivityGroup
 * 使用该组件时,如果使用Fragmnet，调用的Fragment必须为FragmentActivity
 */

@SuppressWarnings("deprecation")
public class TabViewPager extends LinearLayout
{
    
    private Context mContext;
    
    /**
     * 主视图
     */
    private View mainView;
    
    /**
     * 标签
     */
    private TabHost tabHost;
    
    /**
     * 显示内容的viewpager
     */
    private ViewPager viewPager;
    
    /**
     * ActivityManager
     */
    private LocalActivityManager manager;
    
    /**
     * activity views
     */
    private ArrayList<View> listViews;
    
    /**
     * fragment class
     */
    private ArrayList<Class<?>> listClasses;
    
    /**
     * 目前标签的位置
     */
    private int curPosition = 0;
    
    /**
     * 每个标签的宽度
     */
    private int width;
    
    /**
     * 游标高度
     */
    private static final int CURSOR_HEIGHT = 4;
    
    /**
     * 游标高度
     */
    private static final int MARGIN = 20;
    
    /**
     * 动画图片
     */
    private ImageView ivCursor;
    
    public TabViewPager(Context context)
    {
        this(context, null);
    }
    
    public TabViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.tab_viewpager, this);
        findView();
        
    }
    
    private void findView()
    {
        tabHost = (TabHost)mainView.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(tabChangeListener);
        viewPager = (ViewPager)mainView.findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(pageChangeListener);
        
    }
    
    /**
     * 添加页卡和内容
     * @param titles
     * @param activity
     */
    public void addTab(String[] titles, Class<?>[] activity)
    {
        initImageView(activity.length);
        manager = ((ActivityGroup)mContext).getLocalActivityManager();
        tabHost.setup(manager);
        listViews = new ArrayList<View>();
        for (int i = 0; i < titles.length; i++)
        {
            String s = titles[i];
            Class<?> a = activity[i];
            Intent intent = new Intent(mContext, a);
            listViews.add(manager.startActivity(i + "", intent).getDecorView());
            LinearLayout tabWidget = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.tabwidget, null);
            TextView title = (TextView)tabWidget.findViewById(R.id.tabTitle);
            title.setText(s);
            tabHost.addTab(tabHost.newTabSpec("tab" + i).setIndicator(tabWidget).setContent(intent));
        }
        
        viewPager.setAdapter(new MyPagerAdapter(listViews));
        viewPager.setOffscreenPageLimit(3);//设置viewPager 默认加载几页
        tabHost.setCurrentTab(curPosition);
    }
    
    /**
     * 
     * <添加页卡和内容>
     * <功能详细描述>
     * @param titles tab标签的内容
     * @param fragments tab内容fragment
     * @see [类、类#方法、类#成员]
     */
    public void addFragmentTab(String[] titles, Class<?>[] fragments)
    {
        initImageView(fragments.length);
        listClasses = new ArrayList<Class<?>>();
        for (int i = 0; i < titles.length; i++)
        {
            String s = titles[i];
            Class<?> a = fragments[i];
            listClasses.add(a);
            LinearLayout tabWidget = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.tabwidget, null);
            TextView title = (TextView)tabWidget.findViewById(R.id.tabTitle);
            title.setText(s);
            tabHost.addTab(tabHost.newTabSpec("tab" + i)
                .setIndicator(tabWidget)
                .setContent(new DummyTabFactory(mContext)));
        }
        
        viewPager.setAdapter(new TabAdapter((FragmentActivity)mContext, listClasses));
        
        viewPager.setOffscreenPageLimit(3);//设置viewPager 默认加载几页
        tabHost.setCurrentTab(curPosition);
    }
    
    static class DummyTabFactory implements TabHost.TabContentFactory
    {
        private final Context mContext;
        
        public DummyTabFactory(Context context)
        {
            mContext = context;
        }
        
        @Override
        public View createTabContent(String tag)
        {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }
    
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int i, float v, int i2)
        {
            
        }
        
        @Override
        public void onPageSelected(int i)
        {
            doAnim(i);
            curPosition = i;
            
            TabWidget widget = tabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            tabHost.setCurrentTab(i);
            widget.setDescendantFocusability(oldFocusability);
        }
        
        @Override
        public void onPageScrollStateChanged(int i)
        {
            
        }
    };
    
    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener()
    {
        @Override
        public void onTabChanged(String tabId)
        {
            int position = tabHost.getCurrentTab();
            viewPager.setCurrentItem(position);
        }
    };
    
    private class MyPagerAdapter extends PagerAdapter
    {
        
        private ArrayList<View> views;
        
        private MyPagerAdapter(ArrayList<View> views)
        {
            this.views = views;
        }
        
        @Override
        public int getCount()
        {
            return views.size();
        }
        
        @Override
        public boolean isViewFromObject(View view, Object o)
        {
            return view == o;
        }
        
        @Override
        public void startUpdate(ViewGroup container)
        {
            super.startUpdate(container);
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView((View)views.get(position));
            return views.get(position);
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View)views.get(position));
        }
        
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object)
        {
            super.setPrimaryItem(container, position, object);
        }
        
        @Override
        public void finishUpdate(ViewGroup container)
        {
            super.finishUpdate(container);
        }
    }
    
    private class TabAdapter extends FragmentPagerAdapter
    {
        
        private ArrayList<Class<?>> mClasses;
        
        private FragmentActivity mFActivity;
        
        public TabAdapter(FragmentActivity fActivity, ArrayList<Class<?>> classes)
        {
            super(fActivity.getSupportFragmentManager());
            this.mClasses = classes;
            this.mFActivity = fActivity;
        }
        
        @Override
        public android.support.v4.app.Fragment getItem(int arg0)
        {
            return android.support.v4.app.Fragment.instantiate(mFActivity, mClasses.get(arg0).getName());
        }
        
        @Override
        public int getCount()
        {
            return mClasses.size();
        }
        
    }
    
    /**
     *  执行动画
     * <功能详细描述>
     * @param position
     * @see [类、类#方法、类#成员]
     */
    private void doAnim(int position)
    {
        /**
         * 创建动画
         */
        TranslateAnimation anim = new TranslateAnimation(curPosition * ivCursor.getWidth(), position * width, 0, 0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        
        /**
         * 执行动画
         */
        ivCursor.startAnimation(anim);
    }
    
    /**
     * 
     * <初始化标签移动的动画>
     * <功能详细描述>
     * @param num 标签的数量
     * @see [类、类#方法、类#成员]
     */
    private void initImageView(int num)
    {
        /**
         *  初始化ivCursor
         */
        ivCursor = (ImageView)mainView.findViewById(R.id.ivCursor);
        width = getResources().getDisplayMetrics().widthPixels / num;
        LayoutParams params = new LayoutParams(width - MARGIN, CURSOR_HEIGHT);
        /**
         *  设置ivcursor的布局参数
         */
        params.setMargins(MARGIN / 2, 0, 0, 0);
        ivCursor.setLayoutParams(params);
    }
    
}
