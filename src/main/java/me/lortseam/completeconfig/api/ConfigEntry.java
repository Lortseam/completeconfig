package me.lortseam.completeconfig.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.EnumEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that a field is an entry inside the mod's config. Only required if
 * the {@link ConfigEntryContainer} is not a POJO class.
 *
 * <p>This annotation also contains various options to modify the entry.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntry {

    /**
     * Specifies the ID of this entry. If empty, the field name will be used by default.
     *
     * @return the ID
     */
    String value() default "";

    /**
     * Specifies a comment which describes the purpose of this entry. The comment will only be visible in the config
     * save file.
     *
     * <p>If blank, no comment will be applied to the entry.
     *
     * @return a comment
     */
    String comment() default "";

    /**
     * Specifies a custom translation key for this entry. If empty, the default key for this entry will be used.
     *
     * @return a custom translation key
     */
    String translationKey() default "";

    /**
     * Specifies one or more custom translation keys for this entry's tooltip, declared line by line. If empty, the
     * default single-line or multi-line keys will be used, depending on which are defined in the language file(s).
     *
     * @return an array of custom tooltip translation keys
     */
    String[] tooltipTranslationKeys() default {};

    /**
     * Specifies if the entry's field should get updated while at least one listener exists in the field's class.
     *
     * <p>In that case, by default, the entry's field will not get modified when the config is saved, but all listeners
     * will be called. Set this to true to always update the field when saving.
     *
     * @return true if the field should always get updated, else false
     */
    boolean forceUpdate() default false;

    /**
     * Specifies whether the game needs to be restarted after modifying the entry.
     *
     * @return whether the game needs to be restarted after modifying the entry
     */
    boolean requiresRestart() default false;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Bounded {

        /**
         * Applies bounds to an entry of type Integer.
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Integer {

            /**
             * The minimum bound.
             *
             * @return the minimum bound
             */
            int min() default java.lang.Integer.MIN_VALUE;

            /**
             * The maximum bound.
             *
             * @return the maximum bound
             */
            int max() default java.lang.Integer.MAX_VALUE;

            /**
             * Specifies whether the entry should be rendered as slider.
             *
             * @return whether the entry should be rendered as slider
             */
            boolean slider() default true;

        }

        /**
         * Applies bounds to an entry of type Long.
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Long {

            /**
             * The minimum bound.
             *
             * @return the minimum bound
             */
            long min() default java.lang.Long.MIN_VALUE;

            /**
             * The maximum bound.
             *
             * @return the maximum bound
             */
            long max() default java.lang.Long.MAX_VALUE;

            /**
             * Specifies whether the entry should be rendered as slider.
             *
             * @return whether the entry should be rendered as slider
             */
            boolean slider() default true;

        }

        /**
         * Applies bounds to an entry of type Float.
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Float {

            /**
             * The minimum bound.
             *
             * @return the minimum bound
             */
            float min() default -java.lang.Float.MAX_VALUE;

            /**
             * The maximum bound.
             *
             * @return the maximum bound
             */
            float max() default java.lang.Float.MAX_VALUE;

        }

        /**
         * Applies bounds to an entry of type Double.
         */
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Double {

            /**
             * The minimum bound.
             *
             * @return the minimum bound
             */
            double min() default -java.lang.Double.MAX_VALUE;

            /**
             * The maximum bound.
             *
             * @return the maximum bound
             */
            double max() default java.lang.Double.MAX_VALUE;

        }

    }

    /**
     * Applied to an entry of type Enum to change the render behaviour.
     *
     * <p>This annotation is optional.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Enum {

        /**
         * Specifies how the entry should be rendered.
         *
         * @return the desired {@link me.lortseam.completeconfig.data.EnumEntry.DisplayType}
         */
        EnumEntry.DisplayType displayType() default EnumEntry.DisplayType.BUTTON;

    }

    /**
     * Specifies that the annotated field is a color entry.
     *
     * <p>This annotation is optional for known color types, such as {@link net.minecraft.text.TextColor}, but is
     * required for unknown types.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Color {

        /**
         * Specifies whether the color has an alpha value.
         *
         * <p>This element is only evaluated by GUI providers. It will not restrict the type of this entry.
         *
         * @return whether the color has an alpha value
         */
        boolean alphaMode();

    }

    /**
     * Can be applied to a field inside a POJO {@link ConfigEntryContainer} class to declare that that field should not
     * be considered to be a config entry.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }

}
