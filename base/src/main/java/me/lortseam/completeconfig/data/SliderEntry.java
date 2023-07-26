package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.text.TranslationKey;
import me.lortseam.completeconfig.util.NumberUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Function;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    private final T interval;
    @Environment(EnvType.CLIENT)
    private TranslationKey valueTranslation;

    public SliderEntry(EntryOrigin origin, T min, T max, T interval) {
        super(origin, min, max);
        this.interval = interval;
    }

    @Override
    public Optional<Function<T, Text>> getValueFormatter() {
        if (valueTranslation == null) {
            ConfigEntry.Slider slider = origin.getAnnotation(ConfigEntry.Slider.class);
            if (!slider.valueKey().isBlank()) {
                valueTranslation = origin.getRoot().getBaseTranslation().append(slider.valueKey());
            } else {
                valueTranslation = getNameTranslation().append("value");
            }
        }
        if (valueTranslation.exists()) {
            return Optional.of(value -> valueTranslation.toText(value));
        }
        return Optional.empty();
    }

    public Optional<T> getInterval() {
        return NumberUtils.isPositive(interval) ? Optional.of(interval) : Optional.empty();
    }

}
