package me.lortseam.completeconfig.data.structure.client;

import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public interface Translatable {

    @Environment(EnvType.CLIENT)
    TranslationKey getNameTranslation();

    @Environment(EnvType.CLIENT)
    default Text getName() {
        return getNameTranslation().toText();
    }

}
