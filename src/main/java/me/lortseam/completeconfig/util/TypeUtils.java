package me.lortseam.completeconfig.util;

import com.google.common.reflect.TypeToken;
import io.leangen.geantyref.GenericTypeReflector;
import lombok.extern.log4j.Log4j2;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

@Log4j2
public final class TypeUtils {

    public static Type getFieldType(Field field) {
        return GenericTypeReflector.getExactFieldType(field, field.getDeclaringClass());
    }

    public static Class<?> getTypeClass(Type type) {
        return TypeToken.of(type).getRawType();
    }

    public static TypeSerializerCollection mergeSerializers(TypeSerializerCollection... typeSerializerCollections) {
        typeSerializerCollections = Arrays.stream(typeSerializerCollections).filter(Objects::nonNull).toArray(TypeSerializerCollection[]::new);
        switch (typeSerializerCollections.length) {
            case 0:
                logger.warn("Tried to merge non-existent type serializer collections!");
                return null;

            case 1:
                return typeSerializerCollections[0];

            default:
                TypeSerializerCollection.Builder builder = TypeSerializerCollection.builder();
                for (TypeSerializerCollection collection : typeSerializerCollections) {
                    builder.registerAll(collection);
                }
                return builder.build();
        }
    }

}
