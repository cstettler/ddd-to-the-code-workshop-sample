package com.github.cstettler.dddttc.support;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.Map;

public class ReflectionUtils {

    private static final ObjenesisStd OBJENESIS = new ObjenesisStd();

    private ReflectionUtils() {
    }

    public static <T> T newInstance(Class<T> type) {
        return OBJENESIS.newInstance(type);
    }

    public static <T> T newInstance(Class<T> type, Map<String, Object> properties) {
        T instance = newInstance(type);
        properties.forEach((propertyName, propertyValue) -> applyProperty(instance, propertyName, propertyValue));

        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T> T propertyValue(Object target, String propertyName) {
        try {
            Field propertyField = target.getClass().getDeclaredField(propertyName);
            propertyField.setAccessible(true);
            Object propertyValue = propertyField.get(target);

            return (T) propertyValue;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("property '" + propertyName + "' not found on target '" + target + "'");
        } catch (Exception e) {
            throw new IllegalStateException("unable to get value of property '" + propertyName + "' on target '" + target + "'", e);
        }
    }

    public static void applyProperty(Object target, String propertyName, Object propertyValue) {
        try {
            Field propertyField = target.getClass().getDeclaredField(propertyName);
            propertyField.setAccessible(true);
            propertyField.set(target, propertyValue);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("property '" + propertyName + "' not found on target '" + target + "'");
        } catch (Exception e) {
            throw new IllegalStateException("unable to set value of property '" + propertyName + "' on target '" + target + "'", e);
        }
    }

}
