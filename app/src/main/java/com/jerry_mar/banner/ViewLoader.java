package com.jerry_mar.banner;

import android.view.View;

public interface ViewLoader<T> {
    void attach(T data, View view);

    View createView(int index);
}
