package me.lortseam.completeconfig.data.transform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.util.ReflectionUtils;
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
public final class Transformation {

    private static final Set<Class<? extends Annotation>> registeredAnnotations = new HashSet<>();

    public static final Transformation[] DEFAULTS = new Transformation[] {
            new Transformation(filter().byType(boolean.class, Boolean.class).byAnnotation(ConfigEntry.Boolean.class, true), BooleanEntry::new),
            new Transformation(filter().byType(int.class, Integer.class).byAnnotation(ConfigEntry.BoundedInteger.class), origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(int.class, Integer.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedInteger.class, ConfigEntry.Slider.class)), origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(long.class, Long.class).byAnnotation(ConfigEntry.BoundedLong.class), origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(long.class, Long.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedLong.class, ConfigEntry.Slider.class)), origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(float.class, Float.class).byAnnotation(ConfigEntry.BoundedFloat.class), origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation(ConfigEntry.BoundedFloat.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(float.class, Float.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedFloat.class, ConfigEntry.Slider.class)), origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation(ConfigEntry.BoundedFloat.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(double.class, Double.class).byAnnotation(ConfigEntry.BoundedDouble.class), origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation(ConfigEntry.BoundedDouble.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(double.class, Double.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedDouble.class, ConfigEntry.Slider.class)), origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation(ConfigEntry.BoundedDouble.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            new Transformation(filter().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))), EnumEntry::new),
            new Transformation(filter().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))).byAnnotation(ConfigEntry.Dropdown.class), DropdownEntry::new),
            new Transformation(filter().byAnnotation(ConfigEntry.Color.class), origin -> new ColorEntry<>(origin, origin.getAnnotation(ConfigEntry.Color.class).alphaMode()))
    };

    /**
     * Creates a new transformation filter.
     *
     * @return a new transformation filter
     */
    public static Filter filter() {
        return new Filter();
    }

    public Transformation(Transformation.Filter filter, Transformer transformer) {
        predicate = filter.build();
        this.transformer = transformer;
    }

    private final Predicate<EntryOrigin> predicate;
    @Getter
    private final Transformer transformer;

    public boolean test(EntryOrigin origin) {
        return predicate.test(origin);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Filter {

        private Predicate<EntryOrigin> predicate;
        private final Set<Class<? extends Annotation>> requiredAnnotations = new HashSet<>();
        private final Set<Class<? extends Annotation>> optionalAnnotations = new HashSet<>();

        private Filter by(Predicate<EntryOrigin> predicate) {
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
        public Filter byType(Type... types) {
            return byType(type -> ArrayUtils.contains(types, type));
        }

        /**
         * Filters by a predicate based on the field type.
         *
         * @param typePredicate a predicate returning {@code true} for valid types
         * @return this builder
         */
        public Filter byType(Predicate<Type> typePredicate) {
            return by(origin -> typePredicate.test(origin.getType()));
        }

        /**
         * Filters by an annotation type. The annotation may be required or optional.
         *
         * @param annotation the annotation type
         * @param optional whether the annotation is optional
         * @return this builder
         */
        public Filter byAnnotation(Class<? extends Annotation> annotation, boolean optional) {
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
         * @see Filter#byAnnotation(Class, boolean)
         */
        public Filter byAnnotation(Class<? extends Annotation> annotation) {
            return byAnnotation(annotation, false);
        }

        /**
         * Filters by multiple annotation types. All annotations are required.
         *
         * @param annotations the annotation types
         * @return this builder
         */
        public Filter byAnnotation(Iterable<Class<? extends Annotation>> annotations) {
            for (Class<? extends Annotation> annotation : annotations) {
                byAnnotation(annotation);
            }
            return this;
        }

        private Predicate<EntryOrigin> build() {
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
            return predicate;
        }

    }

}
