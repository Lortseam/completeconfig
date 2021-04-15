package me.lortseam.completeconfig.gui.cloth;

import lombok.AccessLevel;
import lombok.Getter;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class GuiProviderRegistration<E extends Entry<?>> {

    private final Predicate<Entry<?>> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final GuiProvider<? extends E> provider;

    public GuiProviderRegistration(GuiProvider<? extends E> provider, Predicate<Entry<?>> predicate, Type... types) {
        this.predicate = entry -> {
            if (types.length > 0 && !ArrayUtils.contains(types, entry.getType())) return false;
            return predicate.test(entry);
        };
        this.provider = provider;
    }

    public GuiProviderRegistration(GuiProvider<? extends E> provider, Type... types) {
        this(provider, entry -> true, Arrays.requireNonEmpty(types, "types"));
    }

    public GuiProviderRegistration(Class<E> entryType, GuiProvider<? extends E> provider, Predicate<E> predicate, Type... types) {
        this(provider, entry -> entry.getClass() == entryType && predicate.test((E) entry), types);
    }

    public GuiProviderRegistration(Class<E> entryType, GuiProvider<? extends E> provider, Type... types) {
        this(entryType, provider, entry -> true, types);
    }

    boolean test(Entry<?> entry) {
        return predicate.test(entry);
    }

}
