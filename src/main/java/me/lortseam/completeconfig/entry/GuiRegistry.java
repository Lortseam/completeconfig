package me.lortseam.completeconfig.entry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class GuiRegistry {

    private final LinkedHashMap<GuiProviderPredicate, GuiProvider> guiProviders = new LinkedHashMap<>();

    public void registerProvider(GuiProviderPredicate predicate, GuiProvider provider) {
        guiProviders.put(predicate, provider);
    }

    public <T> void registerTypeProvider(Class<T> type, GuiProvider<T> provider) {
        registerProvider((field, fieldType, extras) -> fieldType == type, provider);
    }

    public <T> void registerBoundedTypeProvider(Class<T> type, GuiProvider<T> provider) {
        registerProvider((field, fieldType, extras) -> fieldType == type && extras.getBounds() != null, provider);
    }

    public <T> GuiProvider<T> getProvider(Entry<T> entry) {
        GuiProvider<T> guiProvider = null;
        for (Map.Entry<GuiProviderPredicate, GuiProvider> mapEntry : guiProviders.entrySet()) {
            Field field = entry.getField();
            if (mapEntry.getKey().test(field, field.getType(), entry.getExtras())) {
                guiProvider = mapEntry.getValue();
            }
        }
        return guiProvider;
    }

}