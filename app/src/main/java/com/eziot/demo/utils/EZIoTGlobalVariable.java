/*
 *
 */
package com.eziot.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import androidx.core.util.Supplier;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * 全局变量管理
 *
 * @author yulinze
 * @date 2016/6/18
 */
public class EZIoTGlobalVariable<T> {
    /**
     * 定义区
     */
//    public final static EZIoTGlobalVariable<String> API_DOMAIN = new EZIoTGlobalVariable<String>(1, true, String.class, "");
    public final static EZIoTGlobalVariable<String> API_DOMAIN = new EZIoTGlobalVariable<String>(1, true, String.class, "");
    public final static EZIoTGlobalVariable<String> APP_ID = new EZIoTGlobalVariable<String>(2, true, String.class, "");
    public final static EZIoTGlobalVariable<String> LOCAL_CATCH = new EZIoTGlobalVariable<String>(3, true, String.class, "");
    /**
     * 代码实现区
     */
    private static Gson mGson;
    private static SparseArray<Object> values;
    private static SharedPreferences sharedPreferences;
    private static HashMap<String , SharedPreferences> sharedPreferencesMap;

    // 缓存到本地
    private boolean cacheInLocal;
    private Class<T> type;
    private Object defaultValue;
    private int index;
    private static Context mContext;
    private Supplier<String> spName;
    /**
     * 定义
     *
     * @param index 索引（勿重复）
     * @param cacheInLocal 是否保存到本地
     * @param type 类型
     * @param defaultValue 默认值
     */
    private EZIoTGlobalVariable(int index, boolean cacheInLocal, Class<T> type, T defaultValue , Supplier<String> spName) {
        this.cacheInLocal = cacheInLocal;
        this.type = type;
        this.defaultValue = defaultValue;
        this.index = index;
        this.spName = spName;
    }

    /**
     * 定义
     *
     * @param index 索引（勿重复）
     * @param cacheInLocal 是否保存到本地
     * @param type 类型
     * @param defaultValue 默认值
     */
    private EZIoTGlobalVariable(int index, boolean cacheInLocal, Class<T> type, T defaultValue) {
        this.cacheInLocal = cacheInLocal;
        this.type = type;
        this.defaultValue = defaultValue;
        this.index = index;
    }

    public static void init(Context context) {
        if (values == null){
            values = new SparseArray<>();
            mGson = new Gson();
            sharedPreferences = context.getApplicationContext().getSharedPreferences("EZIOT_PREFERENCE_NAME_DEMO", 0);
            sharedPreferencesMap = new HashMap<>();
            mContext = context;
        }
    }

    /**
     * 取值
     */
    public T get() {
        String key = getKey();
        Object result = values.get(index);
        if (result == null && cacheInLocal) {
            if (type == String.class) {
                result = getSharedPreferences().getString(key, (String) defaultValue);
            } else if (type == int.class || type == Integer.class) {
                result = getSharedPreferences().getInt(key, (int) defaultValue);
            } else if (type == long.class || type == Long.class) {
                result = getSharedPreferences().getLong(key, (long) defaultValue);
            } else if (type == boolean.class || type == Boolean.class) {
                result = getSharedPreferences().getBoolean(key, (boolean) defaultValue);
            } else if (type == float.class || type == Float.class) {
                result = getSharedPreferences().getFloat(key, (float) defaultValue);
            } else {
                String json = getSharedPreferences().getString(key, null);
                result = json == null ? null : mGson.fromJson(json, type);
            }
        }

        if (result == null) {
            set((T) defaultValue);
            return (T) defaultValue;
        } else {
            return (T) result;
        }
    }

    /**
     * 设值
     *
     * @param value 值
     */
    public void set(T value) {
        String key = getKey();
        if (value == null) {
            values.remove(index);
            if (cacheInLocal) {
                getSharedPreferences().edit().remove(key).apply();
            }
        } else {
            if (value.getClass() != type) {
                throw new IllegalArgumentException("value type is not correct");
            }

            values.put(index, value);
            if (cacheInLocal) {
                SharedPreferences.Editor editor = getSharedPreferences().edit();
                if (type == String.class) {
                    editor.putString(key, (String) value);
                } else if (type == int.class || type == Integer.class) {
                    editor.putInt(key, (Integer) value);
                } else if (type == long.class || type == Long.class) {
                    editor.putLong(key, (Long) value);
                } else if (type == boolean.class || type == Boolean.class) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (type == float.class || type == Float.class) {
                    editor.putFloat(key, (Float) value);
                } else {
                    editor.putString(key, mGson.toJson(value));
                }
                editor.apply();
            }
        }
    }

    public SharedPreferences getSharedPreferences(){
        if(spName != null){
            SharedPreferences sharedPreferences = sharedPreferencesMap.get(spName.get());
            if(sharedPreferences == null){
                SharedPreferences sharedPreferences1 = mContext.getApplicationContext().getSharedPreferences(spName.get(), 0);
                sharedPreferencesMap.put(spName.get(),sharedPreferences1);
                return sharedPreferences1;
            } else {
                return sharedPreferences;
            }
        }
        return sharedPreferences;
    }

    /**
     * 移除
     */
    public void remove() {
        set(null);
    }

    public void removeMemoryCache() {
        if(values != null){
            values.remove(index);
        }
    }

    private String getKey() {
        return "SP_KEY_" + index;
    }
}