package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    @Environment(EnvType.CLIENT)
    private TranslationKey valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max) {
        super(origin, min, max);
    }

    @Environment(EnvType.CLIENT)
    public Function<T, Text> getValueTextSupplier() {
        if (valueTranslation == null) {
            ConfigEntry.Slider slider = origin.getAnnotation(ConfigEntry.Slider.class);
            if (!StringUtils.isBlank(slider.valueTranslationKey())) {
                valueTranslation = getTranslation().root().append(slider.valueTranslationKey());
            } else {
                valueTranslation = getTranslation().append("value");
            }
        }
        if (valueTranslation.exists()) {
            return value -> valueTranslation.toText(value);
        }
        return null;
    }

}
