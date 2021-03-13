package me.lortseam.completeconfig.io;

import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

final class TextColorSerializer extends ScalarSerializer<TextColor> {

    static final TextColorSerializer INSTANCE = new TextColorSerializer();

    private TextColorSerializer() {
        super(TextColor.class);
    }

    @Override
    public TextColor deserialize(Type type, Object obj) throws SerializationException {
        if (obj instanceof Integer) {
            return TextColor.fromRgb((Integer) obj);
        }
        throw new CoercionFailedException(type, obj, TextColor.class.getSimpleName());
    }

    @Override
    protected Object serialize(TextColor item, Predicate<Class<?>> typeSupported) {
        return item.getRgb();
    }

}
