package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class ColorEntry<T> extends Entry<T> {

    @Getter
    private final boolean alphaMode;

    public ColorEntry(EntryOrigin origin, boolean alphaMode) {
        super(origin);
        this.alphaMode = alphaMode;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Serializers {

        public static final ScalarSerializer<TextColor> TEXT_COLOR = TypeSerializer.of(TextColor.class, (item, typeSupported) -> {
            return item.getRgb();
        }, value -> {
            if (value instanceof Integer) {
                return TextColor.fromRgb((int) value);
            }
            throw new CoercionFailedException(value, "TextColor");
        });

    }

}
