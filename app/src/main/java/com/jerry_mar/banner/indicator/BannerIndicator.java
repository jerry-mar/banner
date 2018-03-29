package com.jerry_mar.banner.indicator;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class BannerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private ViewPager view;
    private int count;
    private Indicator indicator;
    private Adapter adapter;
    private CountObserver observer = new CountObserver(this);

    public BannerIndicator(Context context) {
        super(context);
    }

    public BannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBannerLayout(ViewPager view) {
        while(this.view != view){
            PagerAdapter adapter = view.getAdapter();
            if (adapter == null) {
                throw new IllegalStateException("ViewPager does not have adapter instance.");
            }
            this.view = view;
            this.view.addOnPageChangeListener(this);
            adapter.registerDataSetObserver(observer);
            break;
        }
    }

    public void redo(){
        int page = view.getAdapter().getItemPosition(this);
        if(page >= 0) {
            count = page;
        } else {
            count = view.getAdapter().getCount();
        }
        int childCount = getChildCount();
        boolean isAdded = count > childCount;
        if(indicator != null) {
            indicator.select(false);
            indicator = null;
        }

        for(int i = 0; i < Math.abs(count - childCount); i++){
            if(isAdded){
                addView(adapter.getView());
            }else{
                removeViewAt(0);
            }
        }
        setCurrentItem(0);
    }

    public void setCurrentItem(int index) {
        View view = getChildAt(index);
        if(view != null) {
            if (indicator != null)
                indicator.select(false);
            indicator = (Indicator) view;
            indicator.select(true);
        }
    }

    public void setAdapter(Adapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position % count);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public interface Adapter{
        View getView();
    }

    static class CountObserver extends DataSetObserver {
        private BannerIndicator layout;
        public CountObserver(BannerIndicator layout) {
            this.layout = layout;
        }
        @Override
        public void onChanged() {
            layout.redo();
        }
    }
}
