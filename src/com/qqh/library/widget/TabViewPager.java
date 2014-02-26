package com.qqh.library.widget;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import net.tsz.afinal.R;

import java.util.ArrayList;

/**
 * Created by qiuqiaohua on 2/11/14.
 * 使用该组件时,调用的Activity必须为ActivityGroup
 */
public class TabViewPager extends LinearLayout {

    private Context mContext;

    private View mainView;

    private TabHost tabHost;

    private ViewPager viewPager;

    private LocalActivityManager manager;

    private ArrayList<View> listViews;


    public TabViewPager(Context context) {
        this(context, null);
    }

    public TabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.tab_viewpager, this);
        findView();
    }

    private void findView() {
        tabHost = (TabHost) mainView.findViewById(R.id.tabHost);
        tabHost.setup();
        manager = ((ActivityGroup) mContext).getLocalActivityManager();
        tabHost.setup(manager);
        tabHost.setOnTabChangedListener(tabChangeListener);
        viewPager = (ViewPager) mainView.findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(pageChangeListener);

    }

    /**
     * 添加页卡和内容
     * @param titles
     * @param activity
     */
    public void addTab(String[] titles, Class[] activity) {
        listViews = new ArrayList<View>();
        for (int i = 0; i < titles.length; i++) {
            String s = titles[i];
            Class a = activity[i];
            Intent intent = new Intent(mContext, a);
            listViews.add(manager.startActivity(i + "", intent).getDecorView());
            LinearLayout tabWidget = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tabwidget, null);
            TextView title = (TextView) tabWidget.findViewById(R.id.tabTitle);
            title.setText(s);
            tabHost.addTab(tabHost.newTabSpec("tab" + i).setIndicator(tabWidget).setContent(intent));
        }

        viewPager.setAdapter(new MyPagerAdapter(listViews));

        viewPager.setOffscreenPageLimit(3);//设置viewPager 默认加载几页
        tabHost.setCurrentTab(0);
    }


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            TabWidget widget = tabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            tabHost.setCurrentTab(i);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            int position = tabHost.getCurrentTab();
            viewPager.setCurrentItem(position);
        }
    };


    private class MyPagerAdapter extends PagerAdapter {

        private ArrayList<View> views;

        private MyPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }
    }

}
