package me.lortseam.completeconfig.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntry {

    String customTranslationKey() default "";

    String[] customTooltipKeys() default {};

    boolean forceUpdate() default false;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Bounded {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Integer {

            int min() default java.lang.Integer.MIN_VALUE;

            int max() default java.lang.Integer.MAX_VALUE;

            boolean slider() default true;

        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Long {

            long min() default java.lang.Long.MIN_VALUE;

            long max() default java.lang.Long.MAX_VALUE;

            boolean slider() default true;

        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Float {

            float min() default -java.lang.Float.MAX_VALUE;

            float max() default java.lang.Float.MAX_VALUE;

        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Double {

            double min() default -java.lang.Double.MAX_VALUE;

            double max() default java.lang.Double.MAX_VALUE;

        }

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }

}
