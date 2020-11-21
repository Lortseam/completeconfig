package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A container of config entries which has an identifier additionally.
 */
public interface ConfigCategory extends ConfigEntryContainer {

    /**
     * Used to modify the ID of this category. Defaults to the class name. Override this method to set a custom ID.
     *
     * @return the ID of this config category
     */
    default String getConfigCategoryID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

}