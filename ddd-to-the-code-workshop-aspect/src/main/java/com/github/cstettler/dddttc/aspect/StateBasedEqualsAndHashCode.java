package com.github.cstettler.dddttc.aspect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StateBasedEqualsAndHashCode {

    private StateBasedEqualsAndHashCode() {
    }

    public static boolean stateBasedEquals(Object object, Object other) {
        if (object == other) {
            return true;
        }

        if (other == null || object.getClass() != other.getClass()) {
            return false;
        }

        return Objects.equals(stateProperties(object), stateProperties(other));
    }

    public static int stateBasedHashCode(Object object) {
        return Objects.hash(stateProperties(object));
    }

    private static Map<String, Object> stateProperties(Object object) {
        try {
            // TODO for value objects, consider all declared fields in hierarchy
            Map<String, Object> stateProperties = new HashMap<>();
            Field[] declaredFields = object.getClass().getDeclaredFields();

            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);

                stateProperties.put(declaredField.getName(), value);
            }

            return stateProperties;
        } catch (Exception e) {
            throw new IllegalStateException("unable to get state properties of object '" + object + "'", e);
        }
    }

}
