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
    @EqualsAndHashCode.Include
    private final Field field;
    @Getter
    private final Type type;
    @Getter
    private final ConfigContainer container;

    EntryOrigin(Config root, Parent parent, Field field, ConfigContainer container) {
        this.root = root;
        this.parent = parent;
        this.field = field;
        type = ReflectionUtils.getFieldType(field);
        this.container = container;
    }

    public Class<? extends ConfigContainer> getDeclaringClass() {
        return (Class<? extends ConfigContainer>) field.getDeclaringClass();
    }

    @EqualsAndHashCode.Include
    public ConfigContainer getObject() {
        return Modifier.isStatic(field.getModifiers()) ? null : container;
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
        return getOptionalAnnotation(ConfigEntries.class);
    }

}
