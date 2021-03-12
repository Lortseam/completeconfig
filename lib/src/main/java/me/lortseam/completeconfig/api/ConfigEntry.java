package me.lortseam.completeconfig.api;

import me.lortseam.completeconfig.data.EnumEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that a field should be resolved as config entry.
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
     * Specifies a comment to describe the purpose of this entry. The comment will only be visible in the config
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
     * Specifies whether the field should always get updated, regardless of the number of listeners.
     *
     * <p>If at least one listener exists in the field's class, by default, the field will not get modified when the
     * config is saved. Instead, all listeners will be called. Set this to true to disable that behaviour.
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

    /**
     * Applied to an entry of type Boolean.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Boolean {

        String trueTranslationKey() default "";

        String falseTranslationKey() default "";
        
    }

    /**
     * Applies bounds to an entry of type Integer.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BoundedInteger {

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
    @interface BoundedLong {

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
    @interface BoundedFloat {

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
    @interface BoundedDouble {

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

    /**
     * Applied to an entry of type Enum.
     *
     * <p>This annotation is optional.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Enum {

        /**
         * Specifies how the entry should be rendered.
         *
         * @return the desired {@link EnumEntry.DisplayType}
         */
        EnumEntry.DisplayType displayType() default EnumEntry.DisplayType.BUTTON;

    }

    /**
     * Applied to an entry which represents a color.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Color {

        /**
         * Specifies whether the color has an alpha value.
         *
         * @return whether the color has an alpha value
         */
        boolean alphaMode();

    }

}