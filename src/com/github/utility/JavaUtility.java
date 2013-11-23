package com.github.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavaUtility {
    public static Field getField(Class<?> cl, String fieldName) {
        for (Field f : cl.getFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    public static String getLastMethodCalls(Thread thread, int number) {
        StackTraceElement[] stackTraceElements = thread.getStackTrace();
        StringBuilder sb = new StringBuilder();
        int start = 2;
        for (int i = start; i <= number + start - 1; i++) {
            sb.append(stackTraceElements[i] + "\n");
        }
        return sb.toString();
    }

    public static Method getMethod(Class<?> cl, String methodName) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    public static <T> T getObject(Class<T> type, Object cast) {
        if (type.isInstance(cast)) {
            return type.cast(cast);
        } else {
            return null;
        }
    }
}
