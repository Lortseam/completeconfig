package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.text.TranslationKey;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    private final TranslationKey valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max) {
        super(origin, min, max);
        ConfigEntry.Slider slider = origin.getAnnotation(ConfigEntry.Slider.class);
        if (!StringUtils.isBlank(slider.valueTranslationKey())) {
            valueTranslation = translation.root().append(slider.valueTranslationKey());
        } else {
            valueTranslation = null;
        }
    }

    public Function<T, Text> getValueTextSupplier() {
        if (valueTranslation != null) {
            return valueTranslation::toText;
        }
        TranslationKey defaultValueTranslation = translation.append("value");
        if (defaultValueTranslation.exists()) {
            return defaultValueTranslation::toText;
        }
        return null;
    }

}
