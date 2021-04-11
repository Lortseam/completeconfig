package me.lortseam.completeconfig.data.entry;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation {

    private static final Set<Class<Annotation>> registeredAnnotations = new HashSet<>();

    public static Transformation.Builder builder() {
        return new Transformation.Builder();
    }

    private final Predicate<Entry.Draft<?>> predicate;
    private final Transformer transformer;

    public boolean test(Entry.Draft<?> base) {
        return predicate.test(base);
    }

    public Entry<?> transform(Entry.Draft<?> draft, ConfigContainer parentObject, TranslationIdentifier parentTranslation) {
        return transformer.transform(originCreator.create(draft.getField(), parentObject, parentTranslation));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private Predicate<Entry.Draft<?>> predicate;

        private Builder by(Predicate<Entry.Draft<?>> predicate) {
            if (this.predicate == null) {
                this.predicate = predicate;
            } else {
                this.predicate = this.predicate.and(predicate);
            }
            return this;
        }

        public Builder byType(Type... types) {
            return byType(type -> ArrayUtils.contains(types, type));
        }

        public Builder byTypeClass(Predicate<Class<?>> typeClassPredicate) {
            return by(draft -> typeClassPredicate.test(draft.getTypeClass()));
        }

        public Builder byType(Predicate<Type> typePredicate) {
            return by(draft -> typePredicate.test(draft.getType()));
        }

        public Builder byAnnotation(Class<Annotation>... annotations) {
            registeredAnnotations.addAll(Arrays.asList(annotations));
            return by(draft -> {
                for (Class<Annotation> annotationType : annotations) {
                    if (!draft.getField().isAnnotationPresent(annotationType)) {
                        return false;
                    }
                }
                return true;
            });
        }

        public Transformation transforms(Transformer transformer) {
            return new Transformation(predicate, transformer);
        }

    }

    @FunctionalInterface
    private interface OriginCreator<O extends EntryOrigin> {

        O create(Field field, ConfigContainer parentObject, TranslationIdentifier parentTranslation);

    }

}
