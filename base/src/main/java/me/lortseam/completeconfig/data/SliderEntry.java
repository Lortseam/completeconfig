package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    @Getter
    private final T interval;
    @Environment(EnvType.CLIENT)
    private TranslationKey valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max, T interval) {
        super(origin, min, max);
        this.interval = interval;
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
