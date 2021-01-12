package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Field;

public class ColorEntry<T> extends Entry<T> {

    public static final ScalarSerializer<TextColor> TEXT_COLOR_SERIALIZER = TypeSerializer.of(TextColor.class, (item, typeSupported) -> {
        return item.getRgb();
    }, value -> {
        if (value instanceof Integer) {
            return TextColor.fromRgb((int) value);
        }
        throw new CoercionFailedException(value, "TextColor");
    });

    @Getter
    private final boolean alphaMode;

    protected ColorEntry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation, boolean alphaMode) {
        super(field, parentObject, parentTranslation);
        this.alphaMode = alphaMode;
    }

}
