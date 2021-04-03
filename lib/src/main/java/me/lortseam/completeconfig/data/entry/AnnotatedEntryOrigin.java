package me.lortseam.completeconfig.data.entry;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotatedEntryOrigin<A extends Annotation> extends EntryOrigin {

    @Getter
    private final A annotation;

    AnnotatedEntryOrigin(Field field, ConfigContainer parentObject, TranslationIdentifier parentTranslation, Class<A> annotationType) {
        super(field, parentObject, parentTranslation);
        annotation = field.getAnnotation(annotationType);
    }

}
