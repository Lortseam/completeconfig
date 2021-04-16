package me.lortseam.completeconfig.extensions.clothbasicmath;

import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

final class ColorSerializer extends ScalarSerializer<Color> {

    static final ColorSerializer INSTANCE = new ColorSerializer();

    private ColorSerializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(Type type, Object obj) throws SerializationException {
        if (obj instanceof Integer) {
            return Color.ofTransparent((Integer) obj);
        }
        throw new CoercionFailedException(type, obj, Color.class.getSimpleName());
    }

    @Override
    protected Object serialize(Color item, Predicate<Class<?>> typeSupported) {
        return item.getColor();
    }

}
