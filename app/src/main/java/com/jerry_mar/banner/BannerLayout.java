package com.jerry_mar.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.jerry_mar.banner.adapter.BannerAdapter;
import com.jerry_mar.banner.adapter.BannerScroller;

import java.lang.reflect.Field;
import java.util.List;

public class BannerLayout extends ViewPager implements Handler.Callback {
    final static int INTERVAL = 1000;

    private long time;
    private float scale;
    private boolean auto;
    private boolean vertical;

    private BannerScroller scroller;
    private Handler handler;
    private BannerAdapter adapter;
    private PageTransformer transformer;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
        handler = new Handler(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(swap(ev));
        swap(ev);
        return intercept;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swap(ev));
    }

    private MotionEvent swap(MotionEvent event) {
        if (vertical) {
            float width = getWidth();
            float height = getHeight();
            event.setLocation((event.getY() / height) * width,
                    (event.getX() / width) * height);
        }
        return event;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (size == 0 && mode == MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)
                    (MeasureSpec.getSize(widthMeasureSpec) * scale), MeasureSpec.EXACTLY);
        } else {
            size = MeasureSpec.getSize(widthMeasureSpec);
            mode = MeasureSpec.getMode(widthMeasureSpec);

            if (size == 0 && mode == MeasureSpec.EXACTLY) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int)
                        (MeasureSpec.getSize(heightMeasureSpec) * scale), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        if (transformer != null) {
            final int scrollX = getScrollX();
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.isDecor) continue;
                final float transformPos = (float) (child.getLeft() - getPaddingLeft() - scrollX) / measuredWidth();
                transformer.transformPage(child, transformPos);
            }
        }
    }

    private int measuredWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerLayout);
        time = a.getInt(R.styleable.BannerLayout_time, 2000);
        auto = a.getBoolean(R.styleable.BannerLayout_auto, false);
        vertical = a.getBoolean(R.styleable.BannerLayout_banner_vertical, false);
        scale = a.getFloat(R.styleable.BannerLayout_scale, 1.0F);
        a.recycle();

        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new BannerScroller(getContext(), (Interpolator) interpolatorField.get(this));
            interpolatorField.setAccessible(false);
            scroller.setDuration(INTERVAL);
            scrollerField.set(this, scroller);
            scrollerField.setAccessible(false);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }

        setAdapter(adapter = new BannerAdapter(auto));
    }

    public void setData(List<?> data) {
        adapter.setData(data);
    }

    public void setData(Object data) {
        adapter.setData(data);
    }

    public void setData(Object[] data) {
        adapter.setData(data);
    }

    public void setTransformer(Class<? extends PageTransformer> transformer) {
        try {
            this.transformer = transformer.newInstance();
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Please set the PageTransformer class");
        }
    }

    public void setLoader(ViewLoader loader) {
        adapter.setLoader(loader);
    }

    public void start() {
        start(time);
    }

    public void start(long delay) {
        time = delay;
        adapter.notifyDataSetChanged();
        if (adapter.isAuto()) {
            handler.removeMessages(INTERVAL);
            handler.sendEmptyMessageDelayed(INTERVAL, delay + INTERVAL);
        }
    }

    public void stop() {
        handler.removeMessages(INTERVAL);
    }

    public void clear() {
        stop();
        adapter.clear();
    }

    public void scroll() {
        PagerAdapter adapter = getAdapter();
        int currentItem = 1 + getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }

        if (currentItem == totalCount) {
            setCurrentItem(0, true);
        } else {
            setCurrentItem(currentItem, true);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case INTERVAL:
                scroll();
                handler.sendEmptyMessageDelayed(INTERVAL, time + INTERVAL);
        }
        return true;
    }
}
