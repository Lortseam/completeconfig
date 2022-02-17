package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class BooleanEntry extends Entry<Boolean> {

    @Environment(EnvType.CLIENT)
    private Map<Boolean, TranslationKey> valueTranslations;

    public BooleanEntry(EntryOrigin origin) {
        super(origin);
    }

    @Environment(EnvType.CLIENT)
    private Map<Boolean, TranslationKey> getValueTranslations() {
        if (valueTranslations == null) {
            valueTranslations = new HashMap<>();
            Optional<ConfigEntry.Boolean> annotation = origin.getOptionalAnnotation(ConfigEntry.Boolean.class);
            if (annotation.isPresent()) {
                if (!annotation.get().trueKey().isBlank()) {
                    valueTranslations.put(true, getTranslation().root().append(annotation.get().trueKey()));
                }
                if (!annotation.get().falseKey().isBlank()) {
                    valueTranslations.put(false, getTranslation().root().append(annotation.get().falseKey()));
                }
            }
            TranslationKey defaultTrueTranslation = getTranslation().append("true");
            if (defaultTrueTranslation.exists()) {
                valueTranslations.putIfAbsent(true, defaultTrueTranslation);
            }
            TranslationKey defaultFalseTranslation = getTranslation().append("false");
            if (defaultFalseTranslation.exists()) {
                valueTranslations.putIfAbsent(false, defaultFalseTranslation);
            }
        }
        return valueTranslations;
    }

    @Override
    public Function<Boolean, Text> getValueTextSupplier() {
        if (getValueTranslations().isEmpty()) {
            // TODO: Create default translations for true and false und use these instead
            return super.getValueTextSupplier();
        }
        return value -> getValueTranslations().get(value).toText();
    }

}
