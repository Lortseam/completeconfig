package me.lortseam.completeconfig.data.client;

import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Optional;

public interface TooltipSupplier {

    @Environment(EnvType.CLIENT)
    TranslationKey[] getTooltipTranslation();

    @Environment(EnvType.CLIENT)
    default Optional<Text[]> getTooltip() {
        if (getTooltipTranslation().length == 0) return Optional.empty();
        return Optional.of(Arrays.stream(getTooltipTranslation()).map(TranslationKey::toText).toArray(Text[]::new));
    }

}
