package com.jerry_mar.banner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jerry_mar.banner.indicator.BannerIndicator;
import com.jerry_mar.banner.ViewLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private boolean auto;
    private int coped;
    private ViewLoader loader;
    private List<Object> data = new ArrayList<>();
    private List<View> scene = new ArrayList<>();

    public BannerAdapter(boolean auto) {
        this.auto = auto;
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof BannerIndicator) {
            return scene.size() - coped;
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return auto & scene.size() > 1 ? Integer.MAX_VALUE : scene.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = scene.get(position % scene.size());
        ViewParent parent = view.getParent();
        if (parent != null){
            ViewGroup parentView = (ViewGroup) parent;
            parentView.removeView(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        int size = data.size();
        int x = Integer.MAX_VALUE;
        if (size > 1 && size < 6 && auto) {
            x = size;
            if (size == 2) {
                size *= 3;
            } else {
                size *= 2;
            }
            coped = size - x;
        }
        for (int i = 0; i < size; i++) {
            View view = loader.createView(i % x);
            scene.add(view);
            loader.attach(data.get(i % x), view);
        }
        super.notifyDataSetChanged();
    }

    public void setData(List<?> data) {
        this.data.addAll(data);
    }

    public void setData(Object data) {
        this.data.add(data);
    }

    public void setData(Object[] data) {
        this.data.addAll(Arrays.asList(data));
    }

    public void clear() {
        data.clear();
        scene.clear();
        super.notifyDataSetChanged();
    }

    public void setLoader(ViewLoader loader) {
        this.loader = loader;
    }

    public boolean isAuto() {
        return data.size() > 1 && auto;
    }
}
