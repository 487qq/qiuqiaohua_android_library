/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qqh.library.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
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
 * 
 * <TabViewPager>
 * <viewpager 使用的fragment>
 * 
 * @author  qiuqiaohua
 * @version  [版本号, May 6, 2014]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class TabsPager extends LinearLayout
{
    private Context mContext;
    
    /**
     * 主视图
     */
    private View mainView;
    
    TabHost mTabHost;
    
    ViewPager mViewPager;
    
    TabsAdapter mTabsAdapter;
    
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
    
    public TabsPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.tab_viewpager, this);
        findView();
        /**
         *  初始化ivCursor
         */
        ivCursor = (ImageView)mainView.findViewById(R.id.ivCursor);
        
    }
    
    private void findView()
    {
        mTabHost = (TabHost)mainView.findViewById(R.id.tabHost);
        mTabHost.setup();
        mViewPager = (ViewPager)mainView.findViewById(R.id.viewPager);
        mTabsAdapter = new TabsAdapter((FragmentActivity)mContext, mTabHost, mViewPager);
    }
    
    /**
     * 
     * <添加Fragment标签页和标题>
     * <功能详细描述>
     * @param tag       标签的tag
     * @param title     标签内容
     * @param clss      Fragment.class
     * @param args      Bundle of arguments to supply to the fragment, which it can retrieve with getArguments(). May be null.
     * @see [类、类#方法、类#成员]
     */
    public void addTab(String tag, String title, Class<?> clss, Bundle args)
    {
        /**
         * tab 标题栏样式
         */
        View tabWidget = LayoutInflater.from(mContext).inflate(R.layout.tabwidget, null);
        TextView titleView = (TextView)tabWidget.findViewById(R.id.tabTitle);
        titleView.setText(title);
        mTabsAdapter.addTab(mTabHost.newTabSpec(tag).setIndicator(tabWidget), clss, args);
        initImageView(mTabsAdapter.getCount());
    }
    
    /**
     * 
     * <设置目前选择的标签>
     * <功能详细描述>
     * @param index
     * @see [类、类#方法、类#成员]
     */
    public void setCurrentTab(int index)
    {
        curPosition = index;
        mTabHost.setCurrentTab(index);
    }
    
    public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener,
        ViewPager.OnPageChangeListener
    {
        private final Context mContext;
        
        private final TabHost mTabHost;
        
        private final ViewPager mViewPager;
        
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        
        class TabInfo
        {
            private final String tag;
            
            private final Class<?> clss;
            
            private final Bundle args;
            
            TabInfo(String _tag, Class<?> _class, Bundle _args)
            {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }
        
        class DummyTabFactory implements TabHost.TabContentFactory
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
        
        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager)
        {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }
        
        /**
         * 
         * <添加标签>
         * <功能详细描述>
         * @param tabSpec
         * @param clss
         * @param args
         * @see [类、类#方法、类#成员]
         */
        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args)
        {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();
            
            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount()
        {
            return mTabs.size();
        }
        
        @Override
        public Fragment getItem(int position)
        {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }
        
        @Override
        public void onTabChanged(String tabId)
        {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }
        
        @Override
        public void onPageSelected(int position)
        {
            doAnim(position);
            curPosition = position;
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }
        
        @Override
        public void onPageScrollStateChanged(int state)
        {
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
        
        width = getResources().getDisplayMetrics().widthPixels / num;
        LayoutParams params = new LayoutParams(width - MARGIN, CURSOR_HEIGHT);
        /**
         *  设置ivcursor的布局参数
         */
        params.setMargins(MARGIN / 2, 0, 0, 0);
        ivCursor.setLayoutParams(params);
    }
}
