package com.github.cstettler.dddttc.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static com.github.cstettler.dddttc.support.ReflectionUtils.newInstance;

public abstract class ReflectionBasedStateBuilder<T> {

    private static final Random RANDOM = new SecureRandom();
    private static final char[] RANDOM_STRING_SYMBOLS = randomStringSymbols();

    private Map<String, Object> properties;

    protected ReflectionBasedStateBuilder() {
        this.properties = new LinkedHashMap<>();
    }

    protected <B extends ReflectionBasedStateBuilder<T>> B recordProperty(B builder, String propertyName, Object propertyValue) {
        this.properties.put(propertyName, propertyValue);

        return builder;
    }

    public T build() {
        return newInstance(objectType(), this.properties);
    }

    @SuppressWarnings("unchecked")
    private Class<T> objectType() {
        Type genericSuperclass = getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }

        throw new IllegalStateException("unable to determine object type via generic type parameter of class '" + getClass().getName() + "'");
    }

    protected static String randomString(int length) {
        char[] buffer = new char[length];

        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = RANDOM_STRING_SYMBOLS[RANDOM.nextInt(RANDOM_STRING_SYMBOLS.length)];
        }

        return new String(buffer);
    }

    private static char[] randomStringSymbols() {
        String upperCaseSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseSymbols = upperCaseSymbols.toLowerCase();

        return (upperCaseSymbols + lowerCaseSymbols).toCharArray();
    }

}
