package me.lortseam.completeconfig.data.entry;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation<O extends EntryOrigin> {

    public static Transformation.Builder<EntryOrigin> by(Predicate<EntryBase<?>> predicate) {
        return new Transformation.Builder<>(EntryOrigin::new).and(predicate);
    }

    public static Transformation.Builder<EntryOrigin> byType(Type... types) {
        return new Transformation.Builder<>(EntryOrigin::new).andType(types);
    }

    public static <A extends Annotation> Transformation.Builder<AnnotatedEntryOrigin<A>> byAnnotation(Class<A> annotationClass) {
        return new Transformation.Builder<>((field, parentObject, parentTranslation) -> {
            return new AnnotatedEntryOrigin<>(field, parentObject, parentTranslation, field.getDeclaredAnnotation(annotationClass));
        }).and(base -> base.getField().isAnnotationPresent(annotationClass));
    }

    private final Predicate<EntryBase<?>> predicate;
    private final OriginCreator<O> originCreator;
    private final Transformer<O> transformer;

    public boolean test(EntryBase<?> base) {
        return predicate.test(base);
    }

    public Entry<?> transform(EntryBase<?> base, ConfigContainer parentObject, TranslationIdentifier parentTranslation) {
        return transformer.transform(originCreator.create(base.getField(), parentObject, parentTranslation));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder<O extends EntryOrigin> {

        private final OriginCreator<O> originCreator;
        private Predicate<EntryBase<?>> predicate;

        public Builder<O> and(Predicate<EntryBase<?>> predicate) {
            if (this.predicate == null) {
                this.predicate = predicate;
            } else {
                this.predicate = this.predicate.and(predicate);
            }
            return this;
        }

        public Builder<O> andType(Type... types) {
            if (types.length == 0) {
                throw new IllegalArgumentException("Types must not be empty");
            }
            return and(base -> ArrayUtils.contains(types, base.getType()));
        }

        public Transformation<O> transforms(Transformer<O> transformer) {
            return new Transformation<>(predicate, originCreator, transformer);
        }

    }

    @FunctionalInterface
    private interface OriginCreator<O extends EntryOrigin> {

        O create(Field field, ConfigContainer parentObject, TranslationIdentifier parentTranslation);

    }

}
