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

    boolean forceUpdate() default false;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Integer {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bounded {

            int min() default java.lang.Integer.MIN_VALUE;

            int max() default java.lang.Integer.MAX_VALUE;

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Long {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bounded {

            long min() default java.lang.Long.MIN_VALUE;

            long max() default java.lang.Long.MAX_VALUE;

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Float {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bounded {

            float min() default -java.lang.Float.MAX_VALUE;

            float max() default java.lang.Float.MAX_VALUE;

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Double {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bounded {

            double min() default -java.lang.Double.MAX_VALUE;

            double max() default java.lang.Double.MAX_VALUE;

        }

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }

}
