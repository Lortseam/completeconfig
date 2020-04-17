package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;

public interface ConfigCategory extends ConfigEntryContainer {

    default String getConfigCategoryID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

}