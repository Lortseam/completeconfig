package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class BooleanEntry extends Entry<Boolean> {

    private final Function<Boolean, TranslationIdentifier> valueTranslationSupplier;

    BooleanEntry(EntryOrigin origin) {
        super(origin);
        valueTranslationSupplier = origin.getOptionalAnnotation(ConfigEntry.Boolean.class).map(annotation -> {
            if (StringUtils.isBlank(annotation.trueTranslationKey()) && StringUtils.isBlank(annotation.falseTranslationKey())) {
                return null;
            } else {
                return (Function<Boolean, TranslationIdentifier>) value -> {
                    String key = value ? annotation.trueTranslationKey() : annotation.falseTranslationKey();
                    if (!StringUtils.isBlank(key)) {
                        return getTranslation().root().append(key);
                    }
                    return getTranslation().append(value ? "true" : "false");
                };
            }
        }).orElse(null);
    }

    public Function<Boolean, Text> getValueTextSupplier() {
        if (valueTranslationSupplier != null) {
            return bool -> valueTranslationSupplier.apply(bool).toText();
        }
        TranslationIdentifier defaultTrueTranslation = getTranslation().append("true");
        TranslationIdentifier defaultFalseTranslation = getTranslation().append("false");
        if (defaultTrueTranslation.exists() || defaultFalseTranslation.exists()) {
            return bool -> (bool ? defaultTrueTranslation : defaultFalseTranslation).toText();
        }
        return null;
    }

}
