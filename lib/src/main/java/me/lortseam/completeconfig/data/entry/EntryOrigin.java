package me.lortseam.completeconfig.data.entry;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public final class EntryOrigin {

    @Getter
    protected final Field field;
    @Getter
    private final ConfigContainer parentObject;
    @Getter
    private final TranslationIdentifier parentTranslation;

    public EntryOrigin(Entry.Draft<?> draft, ConfigContainer parentObject, TranslationIdentifier parentTranslation) {
        field = draft.getField();
        this.parentObject = parentObject;
        this.parentTranslation = parentTranslation;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return Objects.requireNonNull(field.getDeclaredAnnotation(annotationType), "Missing required transformation annotation");
    }

    public <A extends Annotation> Optional<A> getOptionalAnnotation(Class<A> annotationType) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationType));
    }

}
