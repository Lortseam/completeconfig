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

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface TranslationKey {

        String value();

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Integer {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bound {

            int min();

            int max();

        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Long {

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Bound {

            long min();

            long max();

        }

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }

}
