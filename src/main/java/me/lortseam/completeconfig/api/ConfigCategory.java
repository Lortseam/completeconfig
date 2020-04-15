package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;
import org.jetbrains.annotations.NotNull;

public interface ConfigCategory extends ConfigEntryContainer {

    default @NotNull String getConfigCategoryID() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    }

}