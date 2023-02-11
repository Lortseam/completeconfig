package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * The origin of a config entry.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class EntryOrigin {

    @Getter(AccessLevel.PACKAGE)
    private final Config root;
    @Getter(AccessLevel.PACKAGE)
    private final Parent parent;
    @Getter
    private final Class<? extends ConfigContainer> declaringClass;
    @Getter
    @EqualsAndHashCode.Include
    private final ConfigContainer object;
    @Getter
    @EqualsAndHashCode.Include
    private final Field field;
    @Getter
    private final Type type;
    @Getter
    private final Type[] genericTypes;
    @Getter
    private final ConfigContainer container;

    EntryOrigin(Config root, Parent parent, Field field, ConfigContainer container) {
        this.root = root;
        this.parent = parent;
        this.field = field;
        this.container = container;
        declaringClass = (Class<? extends ConfigContainer>) field.getDeclaringClass();
        object = Modifier.isStatic(field.getModifiers()) ? null : container;
        type = ReflectionUtils.getFieldType(field);
        genericTypes = ReflectionUtils.getFieldGenericTypes(field);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        A annotation = field.getDeclaredAnnotation(annotationType);
        if (annotation == null) {
            throw new RuntimeException("Missing required transformation annotation: " + annotationType);
        }
        return annotation;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    public <A extends Annotation> Optional<A> getOptionalAnnotation(Class<A> annotationType) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationType));
    }

    public Optional<ConfigEntry> getMainAnnotation() {
        return getOptionalAnnotation(ConfigEntry.class);
    }

    public Optional<ConfigEntries> getClassAnnotation() {
        return Optional.ofNullable(declaringClass.getDeclaredAnnotation(ConfigEntries.class));
    }

}
