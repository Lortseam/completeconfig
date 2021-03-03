package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Optional;

@Log4j2
public class Collection extends Node {

    private final TranslationIdentifier[] customTooltipTranslation;

    Collection(TranslationIdentifier translation, String[] customTooltipTranslationKeys) {
        super(translation);
        customTooltipTranslation = ArrayUtils.isNotEmpty(customTooltipTranslationKeys) ? Arrays.stream(customTooltipTranslationKeys).map(key -> translation.root().appendKey(key)).toArray(TranslationIdentifier[]::new) : null;
    }

    public Optional<Text[]> getTooltipTranslation() {
        return (customTooltipTranslation != null ? Optional.of(customTooltipTranslation) : translation.appendTooltip()).map(lines -> {
            return Arrays.stream(lines).map(TranslationIdentifier::toText).toArray(Text[]::new);
        });
    }

}