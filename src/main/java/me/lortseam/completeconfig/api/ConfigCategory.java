package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A container of config entries which is rendered as category or subcategory in the config GUI.
 */
public interface ConfigCategory extends ConfigEntryContainer {

    /**
     * Used to get the ID of the config category created with this class. Defaults to the class name.
     * Override this method to set a custom ID.
     * @return the ID of this config category
     */
    default String getConfigCategoryID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

}