package com.ykren.fastdfs.common;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 工具类
 *
 * @author ykren
 * @date 2022/1/23
 */
public final class CodeUtils {
    private CodeUtils() {
    }

    public static void validateFile(File file) {
        if (!checkFile(file)) {
            throw new IllegalArgumentException("Illegal file path: " + file.getPath());
        }
    }

    public static boolean checkFile(File file) {
        if (file == null) {
            return false;
        }

        boolean exists = false;
        boolean isFile = false;
        boolean canRead = false;
        try {
            exists = file.exists();
            isFile = file.isFile();
            canRead = file.canRead();
        } catch (SecurityException se) {
            // Swallow the exception and return false directly.
            return false;
        }
        return (exists && isFile && canRead);
    }

    public static void validateNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException(argName + " must not be null.");
        }
    }

    public static void validateNotBlankString(String arg, String argName) {
        validateNotNull(arg, argName);
        if (arg.isEmpty()) {
            throw new IllegalArgumentException(argName + " must not be a empty string.");
        }
    }

    public static void validateNotLessZero(Number arg, String argName) {
        if (arg == null || arg.longValue() < 0) {
            throw new IllegalArgumentException(argName + " must not be < 0 ");
        }
    }


    public static void validateGreaterZero(Number arg, String argName) {
        if (arg == null || arg.longValue() <= 0) {
            throw new IllegalArgumentException(argName + " must be > 0 ");
        }
    }

    public static <T> void validateCollectionNotEmpty(Collection<T> collection, String argName) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(argName + " must not be null or empty ");
        }
    }


    /**
     * 获取本类及其父类的属性的方法
     *
     * @param clazz 当前类对象
     * @return 字段集合
     */
    public static List<Field> getAllFieldList(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

}
