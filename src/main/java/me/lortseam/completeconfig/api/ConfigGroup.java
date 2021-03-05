package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A group of config entries. Inside its parent node, every group is uniquely defined by an identifier.
 */
public interface ConfigGroup extends ConfigContainer {

    /**
     * Used to identify this group. Defaults to the class name.
     *
     * @return the ID of this group
     */
    default String getGroupID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

    default String[] getTooltipTranslationKeys() {
        return null;
    }

    default String getComment() {
        return null;
    }

}