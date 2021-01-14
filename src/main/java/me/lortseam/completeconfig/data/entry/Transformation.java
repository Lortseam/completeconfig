package me.lortseam.completeconfig.data.entry;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation<O extends EntryOrigin> {

    public static Transformation<EntryOrigin> of(Predicate<EntryBase<?>> predicate, Transformer<EntryOrigin> transformer) {
        return new Transformation<>(predicate, EntryOrigin::new, transformer);
    }

    public static Transformation<EntryOrigin> ofType(Type type, Transformer<EntryOrigin> transformer) {
        return of(base -> base.getType().equals(type), transformer);
    }

    public static <A extends Annotation> Transformation<AnnotatedEntryOrigin<A>> ofAnnotation(Class<A> annotationClass, Transformer<AnnotatedEntryOrigin<A>> transformer, Predicate<EntryBase<?>> predicate) {
        return new Transformation<>(base -> base.getField().isAnnotationPresent(annotationClass) && predicate.test(base), (field, parentObject, parentTranslation) -> new AnnotatedEntryOrigin<>(field, parentObject, parentTranslation, field.getDeclaredAnnotation(annotationClass)), transformer);
    }

    public static <A extends Annotation> Transformation<AnnotatedEntryOrigin<A>> ofAnnotation(Class<A> annotationClass, Transformer<AnnotatedEntryOrigin<A>> transformer, Type... types) {
        return ofAnnotation(annotationClass, transformer, base -> {
            if (types.length > 0 && !ArrayUtils.contains(types, base.getType())) {
                throw new IllegalAnnotationTargetException("Cannot apply annotation " + annotationClass + " to field " + base.getField());
            }
            return true;
        });
    }

    private final Predicate<EntryBase<?>> predicate;
    private final OriginCreator<O> originCreator;
    private final Transformer<O> transformer;

    public boolean test(EntryBase<?> base) {
        return predicate.test(base);
    }

    public Entry<?> transform(EntryBase<?> base, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
        return transformer.transform(originCreator.create(base.getField(), parentObject, parentTranslation));
    }

    @FunctionalInterface
    private interface OriginCreator<O extends EntryOrigin> {

        O create(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation);

    }

}
