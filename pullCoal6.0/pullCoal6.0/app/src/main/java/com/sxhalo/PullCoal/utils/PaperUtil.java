package com.sxhalo.PullCoal.utils;

import java.util.List;

import io.paperdb.Paper;

/**
 * 数据存储
 *
 * @author Xiao_
 * @date 2019/4/8 0008
 */
public class PaperUtil {

    private static final String BOOK_NAME = "pullCoalBook";

    /**
     * 添加数据
     *
     * @param key   键
     * @param value 值
     */
    public static <T> void put(String key, T value) {
        Paper.book(BOOK_NAME).write(key, value);
    }

    /**
     * 根据key获取数据,无默认值
     *
     * @param key 键
     * @return 数据
     */
    public static <T> T get(String key) {
        return Paper.book(BOOK_NAME).read(key);
    }

    /**
     * 根据key获取数据,有默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 数据
     */
    public static <T> T get(String key, T defaultValue) {
        return Paper.book(BOOK_NAME).read(key, defaultValue);
    }

    /**
     * 根据key移除数据
     *
     * @param key 键
     */
    public static void remove(String key) {
        Paper.book(BOOK_NAME).delete(key);
    }


    /**
     * 是否存在此key
     *
     * @param key 键
     * @return true:存在;false:不存在
     */
    public static boolean contains(String key) {
        return Paper.book(BOOK_NAME).contains(key);
    }

    /**
     * 获取所有的key
     *
     * @return 所有key的集合
     */
    public static List<String> getAllKeys() {
        return Paper.book(BOOK_NAME).getAllKeys();
    }

    /**
     * 获取存储路径
     *
     * @return 路径
     */
    public static String getPath() {
        return Paper.book(BOOK_NAME).getPath();
    }

    /**
     * 获取指定key的存储路径
     *
     * @param key 键
     * @return 路径
     */
    public static String getPath(String key) {
        return Paper.book(BOOK_NAME).getPath(key);
    }
}
