package ru.max314.util;

import android.util.Log;

/**
 * Created by max on 31.10.2014.
 * Хелпер для логирования в качестве тега - имя класса
 */
public class LogHelper {
    private String tag;

    public LogHelper(Class<?> classForTag) {
        tag = classForTag.getName();
    }

    public LogHelper(String tag) {
        this.tag = tag;
    }

    public void d(String str) {
        Log.d(tag, str);
    }

    public void e(String str) {
        Log.e(tag, str);
    }

    public void e(String str, Throwable e) {
        Log.e(tag, str, e);
    }
}
