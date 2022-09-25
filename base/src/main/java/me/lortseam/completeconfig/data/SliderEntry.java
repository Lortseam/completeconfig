package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    @Environment(EnvType.CLIENT)
    private TranslationKey valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max) {
        super(origin, min, max);
    }

    @Override
    public Function<T, Text> getValueFormatter() {
        if (valueTranslation == null) {
            ConfigEntry.Slider slider = origin.getAnnotation(ConfigEntry.Slider.class);
            if (!slider.valueKey().isBlank()) {
                valueTranslation = origin.getRoot().getBaseTranslation().append(slider.valueKey());
            } else {
                valueTranslation = getNameTranslation().append("value");
            }
        }
        if (valueTranslation.exists()) {
            return value -> valueTranslation.toText(value);
        }
        return super.getValueFormatter();
    }

}
