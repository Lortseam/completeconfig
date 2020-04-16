package me.lortseam.completeconfig.entry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class GuiRegistry {

    private final LinkedHashMap<Predicate<Field>, Entry.GuiProvider> entryGuiProviders = new LinkedHashMap<>();
    private final LinkedHashMap<Predicate<Field>, BoundedEntry.GuiProvider> boundedEntryGuiProviders = new LinkedHashMap<>();

    public void registerProvider(Predicate<Field> predicate, Entry.GuiProvider provider) {
        entryGuiProviders.put(predicate, provider);
    }

    public <T> void registerTypeProvider(Class<T> type, Entry.GuiProvider<T> provider) {
        registerProvider((field) -> field.getDeclaringClass() == type, provider);
    }

    public void registerBoundedProvider(Predicate<Field> predicate, BoundedEntry.GuiProvider provider) {
        boundedEntryGuiProviders.put(predicate, provider);
    }

    public <T> void registerBoundedTypeProvider(Class<T> type, BoundedEntry.GuiProvider<T> provider) {
        registerBoundedProvider((field) -> field.getDeclaringClass() == type, provider);
    }

    private <T> T getHighestPriorityProvider(LinkedHashMap<Predicate<Field>, T> providers, Entry entry) {
        T guiProvider = null;
        for (Map.Entry<Predicate<Field>, T> mapEntry : providers.entrySet()) {
            if (mapEntry.getKey().test(entry.getField())) {
                guiProvider = mapEntry.getValue();
            }
        }
        return guiProvider;
    }

    public Entry.GuiProvider getProvider(Entry entry) {
        return getHighestPriorityProvider(entryGuiProviders, entry);
    }

    public BoundedEntry.GuiProvider getBoundedProvider(BoundedEntry entry) {
        return getHighestPriorityProvider(boundedEntryGuiProviders, entry);
    }

    public boolean hasProvider(Entry entry) {
        if (entry instanceof BoundedEntry) {
            return getBoundedProvider((BoundedEntry) entry) != null;
        } else {
            return getProvider(entry) != null;
        }
    }

}
