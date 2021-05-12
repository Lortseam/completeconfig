package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

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
                if (!StringUtils.isBlank(annotation.get().trueTranslationKey())) {
                    valueTranslations.put(true, getTranslation().root().append(annotation.get().trueTranslationKey()));
                }
                if (!StringUtils.isBlank(annotation.get().falseTranslationKey())) {
                    valueTranslations.put(false, getTranslation().root().append(annotation.get().falseTranslationKey()));
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

    @Environment(EnvType.CLIENT)
    public Function<Boolean, Text> getValueTextSupplier() {
        if (getValueTranslations().isEmpty()) {
            return null;
        }
        return value -> getValueTranslations().get(value).toText();
    }

}
