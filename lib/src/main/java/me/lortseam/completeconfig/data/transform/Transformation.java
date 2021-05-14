package me.lortseam.completeconfig.data.transform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.EntryOrigin;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A transformation is used to transform a field to an {@link me.lortseam.completeconfig.data.Entry}. This class stores
 * a predicate, which a field has to fulfill, and a {@link Transformer}, which performs the actual transformation
 * process.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation {

    private static final Set<Class<? extends Annotation>> registeredAnnotations = new HashSet<>();

    /**
     * Creates a new transformation builder.
     *
     * @return a new transformation builder
     */
    public static Transformation.Builder builder() {
        return new Transformation.Builder();
    }

    private final Predicate<EntryOrigin> predicate;
    @Getter
    private final Transformer transformer;

    public boolean test(EntryOrigin origin) {
        return predicate.test(origin);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private Predicate<EntryOrigin> predicate;
        private final Set<Class<? extends Annotation>> requiredAnnotations = new HashSet<>();
        private final Set<Class<? extends Annotation>> optionalAnnotations = new HashSet<>();

        private Builder by(Predicate<EntryOrigin> predicate) {
            if (this.predicate == null) {
                this.predicate = predicate;
            } else {
                this.predicate = this.predicate.and(predicate);
            }
            return this;
        }

        /**
         * Filters by field type.
         *
         * @param types the valid field types
         * @return this builder
         */
        public Builder byType(Type... types) {
            return byType(type -> ArrayUtils.contains(types, type));
        }

        /**
         * Filters by a predicate based on the field type.
         *
         * @param typePredicate a predicate returning {@code true} for valid types
         * @return this builder
         */
        public Builder byType(Predicate<Type> typePredicate) {
            return by(origin -> typePredicate.test(origin.getType()));
        }

        /**
         * Filters by an annotation type. The annotation may be required or optional.
         *
         * @param annotation the annotation type
         * @param optional whether the annotation is optional
         * @return this builder
         */
        public Builder byAnnotation(Class<? extends Annotation> annotation, boolean optional) {
            registeredAnnotations.add(annotation);
            (optional ? optionalAnnotations : requiredAnnotations).add(annotation);
            return this;
        }

        /**
         * Filters by an annotation type. The annotation is required.
         *
         * @param annotation the annotation type
         * @return this builder
         *
         * @see Transformation.Builder#byAnnotation(Class, boolean)
         */
        public Builder byAnnotation(Class<? extends Annotation> annotation) {
            return byAnnotation(annotation, false);
        }

        /**
         * Filters by multiple annotation types. All annotations are required.
         *
         * @param annotations the annotation types
         * @return this builder
         */
        public Builder byAnnotation(Iterable<Class<? extends Annotation>> annotations) {
            for (Class<? extends Annotation> annotation : annotations) {
                byAnnotation(annotation);
            }
            return this;
        }

        /**
         * Sets the transformer and creates the {@link Transformation} object.
         *
         * @param transformer the transformer
         * @return the created transformation
         */
        public Transformation transforms(Transformer transformer) {
            if (predicate == null && requiredAnnotations.isEmpty()) {
                throw new IllegalStateException("Missing transformation filter");
            }
            by(origin -> {
                Set<Class<? extends Annotation>> declaredAnnotations = Arrays.stream(origin.getField().getDeclaredAnnotations()).map(Annotation::annotationType).filter(registeredAnnotations::contains).collect(Collectors.toSet());
                for (Class<? extends Annotation> requiredAnnotation : requiredAnnotations) {
                    if (!declaredAnnotations.remove(requiredAnnotation)) return false;
                }
                return optionalAnnotations.containsAll(declaredAnnotations);
            });
            return new Transformation(predicate, transformer);
        }

    }

}
