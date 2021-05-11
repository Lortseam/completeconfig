package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A group of config entries.
 */
public interface ConfigGroup extends ConfigContainer {

    /**
     * Used to identify this group. Defaults to the class name.
     *
     * @return the ID of this group
     */
    default String getId() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

    /**
     * Used to specify one or more custom translation keys for this group's tooltip, declared line by line. If empty,
     * the default single-line or multi-line keys will be used, depending on which are defined in the language file(s).
     *
     * @return an array of custom tooltip translation keys
     */
    default String[] getTooltipTranslationKeys() {
        return null;
    }

    /**
     * Used to specify a comment to describe this group. The comment will only be visible in the config save file.
     *
     * @return a comment
     */
    default String getComment() {
        return null;
    }

}