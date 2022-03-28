package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

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

    default String getDescriptionKey() {
        return null;
    }

    /**
     * Specifies a comment which describes this group. The comment will only be visible in the config file.
     *
     * @return a comment
     */
    default String getComment() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    default Identifier getBackground() {
        return null;
    }

}