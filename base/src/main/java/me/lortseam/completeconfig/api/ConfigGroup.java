package me.lortseam.completeconfig.api;

import com.google.common.base.CaseFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

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
     * Specifies a custom translation key for this group's name. If empty, the default key for this group will be used.
     *
     * @return a custom name translation key
     */
    @Environment(EnvType.CLIENT)
    default String getNameKey() {
        return null;
    }

    /**
     * Specifies a custom translation key for this group's description. If empty, the default key for this group will be
     * used.
     *
     * @return a custom description translation key
     */
    @Environment(EnvType.CLIENT)
    default String getDescriptionKey() {
        return null;
    }

    /**
     * Specifies a comment for this group. The comment will only be visible in the config file.
     *
     * @return a comment
     */
    default String getComment() {
        return null;
    }

    /**
     * Specifies the background for this group inside the config screen.
     *
     * @return the background identifier
     */
    @Environment(EnvType.CLIENT)
    default Identifier getBackground() {
        return null;
    }

}