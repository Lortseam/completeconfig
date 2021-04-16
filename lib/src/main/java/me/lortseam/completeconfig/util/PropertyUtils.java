package me.lortseam.completeconfig.util;

import com.google.common.collect.MoreCollectors;
import lombok.experimental.UtilityClass;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public final class PropertyUtils {

    private final Map<Field, Method> writeMethodsCache = new HashMap<>();

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
