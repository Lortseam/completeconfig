package me.lortseam.completeconfig.util;

import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import io.leangen.geantyref.GenericTypeReflector;
import lombok.experimental.UtilityClass;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public final class ReflectionUtils {

    private static final Map<Field, Method> writeMethodsCache = new HashMap<>();

    public static Class<?> getTypeClass(Type type) {
        return TypeToken.of(type).getRawType();
    }

    public static Type getFieldType(Field field) {
        return GenericTypeReflector.getExactFieldType(field, field.getDeclaringClass());
    }

    public static <T> T instantiateClass(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance();
    }

    public static Optional<Method> getWriteMethod(Field field) throws IntrospectionException {
        if (writeMethodsCache.containsKey(field)) {
            return Optional.ofNullable(writeMethodsCache.get(field));
        }
        Optional<Method> writeMethod = Arrays.stream(Introspector.getBeanInfo(field.getDeclaringClass()).getPropertyDescriptors()).filter(property -> {
            return property.getName().equals(field.getName());
        }).collect(MoreCollectors.toOptional()).map(PropertyDescriptor::getWriteMethod);
        writeMethodsCache.put(field, writeMethod.orElse(null));
        return writeMethod;
    }

}
