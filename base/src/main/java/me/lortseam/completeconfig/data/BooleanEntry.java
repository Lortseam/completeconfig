package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BooleanEntry extends Entry<Boolean> {

    @Environment(EnvType.CLIENT)
    private Map<Boolean, TranslationKey> valueTranslations;
    @Environment(EnvType.CLIENT)
    @Getter
    private final boolean checkbox;

    public BooleanEntry(EntryOrigin origin) {
        super(origin);
        checkbox = origin.isAnnotationPresent(ConfigEntry.Checkbox.class);
    }

    @Environment(EnvType.CLIENT)
    private Map<Boolean, TranslationKey> getValueTranslations() {
        if (valueTranslations == null) {
            valueTranslations = new HashMap<>();
            Optional<ConfigEntry.Boolean> annotation = origin.getOptionalAnnotation(ConfigEntry.Boolean.class);
            if (annotation.isPresent()) {
                if (!annotation.get().trueKey().isBlank()) {
                    valueTranslations.put(true, getNameTranslation().root().append(annotation.get().trueKey()));
                }
                if (!annotation.get().falseKey().isBlank()) {
                    valueTranslations.put(false, getNameTranslation().root().append(annotation.get().falseKey()));
                }
            }
            TranslationKey defaultTrueTranslation = getNameTranslation().append("true");
            if (defaultTrueTranslation.exists()) {
                valueTranslations.putIfAbsent(true, defaultTrueTranslation);
            }
            TranslationKey defaultFalseTranslation = getNameTranslation().append("false");
            if (defaultFalseTranslation.exists()) {
                valueTranslations.putIfAbsent(false, defaultFalseTranslation);
            }
        }
        return valueTranslations;
    }

    @Override
    public Function<Boolean, Text> getValueTextSupplier() {
        if (getValueTranslations().isEmpty()) {
            return ScreenTexts::onOrOff;
        }
        return value -> getValueTranslations().get(value).toText();
    }

}
