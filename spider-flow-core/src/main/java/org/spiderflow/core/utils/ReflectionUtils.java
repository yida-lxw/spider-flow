package org.spiderflow.core.utils;

import java.lang.reflect.Field;

/**
 * @author yida
 * @package com.witarchive.common.utils
 * @date 2023-04-27 14:01
 * @description Java反射操作工具类
 */
public class ReflectionUtils {
    /**
     * @description 通过反射获取对象指定属性的值
     * @author yida
     * @date 2023-04-27 14:19:23
     * @param entity
     * @param fieldName
     *
     * @return {@link P}
     */
    public static <P, T>P getFieldValue(T entity, String fieldName) {
        try {
            Class<T> entityClass = (Class<T>) entity.getClass();
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (P)field.get(entity);
        } catch (Exception e) {
            return null;
        }
    }

    public static <P, T>T setFieldValue(T entity, String fieldName, P fieldValue) {
        try {
            Class<T> entityClass = (Class<T>) entity.getClass();
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, fieldValue);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
