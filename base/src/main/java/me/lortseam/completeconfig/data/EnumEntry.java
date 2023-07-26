package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EnumEntry<T extends Enum<?>> extends Entry<T> {

    @Environment(EnvType.CLIENT)
    private Map<T, TranslationKey> valueTranslations;

    public EnumEntry(EntryOrigin origin) {
        super(origin);
    }

    public final T[] getEnumConstants() {
        return (T[]) ((Class<? extends Enum<?>>) getTypeClass()).getEnumConstants();
    }

    @Override
    public Optional<Function<T, Text>> getValueFormatter() {
        if (valueTranslations == null) {
            valueTranslations = new HashMap<>();
            for (T value : getEnumConstants()) {
                var translation = getNameTranslation().append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value.name()));
                if (translation.exists()) {
                    valueTranslations.put(value, translation);
                }
            }
        }
        if (valueTranslations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value -> {
            var translation = valueTranslations.get(value);
            if (translation == null) {
                return Text.literal(value.toString());
            }
            return translation.toText();
        });
    }

}
