package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A group of config entries. Every group has a unique identifier inside its parent node.
 */
public interface ConfigGroup extends ConfigEntryContainer {

    /**
     * Used to identify this group. Defaults to the class name.
     *
     * <p>Override this method to set a custom ID.
     *
     * @return the ID of this group
     */
    default String getConfigGroupID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

    default String[] getCustomTooltipKeys() {
        return null;
    }

}