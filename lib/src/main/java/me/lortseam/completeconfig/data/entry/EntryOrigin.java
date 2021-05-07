package me.lortseam.completeconfig.data.entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

@RequiredArgsConstructor
public final class EntryOrigin {

    @Getter
    protected final Field field;
    @Getter
    private final ConfigContainer parentObject;
    @Getter
    private final TranslationIdentifier parentTranslation;

    public Type getType() {
        return ReflectionUtils.getFieldType(field);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (!field.isAnnotationPresent(annotationType)) {
            throw new IllegalStateException("Missing required transformation annotation: " + annotationType);
        }
        return field.getDeclaredAnnotation(annotationType);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    public <A extends Annotation> Optional<A> getOptionalAnnotation(Class<A> annotationType) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationType));
    }

}
