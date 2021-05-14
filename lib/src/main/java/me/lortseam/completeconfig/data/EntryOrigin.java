package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class EntryOrigin {

    @Getter(AccessLevel.PACKAGE)
    private final BaseCollection parent;
    @Getter
    @EqualsAndHashCode.Include
    private final Field field;
    @Getter
    private final Type type;
    @Getter
    @EqualsAndHashCode.Include
    private final ConfigContainer object;

    EntryOrigin(BaseCollection parent, Field field, ConfigContainer object) {
        this.parent = parent;
        this.field = field;
        type = ReflectionUtils.getFieldType(field);
        this.object = object;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        A annotation = field.getDeclaredAnnotation(annotationType);
        if (annotation == null) {
            throw new IllegalStateException("Missing required transformation annotation: " + annotationType);
        }
        return annotation;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    public <A extends Annotation> Optional<A> getOptionalAnnotation(Class<A> annotationType) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationType));
    }

}
