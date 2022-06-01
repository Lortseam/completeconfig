package me.lortseam.completeconfig.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * A GUI provider is used to generate a GUI element for an {@link Entry}. This class stores a predicate, which an entry
 * has to fulfill, and an {@link EntryBuilder}, which performs the actual generation.
 */
@Environment(EnvType.CLIENT)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiProvider<T> {

    /**
     * Creates a GUI provider for a custom entry type, filtered by a predicate and value types.
     *
     * @param entryType the custom entry class
     * @param builder the entry builder
     * @param predicate a predicate which the entry has to fulfill
     * @param types the valid value types
     * @param <E> the custom entry type
     * @return the created GUI provider
     */
    public static <T, E extends Entry<?>, B extends EntryBuilder<? extends E, T>> GuiProvider<T> create(Class<E> entryType, B builder, Predicate<E> predicate, Type... types) {
        return new GuiProvider<>(entry -> {
            if (entry.getClass() != (entryType != null ? entryType : Entry.class)) return false;
            if (types.length > 0 && !ArrayUtils.contains(types, entry.getType())) return false;
            return predicate.test((E) entry);
        }, builder);
    }

    /**
     * Creates a GUI provider for a custom entry type, filtered by value types.
     *
     * @param entryType the custom entry class
     * @param builder the entry builder
     * @param types the valid value types
     * @param <E> the custom entry type
     * @return the created GUI provider
     */
    public static <T, E extends Entry<?>, B extends EntryBuilder<? extends E, T>> GuiProvider<T> create(Class<E> entryType, B builder, Type... types) {
        return create(entryType, builder, entry -> true, types);
    }

    /**
     * Creates a GUI provider for the default entry type, filtered by a predicate and value types.
     *
     * @param builder the entry builder
     * @param predicate a predicate which the entry has to fulfill
     * @param types the valid value types
     * @return the created GUI provider
     */
    public static <T, E extends Entry<?>, B extends EntryBuilder<? extends E, T>> GuiProvider<T> create(B builder, Predicate<E> predicate, Type... types) {
        return create(null, builder, predicate, types);
    }

    /**
     * Creates a GUI provider for the default entry type, filtered by value types.
     *
     * @param builder the entry builder
     * @param types the valid value types
     * @return the created GUI provider
     */
    public static <T, E extends Entry<?>, B extends EntryBuilder<? extends E, T>> GuiProvider<T> create(B builder, Type... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        return create(builder, entry -> true, types);
    }

    private final Predicate<Entry<?>> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final EntryBuilder<?, T> builder;

    boolean test(Entry<?> entry) {
        return predicate.test(entry);
    }

}
