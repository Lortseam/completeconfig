package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    private final TranslationIdentifier valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max, ConfigEntry.Slider slider) {
        super(origin, min, max);
        if (!StringUtils.isBlank(slider.valueTranslationKey())) {
            valueTranslation = getTranslation().root().append(slider.valueTranslationKey());
        } else {
            valueTranslation = null;
        }
    }

    public Function<T, Text> getValueTextSupplier() {
        if (valueTranslation != null) {
            return valueTranslation::toText;
        }
        TranslationIdentifier defaultValueTranslation = getTranslation().append("value");
        if (defaultValueTranslation.exists()) {
            return defaultValueTranslation::toText;
        }
        return null;
    }

}
