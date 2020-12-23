package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.data.Extras;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface GuiProviderPredicate<T> {

    boolean test(Field field, Extras<?> extras);

    default GuiProviderPredicate<T> and(GuiProviderPredicate<T> other) {
        Objects.requireNonNull(other);
        return (field, extras) -> test(field, extras) && other.test(field, extras);
    }

}
