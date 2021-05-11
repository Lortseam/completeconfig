package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class BooleanEntry extends Entry<Boolean> {

    private final Function<Boolean, TranslationKey> valueTranslationSupplier;

    BooleanEntry(EntryOrigin origin) {
        super(origin);
        valueTranslationSupplier = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? origin.getOptionalAnnotation(ConfigEntry.Boolean.class).map(annotation -> {
            if (StringUtils.isBlank(annotation.trueTranslationKey()) && StringUtils.isBlank(annotation.falseTranslationKey())) {
                return null;
            } else {
                return (Function<Boolean, TranslationKey>) value -> {
                    String key = value ? annotation.trueTranslationKey() : annotation.falseTranslationKey();
                    if (!StringUtils.isBlank(key)) {
                        return translation.root().append(key);
                    }
                    return translation.append(value ? "true" : "false");
                };
            }
        }).orElse(null) : null;
    }

    @Environment(EnvType.CLIENT)
    public Function<Boolean, Text> getValueTextSupplier() {
        if (valueTranslationSupplier != null) {
            return bool -> valueTranslationSupplier.apply(bool).toText();
        }
        TranslationKey defaultTrueTranslation = translation.append("true");
        TranslationKey defaultFalseTranslation = translation.append("false");
        if (defaultTrueTranslation.exists() || defaultFalseTranslation.exists()) {
            return bool -> (bool ? defaultTrueTranslation : defaultFalseTranslation).toText();
        }
        return null;
    }

}
