/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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



import com.qqh.library.R;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {

    
    private static final CharSequence EMPTY_TITLE = "";
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private LinearLayout mTabLayout;
    /**
     * 选中的tab
     */
    private int mSelectedTabIndex;
    public TabPageIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        View view=LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        mTabLayout=(LinearLayout)view.findViewById(R.id.ll_divider);
//        mTabLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        mTabLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        mTabLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.line_vertical));
//        mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
//        mTabLayout.setDividerPadding(10);
        addView(view, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    @Override
    public void onPageScrollStateChanged(int arg0)
    {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
        
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0)
    {
        setCurrentItem(arg0);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view)
    {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition)
    {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item)
    {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }
  
    private Runnable mTabSelector;
    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }
    
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        mOnPageChangeListener=listener;
    }

    @Override
    public void notifyDataSetChanged()
    {
        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        int count=adapter.getCount();
        for(int i=0;i<count;i++){
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

   
    
    private void addTab(int index, CharSequence text) {
        /**
         * tab 标题栏样式
         */
        TabView tabView = new TabView(getContext());
        tabView.index=index;
        tabView.setText(text);
        tabView.setOnClickListener(mTabOnClickListener);
//        final TabView tabView = new TabView(getContext());
//        tabView.mIndex = index;
//        tabView.setFocusable(true);
//        tabView.setOnClickListener(mTabClickListener);
//        tabView.setText(text);

//        if (iconResId != 0) {
//            titleView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
//        }
        mTabLayout.addView(tabView);
    }
    
    private OnClickListener mTabOnClickListener=new OnClickListener()
    {
        
        @Override
        public void onClick(View view)
        {
            TabView tabView = (TabView)view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
//            if (oldSelected == newSelected && mTabReselectedListener != null) {
//                mTabReselectedListener.onTabReselected(newSelected);
//            }
        }
    };
    
    
    
    /**
     * 
     * <Tab标签>
     * <功能详细描述>
     * 
     * @author  Administrator
     * @version  [版本号, 2014-5-11]
     * @see  [相关类/方法]
     * @since  [产品/模块版本]
     */
    private class TabView extends LinearLayout{
        /**
         * 位置
         */
        private int index;
        
        /**
         * 游标高度
         */
        private static final int CURSOR_HEIGHT = 4;
        
        /**
         * 游标高度
         */
        private static final int MARGIN = 20;
        private TextView tabTitle;
        private ImageView tabCurosr;
        private int width;
        
        public TabView(Context context)
        {
            super(context);
            View view=LayoutInflater.from(getContext()).inflate(R.layout.tabwidget, this);
            tabTitle=(TextView)view.findViewById(R.id.tabTitle);
            tabCurosr=(ImageView)view.findViewById(R.id.cursor);
        }
        
        public void setText(CharSequence text){
            tabTitle.setText(text);
        }

        public int getIndex()
        {
            return index;
        }

        

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            // TODO Auto-generated method stub
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public void setSelected(boolean selected)
        {
            super.setSelected(selected);
            if(selected){
                tabTitle.setTextColor(Color.parseColor("#e9573d"));
                tabCurosr.setVisibility(View.VISIBLE);
            }else{
                tabTitle.setTextColor(Color.parseColor("#646464")); 
                tabCurosr.setVisibility(View.INVISIBLE);
            }
        }
        
        
    }
    
   
}
