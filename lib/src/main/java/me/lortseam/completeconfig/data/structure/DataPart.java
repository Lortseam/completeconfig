package me.lortseam.completeconfig.data.structure;

import me.lortseam.completeconfig.data.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.spongepowered.configurate.CommentedConfigurationNode;

public interface DataPart {

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

    @Environment(EnvType.CLIENT)
    TranslationKey getTranslation();

    @Environment(EnvType.CLIENT)
    default Text getText() {
        return getTranslation().toText();
    }

}
