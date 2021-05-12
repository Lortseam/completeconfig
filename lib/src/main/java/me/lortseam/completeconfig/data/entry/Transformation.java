package me.lortseam.completeconfig.data.entry;

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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation {

    private static final Set<Class<? extends Annotation>> registeredAnnotations = new HashSet<>();

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

        public Builder byType(Type... types) {
            return byType(type -> ArrayUtils.contains(types, type));
        }

        public Builder byType(Predicate<Type> typePredicate) {
            return by(origin -> typePredicate.test(origin.getType()));
        }

        public Builder byAnnotation(Class<? extends Annotation> annotation, boolean optional) {
            registeredAnnotations.add(annotation);
            (optional ? optionalAnnotations : requiredAnnotations).add(annotation);
            return this;
        }

        public Builder byAnnotation(Class<? extends Annotation> annotation) {
            return byAnnotation(annotation, false);
        }

        public Builder byAnnotation(Iterable<Class<? extends Annotation>> annotations) {
            for (Class<? extends Annotation> annotation : annotations) {
                byAnnotation(annotation);
            }
            return this;
        }

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
