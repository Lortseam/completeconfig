package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

/**
 * A container of config entries which has a unique identifier additionally.
 */
public interface ConfigCategory extends ConfigEntryContainer {

    /**
     * Used to identify this category. Defaults to the class name.
     *
     * <p>Override this method to set a custom ID.
     *
     * @return the ID of this category
     */
    default String getConfigCategoryID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

}