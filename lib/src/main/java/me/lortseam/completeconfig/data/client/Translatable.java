package me.lortseam.completeconfig.data.client;

import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public interface Translatable {

    @Environment(EnvType.CLIENT)
    TranslationKey getTranslation();

    @Environment(EnvType.CLIENT)
    default Text getText() {
        return getTranslation().toText();
    }

}
